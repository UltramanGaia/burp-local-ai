package cn.ultramangaia.burp.gui.util;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelBuilder {

    public PanelBuilder(){}

    Component[][] componentGrid;
    int[][] gridWeightsX;
    int[][] gridWeightsY;
    Alignment alignment=Alignment.CENTER;
    double scaleX=1.0, scaleY=1.0;
    int insetsX=0,insetsY=0;

    public Component[][] getComponentGrid() {
        return componentGrid;
    }

    public PanelBuilder setComponentGrid(Component[][] componentGrid) {
        this.componentGrid = componentGrid;
        return this;
    }

    public int[][] getGridWeightsX() {
        return gridWeightsX;
    }

    public PanelBuilder setGridWeightsX(int[][] gridWeightsX) {
        this.gridWeightsX = gridWeightsX;
        return this;
    }

    public int[][] getGridWeightsY() {
        return gridWeightsY;
    }

    public PanelBuilder setGridWeightsY(int[][] gridWeightsY) {
        this.gridWeightsY = gridWeightsY;
        return this;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public PanelBuilder setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public double getScaleX() {
        return scaleX;
    }

    public PanelBuilder setScaleX(double scaleX) {
        if(scaleX > 1 || scaleX < 0) throw new IllegalArgumentException("Scale must be between 0 and 1");
        this.scaleX = scaleX;
        return this;
    }

    public double getScaleY() {
        return scaleY;
    }

    public PanelBuilder setScaleY(double scaleY) {
        if(scaleY > 1 || scaleY < 0) throw new IllegalArgumentException("Scale must be between 0 and 1");
        this.scaleY = scaleY;
        return this;
    }

    public int getInsetsX() {
        return insetsX;
    }

    public PanelBuilder setInsetsX(int insetsX) {
        this.insetsX = insetsX;
        return this;
    }

    public int getInsetsY() {
        return insetsY;
    }

    public PanelBuilder setInsetsY(int insetsY) {
        this.insetsY = insetsY;
        return this;
    }

    public JPanel build() {
        JPanel containerPanel = new JPanel(new GridBagLayout());
        HashMap<Component, GridBagConstraints> constraintsMap = new HashMap<>();
        int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE, maxx = Integer.MIN_VALUE, maxy = Integer.MIN_VALUE;

        for (int row = 0; row < componentGrid.length; row++) {
            for (int column = 0; column < componentGrid[row].length; column++) {
                Component panel = componentGrid[row][column];
                if(panel != null) {
                    int gridx = column + 1;
                    int gridy = row + 1;

                    if (constraintsMap.containsKey(panel)) {
                        GridBagConstraints constraints = constraintsMap.get(panel);
                        if (gridx < constraints.gridx || (constraints.gridx + constraints.gridwidth) < gridx) {
                            throw new RuntimeException("Panels must be contiguous.");
                        }
                        if (gridy < constraints.gridy || (constraints.gridy + constraints.gridheight) < gridy) {
                            throw new RuntimeException("Panels must be contiguous.");
                        }

                        constraints.gridwidth = gridx - constraints.gridx + 1;
                        constraints.gridheight = gridy - constraints.gridy + 1;
                        try{
                            constraints.weightx = gridWeightsX[gridy-1][gridx-1];
                        }catch (Exception e){  }

                        try{
                            constraints.weighty = gridWeightsY[gridy-1][gridx-1];
                        }catch (Exception e){ }




                    } else {
                        GridBagConstraints constraints = new GridBagConstraints();
                        constraints.fill = GridBagConstraints.BOTH;
                        constraints.gridx = gridx;
                        constraints.gridy = gridy;
                        try{
                            constraints.weightx = gridWeightsX[gridy-1][gridx-1];
                        }catch (Exception e){  }

                        try{
                            constraints.weighty = gridWeightsY[gridy-1][gridx-1];
                        }catch (Exception e){ }
                        constraints.insets = new Insets(insetsY, insetsX, insetsY, insetsX);
                        constraintsMap.put(panel, constraints);
                    }

                    if (gridx < minx) minx = gridx;
                    if (gridx > maxx) maxx = gridx;
                    if (gridy < miny) miny = gridy;
                    if (gridy > maxy) maxy = gridy;
                }else{
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = column+1;
                    constraints.gridy = row+1;
                    constraints.fill = GridBagConstraints.BOTH;
                    constraints.weightx = constraints.weighty = 1;
                    JPanel filler = new JPanel();
                    constraintsMap.put(filler, constraints);
                }
            }
        }

        GridBagConstraints innerPanelGbc = new GridBagConstraints();
        innerPanelGbc.fill = GridBagConstraints.BOTH;
        innerPanelGbc.gridx = innerPanelGbc.gridy = 2;
        innerPanelGbc.weightx = scaleX;
        innerPanelGbc.weighty = scaleY;
        GridBagConstraints paddingLeftGbc = new GridBagConstraints();
        GridBagConstraints paddingRightGbc = new GridBagConstraints();
        GridBagConstraints paddingTopGbc = new GridBagConstraints();
        GridBagConstraints paddingBottomGbc = new GridBagConstraints();
        paddingLeftGbc.fill = paddingRightGbc.fill = paddingTopGbc.fill = paddingBottomGbc.fill = GridBagConstraints.BOTH;
        paddingLeftGbc.weightx = paddingRightGbc.weightx = (1-scaleX)/2;
        paddingTopGbc.weighty = paddingBottomGbc.weighty = (1-scaleY)/2;
        paddingLeftGbc.gridy = paddingRightGbc.gridy = 2;
        paddingTopGbc.gridx = paddingBottomGbc.gridx = 2;
        paddingLeftGbc.gridx = 1;
        paddingRightGbc.gridx = 3;
        paddingTopGbc.gridy = 1;
        paddingBottomGbc.gridy = 3;

        JPanel topPanel, leftPanel, bottomPanel, rightPanel;

        if(alignment != Alignment.FILL && alignment != Alignment.TOPLEFT
                && alignment != Alignment.TOPMIDDLE && alignment != Alignment.TOPRIGHT){
            containerPanel.add(topPanel = new JPanel(), paddingTopGbc);
//            topPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        }

        if(alignment != Alignment.FILL && alignment != Alignment.TOPLEFT
                && alignment != Alignment.MIDDLELEFT && alignment != Alignment.BOTTOMLEFT){
            containerPanel.add(leftPanel = new JPanel(), paddingLeftGbc);
//            leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        }

        if(alignment != Alignment.FILL && alignment != Alignment.TOPRIGHT
                && alignment != Alignment.MIDDLERIGHT && alignment != Alignment.BOTTOMRIGHT){
            containerPanel.add(rightPanel = new JPanel(), paddingRightGbc);
//            rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        }

        if(alignment != Alignment.FILL && alignment != Alignment.BOTTOMLEFT
                && alignment != Alignment.BOTTOMMIDDLE && alignment != Alignment.BOTTOMRIGHT){
            containerPanel.add(bottomPanel = new JPanel(), paddingBottomGbc);
//            bottomPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        }


        JPanel innerContainer = new JPanel(new GridBagLayout());
        for (Component component : constraintsMap.keySet()) {
            innerContainer.add(component, constraintsMap.get(component));
        }
        containerPanel.add(innerContainer, innerPanelGbc);

        return containerPanel;
    }

    @Deprecated
    public static JPanel build(Component[][] componentGrid, int[][] gridWeights, Alignment alignment, double scaleX, double scaleY){
        return new PanelBuilder().setComponentGrid(componentGrid)
                .setGridWeightsX(gridWeights)
                .setGridWeightsY(gridWeights)
                .setAlignment(alignment)
                .setScaleX(scaleX)
                .setScaleY(scaleY)
                .build();
    }

    @Deprecated
    public static JPanel build(Component[][] componentGrid, Alignment alignment, double scaleX, double scaleY){
        return new PanelBuilder().setComponentGrid(componentGrid)
                .setAlignment(alignment)
                .setScaleX(scaleX)
                .setScaleY(scaleY)
                .build();
    }
}
