package cn.ultramangaia.burp.gui.dialog;

import cn.ultramangaia.burp.gui.MainForm;
import com.coreyd97.BurpExtenderUtilities.Alignment;
import com.coreyd97.BurpExtenderUtilities.PanelBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppendMatchReplaceConfigDialog extends JDialog {

    public AppendMatchReplaceConfigDialog(Frame owner, String title, boolean modal, JTable jTable){
        super(owner, title, modal);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JScrollPane outerPanel = new JScrollPane();
        JLabel tipsLabel = new JLabel("Please enter the match and replace string.");
        JLabel matchLabel = new JLabel("Match:");
        JTextField matchTextField = new JTextField(60);

        JLabel replaceLabel = new JLabel("Replace:");
        JTextField replaceTextField = new JTextField(60);


        JLabel statusLabel = new JLabel("Status: OK");
        JPanel southButtons = new JPanel();
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = matchTextField.getText().trim();
                String path = replaceTextField.getText().trim();

                if(name.isEmpty()){
                    statusLabel.setText("Status: Error Match !!!");
                }else if(path.isEmpty()){
                    statusLabel.setText("Status: Error Replace !!!");
                }else {
                    DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                    model.addRow(new Object[]{false, name, path});
                    setVisible(false);
                    dispose();
                }
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        southButtons.add(okButton);
        southButtons.add(cancelButton);

        JComponent mainComponent = PanelBuilder
                .build(new Component[][] {
                        new Component[] { tipsLabel, tipsLabel,  tipsLabel},
                        new Component[] { matchLabel, matchTextField },
                        new Component[] { replaceLabel, replaceTextField },
                        new Component[] { statusLabel, statusLabel,  statusLabel},
                        new Component[] { southButtons, southButtons, southButtons },
                }, Alignment.TOPMIDDLE, 0, 0);

        outerPanel.setViewportView(mainComponent);
        setContentPane(outerPanel);

        pack();
        setLocationRelativeTo(MainForm.getInstance().getPopOutWrapper());
    }

}
