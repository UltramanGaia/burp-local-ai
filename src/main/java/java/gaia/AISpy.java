package java.gaia;

public class AISpy {

    private static volatile AbstractSpy spyInstance = null;

    public static AbstractSpy getSpy() {
        return spyInstance;
    }

    public static void setSpy(AbstractSpy spy) {
        spyInstance = spy;
    }

    public static Object handle(String methodInfo, Object target, Object[] args){
        return spyInstance.handle(methodInfo, target, args);
    }
}
