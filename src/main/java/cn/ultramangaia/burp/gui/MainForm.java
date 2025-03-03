package cn.ultramangaia.burp.gui;

import burp.BurpLocalAIExtension;
import cn.ultramangaia.burp.gui.util.ComponentGroup;
import cn.ultramangaia.burp.models.AiServer;
import cn.ultramangaia.burp.models.chat.ChatResult;
import cn.ultramangaia.burp.persistence.PersistedObject;
import cn.ultramangaia.burp.gui.util.Alignment;
import cn.ultramangaia.burp.util.Globals;
import cn.ultramangaia.burp.gui.util.PanelBuilder;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainForm {

    private static MainForm INSTANCE;
    private JTabbedPane tabbedMainPane;
    private JPanel mainPanel;
    private JPanel llmPane;
    private JPanel replacePane;
    private JTable llmTable;
    private JSplitPane llmVerticalSplitPane;
    private JSplitPane llmHorizontalSplitPane;

    private String llmEngine;
    private JTextPane llmReq;
    private JTextPane llmResp;
    private JRadioButton ollamaButton;
    private JRadioButton openaiButton;
    private JRadioButton deepseekButton;
    private JCheckBox autoHookCheckBox;
    private JTextField ollamaHostTextField;
    private JTextField ollamaUrlTextField;
    private JTextField ollamaModelTextField;
    private JTextField ollamaApiKeyTextField;
    private JTextField openaiHostTextField;
    private JTextField openaiUrlTextField;
    private JTextField openaiModelTextField;
    private JTextField openaiApiKeyTextField;
    private JTextField deepseekHostTextField;
    private JTextField deepseekUrlTextField;
    private JTextField deepseekModelTextField;
    private JTextField deepseekApiKeyTextField;
    private JButton repeatButton;
    private JButton hookButton;
    private JButton applyButton;

    private MainForm() {
        initGui();
        initHandlers();
    }

    public static MainForm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainForm();
        }
        return INSTANCE;
    }

    private void initGui() {
        mainPanel = new JPanel(new BorderLayout());
        tabbedMainPane = new JTabbedPane();

        //  LLM
        llmPane = new JPanel(new BorderLayout());
        llmEngine = Globals.OLLAMA;
        llmReq = new JTextPane();
        llmResp = new JTextPane();
        llmTable = new JXTable(new DefaultTableModel(new Object[]{"#", "Engine", "Request", "Response"}, 0));
        llmTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane topPane = new JScrollPane(llmTable);

        llmHorizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JLabel llmReqLabel = new JLabel("Request");
        JLabel llmRespLabel = new JLabel("Response");
        Font font = new Font(llmReqLabel.getFont().getFontName(), Font.BOLD, llmReqLabel.getFont().getSize());
        llmReqLabel.setFont(font);
        llmRespLabel.setFont(font);

        JPanel llmReqPane = new JPanel(new BorderLayout());
        llmReqPane.add(llmReqLabel, BorderLayout.NORTH);
        llmReqPane.add(new JScrollPane(llmReq), BorderLayout.CENTER);
        llmHorizontalSplitPane.setLeftComponent(llmReqPane);
        JPanel llmRespPane = new JPanel(new BorderLayout());
        llmRespPane.add(llmRespLabel, BorderLayout.NORTH);
        llmRespPane.add(new JScrollPane(llmResp), BorderLayout.CENTER);
        llmHorizontalSplitPane.setRightComponent(llmRespPane);

        JPanel llmBottomPane = new JPanel(new FlowLayout());
        repeatButton = new JButton("Repeat");
        llmBottomPane.add(repeatButton);

        JPanel bottomPane = new JPanel(new BorderLayout());
        bottomPane.add(llmHorizontalSplitPane, BorderLayout.CENTER);
        bottomPane.add(llmBottomPane, BorderLayout.SOUTH);

        llmVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        llmVerticalSplitPane.setTopComponent(topPane);
        llmVerticalSplitPane.setBottomComponent(bottomPane);
        llmVerticalSplitPane.setDividerLocation(0.5);


        llmPane.add(llmVerticalSplitPane, BorderLayout.CENTER);
        tabbedMainPane.addTab("LLM", llmPane);

        //  Replace
        replacePane = new JPanel(new BorderLayout());
        replacePane.add(new JLabel("TODO"), BorderLayout.CENTER);
        tabbedMainPane.addTab("Replace", replacePane);


        //  Config
        ComponentGroup enginePanel = new ComponentGroup(ComponentGroup.Orientation.HORIZONTAL, "Engine");
        ButtonGroup buttonGroup = new ButtonGroup();
        ollamaButton = new JRadioButton("Ollama");
        openaiButton = new JRadioButton("OpenAI");
        deepseekButton = new JRadioButton("DeepSeek");
        buttonGroup.add(ollamaButton);
        buttonGroup.add(openaiButton);
        buttonGroup.add(deepseekButton);
        ollamaButton.setSelected(false);
        openaiButton.setSelected(false);
        deepseekButton.setSelected(false);

        enginePanel.add(ollamaButton);
        enginePanel.add(openaiButton);
        enginePanel.add(deepseekButton);


        ComponentGroup ollamaPanel = new ComponentGroup(ComponentGroup.Orientation.VERTICAL, "ollama");
        ollamaHostTextField = new JTextField(30);
        ollamaHostTextField.setText(PersistedObject.getInstance().getOrSetString("ollama.host", "http://127.0.0.1:11434"));
        ollamaPanel.addComponentWithLabel("Host:", ollamaHostTextField);
        ollamaUrlTextField = new JTextField(30);
        ollamaUrlTextField.setText(PersistedObject.getInstance().getOrSetString("ollama.url", "/api/chat"));
        ollamaPanel.addComponentWithLabel("Url:", ollamaUrlTextField);
        ollamaModelTextField = new JTextField(30);
        ollamaModelTextField.setText(PersistedObject.getInstance().getOrSetString("ollama.model", "deepseek-r1:32b"));
        ollamaPanel.addComponentWithLabel("Model:", ollamaModelTextField);
        ollamaApiKeyTextField = new JPasswordField();
        ollamaApiKeyTextField.setText(PersistedObject.getInstance().getOrSetString("ollama.apikey", ""));
        ollamaPanel.addComponentWithLabel("API Key:", ollamaApiKeyTextField);

        ComponentGroup openaiPanel = new ComponentGroup(ComponentGroup.Orientation.VERTICAL, "openai");
        openaiHostTextField = new JTextField(30);
        openaiHostTextField.setText(PersistedObject.getInstance().getOrSetString("openai.host", "https://api.openai.com"));
        openaiPanel.addComponentWithLabel("Host:", openaiHostTextField);
        openaiUrlTextField = new JTextField(30);
        openaiUrlTextField.setText(PersistedObject.getInstance().getOrSetString("openai.url", "/v1/chat/completions"));
        openaiPanel.addComponentWithLabel("Url:", openaiUrlTextField);
        openaiModelTextField = new JTextField(30);
        openaiModelTextField.setText(PersistedObject.getInstance().getOrSetString("openai.model", "gpt-3.5-turbo"));
        openaiPanel.addComponentWithLabel("Model:", openaiModelTextField);
        openaiApiKeyTextField = new JPasswordField();
        openaiApiKeyTextField.setText(PersistedObject.getInstance().getOrSetString("ollama.apikey", ""));
        openaiPanel.addComponentWithLabel("API Key:", openaiApiKeyTextField);

        ComponentGroup deepseekPanel = new ComponentGroup(ComponentGroup.Orientation.VERTICAL, "deepseek");
        deepseekHostTextField = new JTextField(30);
        deepseekHostTextField.setText(PersistedObject.getInstance().getOrSetString("deepseek.host", "https://api.deepseek.com"));
        deepseekPanel.addComponentWithLabel("Host:", deepseekHostTextField);
        deepseekUrlTextField = new JTextField(30);
        deepseekUrlTextField.setText(PersistedObject.getInstance().getOrSetString("deepseek.url", "/chat/completions"));
        deepseekPanel.addComponentWithLabel("Url:", deepseekUrlTextField);
        deepseekModelTextField = new JTextField(30);
        deepseekModelTextField.setText(PersistedObject.getInstance().getOrSetString("deepseek.model", "deepseek-chat"));
        deepseekPanel.addComponentWithLabel("Model:", deepseekModelTextField);
        deepseekApiKeyTextField = new JPasswordField();
        deepseekApiKeyTextField.setText(PersistedObject.getInstance().getOrSetString("deepseek.apikey", ""));
        deepseekPanel.addComponentWithLabel("API Key:", deepseekApiKeyTextField);


        ComponentGroup buttonPanel = new ComponentGroup(ComponentGroup.Orientation.HORIZONTAL, "Button");
        autoHookCheckBox = new JCheckBox("Auto Hook");
        hookButton = new JButton("Hook");
        applyButton = new JButton("Apply");
        buttonPanel.add(autoHookCheckBox);
        buttonPanel.add(hookButton);
        buttonPanel.add(applyButton);


        JComponent configComponent = PanelBuilder
                .build(new JPanel[][] {
                        new JPanel[] { enginePanel, enginePanel, enginePanel, enginePanel, enginePanel },
                        new JPanel[] { ollamaPanel, openaiPanel, deepseekPanel },
                        new JPanel[] { buttonPanel, buttonPanel, buttonPanel, buttonPanel, buttonPanel },
                }, Alignment.TOPMIDDLE, 0, 0);

        tabbedMainPane.addTab("Config", configComponent);

        mainPanel.add(tabbedMainPane, BorderLayout.CENTER);

    }

    private void initHandlers() {
        llmTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                String engine = (String)llmTable.getValueAt(llmTable.getSelectedRow(), 1);
                String reqStr = (String)llmTable.getValueAt(llmTable.getSelectedRow(), 2);
                String respStr = (String)llmTable.getValueAt(llmTable.getSelectedRow(), 3);
                llmEngine = engine;
                llmReq.setText(reqStr);
                llmResp.setText(respStr);
            }
        });

        llmHorizontalSplitPane.setDividerLocation(0.5);
        llmHorizontalSplitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                llmHorizontalSplitPane.setDividerLocation(0.5);
            }
        });

        llmVerticalSplitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                llmVerticalSplitPane.setDividerLocation(0.5);
            }
        });

        repeatButton.addActionListener(e -> {
            repeatButton.setEnabled(false);
            String reqStr = llmReq.getText();
            JSONObject reqBody = JSONObject.parseObject(reqStr);
            llmResp.setText("waiting...");
            new Thread(() -> {
                String content = AiServer.getInstance().chat(llmEngine, reqBody);
                llmResp.setText(content);
                repeatButton.setEnabled(true);
            }).start();
        });

        String engineName = PersistedObject.getInstance().getString("engine");
        if(engineName == null){
            engineName = Globals.OLLAMA;
        }
        switch (engineName){
            case Globals.OLLAMA:
                ollamaButton.setSelected(true);
                AiServer.getInstance().updateCfg(Globals.OLLAMA);
                break;
            case Globals.OPENAI:
                openaiButton.setSelected(true);
                AiServer.getInstance().updateCfg(Globals.OPENAI);
                break;
            case Globals.DEEPSEEK:
                deepseekButton.setSelected(true);
                AiServer.getInstance().updateCfg(Globals.DEEPSEEK);
                break;
        }

        ollamaButton.addActionListener(e -> {
            ollamaHostTextField.setText("http://127.0.0.1:11434");
            ollamaUrlTextField.setText("/api/chat");
            ollamaModelTextField.setText("deepseek-r1:32b");
        });
        openaiButton.addActionListener(e -> {
            openaiHostTextField.setText("https://api.openai.com");
            openaiUrlTextField.setText("/v1/chat/completions");
            openaiModelTextField.setText("gpt-3.5-turbo");
        });
        deepseekButton.addActionListener(e -> {
            deepseekHostTextField.setText("https://api.deepseek.com");
            deepseekUrlTextField.setText("/chat/completions");
            deepseekModelTextField.setText("deepseek-chat");
            //    modelTextField.setText("deepseek-reasoner");
        });

        hookButton.addActionListener(e -> {
            switch(hookButton.getText()){
                case "Hook":
                    BurpLocalAIExtension.hookManager.hook();
                    hookButton.setText("Unhook");
                    break;
                case "Unhook":
                    BurpLocalAIExtension.hookManager.unhook();
                    hookButton.setText("Hook");
                    break;
            }
        });

        Boolean autoHook = PersistedObject.getInstance().getBoolean("autoHook");
        if(autoHook != null && autoHook){
            autoHookCheckBox.setSelected(true);
            BurpLocalAIExtension.hookManager.hook();
            hookButton.setText("Unhook");
        }

        applyButton.addActionListener(e -> {
            PersistedObject.getInstance().setBoolean("autoHook", autoHookCheckBox.isSelected());
            PersistedObject.getInstance().setString("ollama.host", ollamaHostTextField.getText().trim());
            PersistedObject.getInstance().setString("ollama.url", ollamaUrlTextField.getText().trim());
            PersistedObject.getInstance().setString("ollama.model", ollamaModelTextField.getText().trim());
            PersistedObject.getInstance().setString("ollama.apikey", ollamaApiKeyTextField.getText().trim());

            PersistedObject.getInstance().setString("openai.host", openaiHostTextField.getText().trim());
            PersistedObject.getInstance().setString("openai.url", openaiUrlTextField.getText().trim());
            PersistedObject.getInstance().setString("openai.model", openaiModelTextField.getText().trim());
            PersistedObject.getInstance().setString("openai.apikey", openaiApiKeyTextField.getText().trim());

            PersistedObject.getInstance().setString("deepseek.host", deepseekHostTextField.getText().trim());
            PersistedObject.getInstance().setString("deepseek.url", deepseekUrlTextField.getText().trim());
            PersistedObject.getInstance().setString("deepseek.model", deepseekModelTextField.getText().trim());
            PersistedObject.getInstance().setString("deepseek.apikey", deepseekApiKeyTextField.getText().trim());

            if(ollamaButton.isSelected()){
                PersistedObject.getInstance().setString("engine", Globals.OLLAMA);
                AiServer.getInstance().updateCfg(Globals.OLLAMA);
            }else if(openaiButton.isSelected()){
                PersistedObject.getInstance().setString("engine", Globals.OPENAI);
                AiServer.getInstance().updateCfg(Globals.OPENAI);
            }else if(deepseekButton.isSelected()){
                PersistedObject.getInstance().setString("engine", Globals.DEEPSEEK);
                AiServer.getInstance().updateCfg(Globals.DEEPSEEK);
            }
            PersistedObject.getInstance().save();
        });

    }
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addAiLog(ChatResult chatResult) {
        String engine = chatResult.engine;
        String requestStr = chatResult.reqBody.toJSONString(JSONWriter.Feature.PrettyFormat);
        String content = chatResult.content;
        ((DefaultTableModel)llmTable.getModel()).addRow(new Object[]{llmTable.getRowCount()+1, engine, requestStr, content});
    }
}
