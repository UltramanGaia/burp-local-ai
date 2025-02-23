package cn.ultramangaia.burp.hook;

import burp.api.montoya.ai.Ai;
import burp.api.montoya.ai.chat.Prompt;
import cn.ultramangaia.burp.BurpLocalAIExtension;
import cn.ultramangaia.burp.impl.AISpyImpl;
import cn.ultramangaia.java.jvmhelper.VmTool;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.Descriptor;

import java.gaia.AbstractSpy;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static burp.api.montoya.internal.ObjectFactoryLocator.FACTORY;
import static cn.ultramangaia.burp.BurpLocalAIExtension.api;
import static cn.ultramangaia.burp.BurpLocalAIExtension.logging;

public class HookManager {

    private final Map<Class<?>, byte[]> classBytesBackup = new HashMap<>();

    public HookManager() {
        // spy
        initSpy();
    }

    private void initSpy() {
        try {
            // 将Spy添加到BootstrapClassLoader
            ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
            Class<?> spyClass = null;
            if (parent != null) {
                try {
                    spyClass = parent.loadClass("java.gaia.AISpy");
                } catch (Throwable e) {
                    // ignore
                }
            }
            if (spyClass == null) {
                File file = File.createTempFile("spy", ".jar");
                InputStream link = BurpLocalAIExtension.class.getResourceAsStream("/spy.jar");
                if (link != null) {
                    Files.copy(link, file.getAbsoluteFile().toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    file.deleteOnExit();
                    VmTool.appendToBootstrapClassLoaderSearch(new JarFile(file));
                }

                try {
                    spyClass = parent.loadClass("java.gaia.AISpy");
                } catch (Throwable e) {
                    // ignore
                }
            }
            if (spyClass != null) {
                AISpyImpl spyImpl = AISpyImpl.getInstance();
                Method method = spyClass.getMethod("setSpy", AbstractSpy.class);
                method.invoke(null, spyImpl);
            }
        }catch (Throwable throwable){
            logging.logToError("Init Spy Failed: " + throwable);
        }
    }
    
    public void hook(){
        logging.logToOutput("Hooking...");
        try {


            Ai ai = api.ai();

            ClassPool pool = ClassPool.getDefault();
            ClassLoader loader = this.getClass().getClassLoader();
            if(loader!=null) {
                pool.insertClassPath(new LoaderClassPath(loader));
            }

            // Hook Prompt实现类
            Prompt prompt = ai.prompt();
            Class<?> promptClass = prompt.getClass();
            hookClassMethodBody(pool, promptClass, "execute", "{return (burp.api.montoya.ai.chat.PromptResponse) java.gaia.AISpy.handle(\"%s\", this, $args);}");

            // Hook AI 实现类
            Class<?> aiClass = ai.getClass();
            hookClassMethodBody(pool, aiClass, "prompt", "{return (burp.api.montoya.ai.chat.Prompt) java.gaia.AISpy.handle(\"%s\", this, $args);}");


            // Hook FACTORY
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(FACTORY);
            Class<?> factoryClass = invocationHandler.getClass();
            String code = """
                    {
                        if($2.getName().equals("promptOptions")){
                            return (burp.api.montoya.ai.chat.PromptOptions) java.gaia.AISpy.handle("promptOptions()", this, $args);
                        }
                        if($2.getName().equals("systemMessage")){
                            return (burp.api.montoya.ai.chat.Message) java.gaia.AISpy.handle("systemMessage(Ljava/lang/String;)", this, $args);
                        }
                        if($2.getName().equals("userMessage")){
                            return (burp.api.montoya.ai.chat.Message) java.gaia.AISpy.handle("userMessage(Ljava/lang/String;)", this, $args);
                        }
                        if($2.getName().equals("assistantMessage")){
                            return (burp.api.montoya.ai.chat.Message) java.gaia.AISpy.handle("assistantMessage(Ljava/lang/String;)", this, $args);
                        }
                    }
                    """;
            hookClassMethodBefore(pool, factoryClass, "invoke", code);
        }catch (Throwable throwable){
            logging.logToError("Error initializing:" + throwable);
            throw new RuntimeException(throwable);
        }
        logging.logToOutput("Hooked.");
    }
    
    public void unhook(){
        logging.logToOutput("Unhooking...");
        for(Map.Entry<Class<?>, byte[]> entry : classBytesBackup.entrySet()){
            try {
                VmTool.redefineClasses(entry.getKey(), entry.getValue());
            }catch (Throwable throwable){
                logging.logToError("Error extensionUnloaded:" + throwable);
                throw new RuntimeException(throwable);
            }
        }
        classBytesBackup.clear();
        logging.logToOutput("Unhooked.");

    }



    private void hookClassMethodBody(ClassPool pool, Class<?> clazz, String methodName, String code) {
        try {
            ClassLoader loader = clazz.getClassLoader();
            if (loader != null) {
                pool.insertClassPath(new LoaderClassPath(loader));
            }

            CtClass ctClass = pool.get(clazz.getName());
            byte[] backup = ctClass.toBytecode();
            // 使用toBytecode方法后会冻结，需要解冻
            ctClass.defrost();
            for (CtMethod method : ctClass.getDeclaredMethods(methodName)) {
                String methodInfo = method.getName() + Descriptor.getParamDescriptor(method.getSignature());
                logging.logToOutput("Hooking " + methodInfo);
                method.setBody(String.format(code, methodInfo));
            }
            ctClass.writeFile("C:\\tmp\\tmp\\classes\\");
            VmTool.redefineClasses(clazz, ctClass.toBytecode());
            classBytesBackup.put(clazz, backup);
        }catch(Throwable throwable){
            logging.logToError("Error hookClassMethodBody:" + throwable);
            throw new RuntimeException(throwable);
        }
    }

    private void hookClassMethodBefore(ClassPool pool, Class<?> clazz, String methodName, String code) {
        try {
            ClassLoader loader = clazz.getClassLoader();
            if (loader != null) {
                pool.insertClassPath(new LoaderClassPath(loader));
            }

            CtClass ctClass = pool.get(clazz.getName());
            byte[] backup = ctClass.toBytecode();
            // 使用toBytecode方法后会冻结，需要解冻
            ctClass.defrost();
            for (CtMethod method : ctClass.getDeclaredMethods(methodName)) {
                String methodInfo = method.getName() + Descriptor.getParamDescriptor(method.getSignature());
                logging.logToOutput("Hooking " + methodInfo);
                method.insertBefore(code);
            }
            ctClass.writeFile("C:\\tmp\\tmp\\classes\\");
            VmTool.redefineClasses(clazz, ctClass.toBytecode());
            classBytesBackup.put(clazz, backup);
        }catch(Throwable throwable){
            logging.logToError("Error hookClassMethodBefore:" + throwable);
            throw new RuntimeException(throwable);
        }
    }
    
    
    
}
