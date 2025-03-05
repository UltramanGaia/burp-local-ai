package cn.ultramangaia.burp.gui;

import burp.BurpLocalAIExtension;
import cn.ultramangaia.burp.gui.dialog.AppendMatchReplaceConfigDialog;
import cn.ultramangaia.burp.gui.util.ComponentGroup;
import cn.ultramangaia.burp.models.AiServer;
import cn.ultramangaia.burp.models.chat.ChatResult;
import cn.ultramangaia.burp.persistence.PersistedObject;
import cn.ultramangaia.burp.gui.util.Alignment;
import cn.ultramangaia.burp.util.Globals;
import cn.ultramangaia.burp.gui.util.PanelBuilder;
import com.alibaba.fastjson2.*;
import com.coreyd97.BurpExtenderUtilities.PopOutPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainForm {

    public static final int SELECT_COLUMN_INDEX = 0;
    public static final int MATCH_COLUMN_INDEX = 1;
    public static final int REPLACE_COLUMN_INDEX = 2;

    private static MainForm INSTANCE;
    private JTabbedPane tabbedMainPane;

    private PopOutPanel popOutWrapper;
    private JPanel mainPanel;
    private JPanel llmPane;
    private JTable llmTable;

    private JTable matchAndReplaceTable;
    private JButton addButton;
    private JButton removeButton;
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

        // 初始化Preference标签页
        initPreferencTab();

        // 初始化LLM标签页
        initLLMTab();

        mainPanel.add(tabbedMainPane, BorderLayout.CENTER);
    }

    /**
     * 初始化LLM标签页的界面组件
     * 此方法负责构建和配置LLM标签页的各个界面元素，包括表格、文本框和按钮等
     */
    private void initLLMTab() {
        // 创建一个新的JPanel作为LLM标签页的主容器，并设置其布局为BorderLayout
        llmPane = new JPanel(new BorderLayout());

        // 设置LLM引擎为全局变量OLLAMA
        llmEngine = Globals.OLLAMA;

        // 创建用于显示请求和响应的文本框
        llmReq = new JTextPane();
        llmResp = new JTextPane();

        // 创建一个包含表格头的JXTable，用于显示LLM的交互记录
        llmTable = new JXTable(new DefaultTableModel(new Object[]{"#", "Engine", "Request", "Response"}, 0));
        // 设置表格的自动调整模式，以适应列宽
        llmTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // 创建一个滚动面板来容纳LLM表格
        JScrollPane topPane = new JScrollPane(llmTable);

        // 创建一个水平分割面板来分割请求和响应的显示区域
        llmHorizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 创建请求和响应的标签，并设置其字体为粗体
        JLabel llmReqLabel = new JLabel("Request");
        JLabel llmRespLabel = new JLabel("Response");
        Font font = new Font(llmReqLabel.getFont().getFontName(), Font.BOLD, llmReqLabel.getFont().getSize());
        llmReqLabel.setFont(font);
        llmRespLabel.setFont(font);

        // 创建一个BorderLayout的面板来容纳请求文本框和其标签
        JPanel llmReqPane = new JPanel(new BorderLayout());
        llmReqPane.add(llmReqLabel, BorderLayout.NORTH);
        llmReqPane.add(new JScrollPane(llmReq), BorderLayout.CENTER);
        llmHorizontalSplitPane.setLeftComponent(llmReqPane);

        // 创建一个BorderLayout的面板来容纳响应文本框和其标签
        JPanel llmRespPane = new JPanel(new BorderLayout());
        llmRespPane.add(llmRespLabel, BorderLayout.NORTH);
        llmRespPane.add(new JScrollPane(llmResp), BorderLayout.CENTER);
        llmHorizontalSplitPane.setRightComponent(llmRespPane);

        // 创建一个FlowLayout的面板来容纳重复按钮
        JPanel llmBottomPane = new JPanel(new FlowLayout());
        repeatButton = new JButton("Repeat");
        llmBottomPane.add(repeatButton);

        // 创建一个BorderLayout的面板来组合水平分割面板和底部面板
        JPanel bottomPane = new JPanel(new BorderLayout());
        bottomPane.add(llmHorizontalSplitPane, BorderLayout.CENTER);
        bottomPane.add(llmBottomPane, BorderLayout.SOUTH);

        // 创建一个垂直分割面板来分割表格和底部的请求/响应面板
        llmVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        llmVerticalSplitPane.setTopComponent(topPane);
        llmVerticalSplitPane.setBottomComponent(bottomPane);
        // 设置垂直分割面板的分割线位置在中间
        llmVerticalSplitPane.setDividerLocation(0.5);

        // 将垂直分割面板添加到LLM标签页的主容器中
        llmPane.add(llmVerticalSplitPane, BorderLayout.CENTER);

        // 在主标签页中添加LLM标签页
        tabbedMainPane.addTab("LLM History", llmPane);
    }

    private void initPreferencTab() {
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


        ComponentGroup matchReplacePanel = new ComponentGroup(ComponentGroup.Orientation.HORIZONTAL, "Match and Replace");
        matchAndReplaceTable = new JTable(new DefaultTableModel(new Object[]{"Enable", "Match", "Replace"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == SELECT_COLUMN_INDEX;
            }

            public Class<?> getColumnClass(int column) {
                if(column == SELECT_COLUMN_INDEX){
                    return Boolean.class;
                }else{
                    return String.class;
                }
            }
        });
        matchAndReplaceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        matchAndReplaceTable.getColumnModel().getColumn(SELECT_COLUMN_INDEX).setMinWidth(100);
        matchAndReplaceTable.getColumnModel().getColumn(SELECT_COLUMN_INDEX).setMaxWidth(100);
        matchAndReplaceTable.getColumnModel().getColumn(MATCH_COLUMN_INDEX).setMinWidth(500);
        matchAndReplaceTable.getColumnModel().getColumn(MATCH_COLUMN_INDEX).setMaxWidth(600);
        matchAndReplaceTable.getColumnModel().getColumn(REPLACE_COLUMN_INDEX).setMinWidth(500);
        matchAndReplaceTable.getColumnModel().getColumn(REPLACE_COLUMN_INDEX).setMaxWidth(600);
        JScrollPane matchConfigScrollPane = new JScrollPane(matchAndReplaceTable);
        matchConfigScrollPane.setPreferredSize(new Dimension(1200, 200));

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");

        // 用于弹出窗口
        popOutWrapper = new PopOutPanel(BurpLocalAIExtension.api, mainPanel, Globals.APP_NAME);

        JPanel leftButtonPanel = new JPanel(new GridLayout(2, 1));
        leftButtonPanel.add(addButton);
        leftButtonPanel.add(removeButton);


        JPanel matchReplacePane = new JPanel(new GridBagLayout());
        GridBagConstraints c101 = new GridBagConstraints(); c101.gridy = 0; c101.gridx = 0; c101.anchor = GridBagConstraints.FIRST_LINE_START;
        GridBagConstraints c102 = new GridBagConstraints(); c102.gridy = 0; c102.gridx = 1; c102.anchor = GridBagConstraints.FIRST_LINE_START;
        matchReplacePane.add(leftButtonPanel, c101);
        matchReplacePane.add(matchConfigScrollPane, c102);

        matchReplacePanel.add(matchReplacePane);

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
                        new JPanel[] { matchReplacePanel, matchReplacePanel, matchReplacePanel, matchReplacePanel, matchReplacePanel },
                        new JPanel[] { buttonPanel, buttonPanel, buttonPanel, buttonPanel, buttonPanel },
                }, Alignment.TOPMIDDLE, 0, 0);

        tabbedMainPane.addTab("Preference", configComponent);
    }


    private void initHandlers() {
        // 初始化配置面板事件处理
        initPreferencTabHandlers();
        // 初始化LLM面板事件处理
        initLLMPaneHandlers();
    }

    private void initPreferencTabHandlers() {
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


        String matchAndReplace = PersistedObject.getInstance().getString("matchAndReplace");
        if(matchAndReplace != null){
            try {
                JSONArray jsonArray = JSON.parseArray(matchAndReplace);
                for(int i = 0; i < jsonArray.size(); i++){
                    JSONArray array = jsonArray.getJSONArray(i);
                    ((DefaultTableModel)matchAndReplaceTable.getModel()).addRow(new Object[]{array.getBoolean(0), array.getString(1), array.getString(2)});
                }
            } catch (JSONException e1) {
               e1.printStackTrace();
            }
        }


        addButton.addActionListener(e -> {
            AppendMatchReplaceConfigDialog dialog = new AppendMatchReplaceConfigDialog(null, "Append Match Replace Config", true, matchAndReplaceTable);
            dialog.setVisible(true);
        });
        removeButton.addActionListener(e -> {
            if(matchAndReplaceTable.getSelectedRow() != -1){
                int selectedRow = matchAndReplaceTable.getSelectedRow();
                ((DefaultTableModel)matchAndReplaceTable.getModel()).removeRow(selectedRow);
            }
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

            PersistedObject.getInstance().setString("matchAndReplace", JSON.toJSONString(((DefaultTableModel)matchAndReplaceTable.getModel()).getDataVector()));

            PersistedObject.getInstance().save();
        });
    }

    private void initLLMPaneHandlers() {
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

    public Component getPopOutWrapper() {
        return popOutWrapper;
    }

    public String modifyRequestBody(String originJsonBody) {
        String modifiedJsonBody = originJsonBody;
        DefaultTableModel model = (DefaultTableModel) matchAndReplaceTable.getModel();
        for(int i=0; i < model.getRowCount(); i++){
            Boolean rowEnabled = (Boolean) model.getValueAt(i, SELECT_COLUMN_INDEX);
            if(rowEnabled){
                String rowMatch = (String) model.getValueAt(i, MATCH_COLUMN_INDEX);
                String replaceMatch = (String) model.getValueAt(i, REPLACE_COLUMN_INDEX);
                modifiedJsonBody = modifiedJsonBody.replaceAll(rowMatch, replaceMatch);
            }
        }
        return modifiedJsonBody;
    }
}
