package cn.ultramangaia.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.PersistedObject;
import cn.ultramangaia.burp.gui.MainForm;
import cn.ultramangaia.burp.hook.HookManager;
import cn.ultramangaia.burp.util.Globals;

import javax.swing.*;

@SuppressWarnings("unused")
public class BurpLocalAIExtension implements BurpExtension, ExtensionUnloadingHandler {

    public static MontoyaApi api;
    public static Logging logging;
    public static HookManager hookManager;
    public static PersistedObject projectCfgData;
    private MainForm mainForm;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        api = montoyaApi;
        logging = api.logging();
        logging.logToOutput("Initializing extension Burp Local AI");
        api.extension().setName(Globals.APP_NAME);

        projectCfgData = api.persistence().extensionData();

        api.extension().registerUnloadingHandler(this);
        SwingUtilities.invokeLater(() -> {
            hookManager = new HookManager();
            mainForm = MainForm.getInstance();
            api.userInterface().registerSuiteTab(Globals.APP_NAME, mainForm.getMainPanel());

            // Print the welcome message
            logging.logToOutput(Globals.APP_NAME + " : " + Globals.VERSION);
            logging.logToOutput("");
        });
        logging.logToOutput("Initialized extension Burp Local AI");
    }

    @Override
    public void extensionUnloaded() {
        logging.logToOutput("Unloading extension Burp Local AI");
        hookManager.unhook();
        logging.logToOutput("Unloaded extension Burp Local AI");
    }
}