package cn.ultramangaia.burp.gui;

import cn.ultramangaia.burp.BurpLocalAIExtension;
import cn.ultramangaia.burp.impl.AISpyImpl;
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
    private JPanel configPane;
    private JTable llmTable;
    private JSplitPane llmVerticalSplitPane;
    private JSplitPane llmHorizontalSplitPane;
    private JTextPane llmReq;
    private JTextPane llmResp;
    private JRadioButton ollamaButton;
    private JRadioButton chatgptButton;
    private JRadioButton deepseekButton;
    private JCheckBox autoHookCheckBox;
    private JTextField ollamaUrlTextField;
    private JTextField ollamaModelTextField;
    private JTextField ollamaApiKeyTextField;
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

        llmReq = new JTextPane();
        llmResp = new JTextPane();
        llmTable = new JXTable(new DefaultTableModel(new Object[]{"#", "Request", "Response"}, 0));
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
        configPane = new JPanel(new GridBagLayout());


        hookButton = new JButton("Hook");


        applyButton = new JButton("Apply");


        ButtonGroup buttonGroup = new ButtonGroup();
        ollamaButton = new JRadioButton("Ollama");
        chatgptButton = new JRadioButton("ChatGPT");
        deepseekButton = new JRadioButton("DeepSeek");
        buttonGroup.add(ollamaButton);
        buttonGroup.add(chatgptButton);
        buttonGroup.add(deepseekButton);
        ollamaButton.setSelected(true);
        chatgptButton.setSelected(false);
        chatgptButton.setEnabled(false);
        deepseekButton.setSelected(false);
        deepseekButton.setEnabled(false);

        JPanel autoHookPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        autoHookCheckBox = new JCheckBox("Auto Hook");
        Boolean autoHook = BurpLocalAIExtension.projectCfgData.getBoolean("autoHook");
        if(autoHook != null && autoHook){
            autoHookCheckBox.setSelected(true);
            BurpLocalAIExtension.hookManager.hook();
            hookButton.setText("Unhook");
        }
        autoHookPane.add(autoHookCheckBox);


        JLabel ollamaUrlLabel = new JLabel("Ollama URL:");
        ollamaUrlTextField = new JTextField();
        ollamaUrlTextField.setPreferredSize(new Dimension(200, 20));
        String ollamaUrl = BurpLocalAIExtension.projectCfgData.getString("ollamaUrl");
        if(ollamaUrl == null){
            ollamaUrl = "http://127.0.0.1:11434";
            BurpLocalAIExtension.projectCfgData.setString("ollamaUrl", ollamaUrl);
        }
        ollamaUrlTextField.setText(ollamaUrl);
        JLabel ollamaModelLabel = new JLabel("Ollama Model:");
        ollamaModelTextField = new JTextField();
        ollamaModelTextField.setPreferredSize(new Dimension(200, 20));
        String ollamaModel = BurpLocalAIExtension.projectCfgData.getString("ollamaModel");
        if(ollamaModel == null){
            ollamaModel = "deepseek-r1:32b";
            BurpLocalAIExtension.projectCfgData.setString("ollamaModel", ollamaModel);
        }
        ollamaModelTextField.setText(ollamaModel);
        JLabel ollamaApiKeyLabel = new JLabel("Ollama API Key:");
        ollamaApiKeyTextField = new JTextField();
        ollamaApiKeyTextField.setPreferredSize(new Dimension(200, 20));
        String ollamaApiKey = BurpLocalAIExtension.projectCfgData.getString("ollamaApiKey");
        if(ollamaApiKey == null){
            ollamaApiKey = "";
            BurpLocalAIExtension.projectCfgData.setString("ollamaApiKey", ollamaApiKey);
        }
        ollamaApiKeyTextField.setText(ollamaApiKey);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(ollamaButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(chatgptButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(deepseekButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(autoHookPane, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        configPane.add(ollamaUrlLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(ollamaUrlTextField, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        configPane.add(ollamaModelLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(ollamaModelTextField, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        configPane.add(ollamaApiKeyLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(ollamaApiKeyTextField, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(hookButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        configPane.add(applyButton, gbc);
        tabbedMainPane.addTab("Config", configPane);


        mainPanel.add(tabbedMainPane, BorderLayout.CENTER);
    }

    private void initHandlers() {
        llmTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                String reqStr = (String)llmTable.getValueAt(llmTable.getSelectedRow(), 1);
                String respStr = (String)llmTable.getValueAt(llmTable.getSelectedRow(), 2);
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

            String reqStr = llmReq.getText();
            llmResp.setText("waiting...");
//            String responseStr = AISpyImpl.getInstance().chat(reqStr);
//            llmResp.setText(responseStr);
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

        applyButton.addActionListener(e -> {
            BurpLocalAIExtension.projectCfgData.setBoolean("autoHook", autoHookCheckBox.isSelected());
            String ollamaUrl = ollamaUrlTextField.getText().trim();
            if(ollamaUrl.isEmpty()){
                ollamaUrl = "http://127.0.0.1:11434";
            }
            String ollamaModel = ollamaModelTextField.getText().trim();
            if(ollamaModel.isEmpty()){
                ollamaModel = "deepseek-r1:32b";
            }
            String ollamaApiKey = ollamaApiKeyTextField.getText().trim();

            BurpLocalAIExtension.projectCfgData.setString("ollamaUrl", ollamaUrl);
            BurpLocalAIExtension.projectCfgData.setString("ollamaModel", ollamaModel);
            BurpLocalAIExtension.projectCfgData.setString("ollamaApiKey", ollamaApiKey);
            if(ollamaButton.isSelected()){
                AISpyImpl.getInstance().updateCfg("ollama", ollamaUrl, ollamaModel, ollamaApiKey);
            }else if(chatgptButton.isSelected()){
                AISpyImpl.getInstance().updateCfg("chatgpt", ollamaUrl, ollamaModel, ollamaApiKey);
            }else if(deepseekButton.isSelected()){
                AISpyImpl.getInstance().updateCfg("deepseek", ollamaUrl, ollamaModel, ollamaApiKey);
            }
        });

    }
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addAiLog(String requestStr, String responseStr) {
        ((DefaultTableModel)llmTable.getModel()).addRow(new Object[]{llmTable.getRowCount()+1, requestStr, responseStr});
    }
}
