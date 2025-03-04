package cn.ultramangaia.burp.gui.util;


import javax.swing.*;
import java.awt.*;

public class ComponentGroup extends JPanel {

    public enum Orientation {HORIZONTAL, VERTICAL}

    private final Orientation orientation;
    private int componentIndex = 1;

    public ComponentGroup(Orientation orientation){
        super(new GridBagLayout());
        this.orientation = orientation;
    }

    public ComponentGroup(Orientation orientation, String title){
        this(orientation);
        this.setBorder(BorderFactory.createTitledBorder(title));
    }

    public void addComponentWithLabel(String label, Component component){
        addComponentWithLabel(label, component, false);
    }

    public void addComponentWithLabel(String label, Component component, boolean fillVertical){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = fillVertical ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        if(orientation == Orientation.VERTICAL) {
            gbc.gridx = 1;
            gbc.gridy = componentIndex;
            gbc.weightx = 0.15;
            gbc.weighty = 1;
        }else{
            gbc.gridx = componentIndex;
            gbc.gridy = 1;
            gbc.weightx = 1;
        }
        this.add(new JLabel(label), gbc);

        if(orientation == Orientation.VERTICAL){
            gbc.gridx++;
            gbc.weightx = 0.85;
        }else{
            gbc.gridy++;
        }

        this.add(component, gbc);
        componentIndex++;
    }


    /**
     * Generate the constraints for the next element in the group.
     * Useful for customising before addition.
     * @return GridBagConstraints The default constraints for the next item in the group.
     */
    public GridBagConstraints generateNextConstraints(boolean fillVertical){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = fillVertical ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        gbc.weighty = gbc.weightx = 1;
        gbc.gridwidth = 2;
        if(orientation == Orientation.VERTICAL) {
            gbc.gridx = 1;
            gbc.gridy = componentIndex;
        }else{
            gbc.gridy = 1;
            gbc.gridx = componentIndex*2; //Since we're using 2 width components
        }
        componentIndex++;
        return gbc;
    }

    @Override
    public Component add(Component comp) {
        this.add(comp, generateNextConstraints(true));
        return comp;
    }

    public Component add(Component comp, boolean fillVertical) {
        this.add(comp, generateNextConstraints(fillVertical));
        return comp;
    }
}
