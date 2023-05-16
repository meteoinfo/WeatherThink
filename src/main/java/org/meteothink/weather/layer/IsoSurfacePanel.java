package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.chart.graphic.TriMeshGraphic;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.data.dimarray.DimArray;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.LegendManage;
import org.meteoinfo.geometry.colors.TransferFunction;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.legend.LegendScheme;
import org.meteoinfo.geometry.legend.LegendType;
import org.meteoinfo.geometry.legend.PolygonBreak;
import org.meteoinfo.geometry.shape.ShapeTypes;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.ui.slider.RangeSlider;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.event.TransferFunctionChangedEvent;
import org.meteothink.weather.event.TransferFunctionChangedListener;
import org.meteothink.weather.plot.ControlPoint;
import org.meteothink.weather.plot.OpacityControlPoint;
import org.meteothink.weather.plot.TransferFunctionPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class IsoSurfacePanel extends LayerPanel {

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/RenderDockable");
    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    TransferFunctionPanel transferFunctionPanel;
    JLabel jLabelValue;
    JTextField jTextFieldValue;
    JLabel jLabelColor;
    JLabel jLabelColorView;
    JLabel jLabelOpacity;
    JSlider jSliderOpacity;

    JCheckBox jCheckBoxColorVariable;
    JPanel jPanelColor;
    JLabel jLabelColorVariable;
    JComboBox jComboBoxColorVariable;
    TransferFunctionPanel colorTransferFunctionPanel;
    JLabel jLabelDataValue;
    JTextField jTextFieldDataValue;
    JLabel jColorLabelOpacity;
    JTextField jTextFieldOpacity;
    RangeSlider jSliderValue;
    JTextField jTextFieldMinValue;
    JTextField jTextFieldMaxValue;

    DimArray data;
    DimArray colorData;
    double minValue = 0;
    double maxValue = 1;
    double cMinValue = 0;
    double cMaxValue = 0;
    double minData = 0;
    double maxData = 1;
    DecimalFormat decimalFormat = new DecimalFormat("#.##E0");
    TriMeshGraphic graphic;
    boolean changeMinMaxValue = true;
    boolean updateTransferFunctionPanel = true;

    /**
     * Constructor
     *
     * @param layer The plot layer
     */
    public IsoSurfacePanel(PlotLayer layer, ColorMap[] colorMaps) {
        super(layer, colorMaps);

        Border border = BorderFactory.createTitledBorder(bundle.getString("RenderDockable.isoSurfacePanel.border.title"));
        this.setBorder(border);

        initComponents();
    }

    private void initComponents() {
        //Variable
        this.jLabelVariable = new JLabel(bundle.getString("RenderDockable.variable"));
        this.jComboBoxVariable = new JComboBox();
        jComboBoxVariable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onVariableChanged(e);
            }
        });

        //Transfer function
        if (this.data == null)
            transferFunctionPanel = new TransferFunctionPanel(null);
        else
            transferFunctionPanel = new TransferFunctionPanel(this.data.getArray());
        transferFunctionPanel.addTransferFunctionChangedListener(new TransferFunctionChangedListener() {
            @Override
            public void transferFunctionChangedEvent(TransferFunctionChangedEvent e) {
                onTransferFunctionChanged(e);
            }
        });
        transferFunctionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                updateTransferFunctionPanel = false;
                if (transferFunctionPanel.mouseInColorMap(e)) {
                    ControlPoint controlPoint = transferFunctionPanel.getSelectedControlPoint();
                    if (controlPoint == null) {
                        jLabelValue.setEnabled(false);
                        jTextFieldValue.setEnabled(false);
                    } else {
                        jLabelValue.setEnabled(true);
                        jTextFieldValue.setEnabled(true);
                        double value = controlPoint.getValue(minValue, maxValue);
                        jTextFieldValue.setText(decimalFormat.format(value));
                    }
                }
                updateTransferFunctionPanel = true;
            }
        });
        transferFunctionPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                updateTransferFunctionPanel = false;
                ControlPoint controlPoint = transferFunctionPanel.getSelectedControlPoint();
                if (controlPoint != null) {
                    jLabelValue.setEnabled(true);
                    jTextFieldValue.setEnabled(true);
                    double value = controlPoint.getValue(minValue, maxValue);
                    jTextFieldValue.setText(decimalFormat.format(value));
                }
                updateTransferFunctionPanel = true;
            }
        });

        //Value
        this.jLabelValue = new JLabel(bundle.getString("RenderDockable.isoSurfacePanel.jLabelValue.text"));
        this.jTextFieldValue = new JTextField(10);
        jTextFieldValue.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    onValueChanged();
                }
            }
        });

        //Color
        jLabelColor = new JLabel(bundle.getString("RenderDockable.color"));
        jLabelColorView = new JLabel();
        jLabelColorView.setOpaque(true);
        jLabelColorView.setBackground(Color.white);
        jLabelColorView.setPreferredSize(new Dimension(50, 20));
        jLabelColorView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onColorActionPerformed(e);
            }
        });

        jLabelOpacity = new JLabel(bundle.getString("RenderDockable.opacity"));
        jSliderOpacity = new JSlider();
        jSliderOpacity.setValue(100);
        jSliderOpacity.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onOpacityChanged(e);
            }
        });

        //Using color variable or not
        jCheckBoxColorVariable = new JCheckBox(bundle.getString("RenderDockable.usingColorVariable"));
        jCheckBoxColorVariable.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                jPanelColor.setVisible(checkBox.isSelected());
                if (jPanelColor.isVisible()) {
                    if (Objects.equals(jComboBoxColorVariable.getSelectedItem(), jComboBoxVariable.getSelectedItem())) {
                        int idx = jComboBoxVariable.getSelectedIndex();
                        idx = idx < jComboBoxVariable.getItemCount() - 1 ? idx + 1 : 0;
                        jComboBoxColorVariable.setSelectedIndex(idx);
                    }
                }

                if (graphic == null) {
                    graphic = createGraphic();
                }
                else {
                    if (jPanelColor.isVisible())
                        updateGraphicColorVariable();
                    else {
                        graphic.setVertexValue(null);
                        graphic.setColor(getColor());
                    }
                }
                if (graphic != null) {
                    layer.fileGraphicChangedEvent(graphic);
                }
            }
        });

        //Color variable panel
        jPanelColor = new JPanel();
        jPanelColor.setBorder(BorderFactory.createTitledBorder(bundle.getString("RenderDockable.transferFunction.title")));
        //Variable
        jLabelColorVariable = new JLabel(bundle.getString("RenderDockable.variable"));
        jComboBoxColorVariable = new JComboBox();
        jComboBoxColorVariable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onColorVariableChanged(e);
            }
        });

        //Transfer function
        ColorMap ct = ColorUtil.findColorTable(colorMaps, "matlab_jet");
        if (this.colorData == null)
            colorTransferFunctionPanel = new TransferFunctionPanel(null, ct, colorMaps, 1.0f, 1);
        else
            colorTransferFunctionPanel = new TransferFunctionPanel(this.colorData.getArray(), ct, colorMaps, 1.0f, 1);
        colorTransferFunctionPanel.addTransferFunctionChangedListener(new TransferFunctionChangedListener() {
            @Override
            public void transferFunctionChangedEvent(TransferFunctionChangedEvent e) {
                onColorTransferFunctionChanged(e);
            }
        });
        colorTransferFunctionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                updateTransferFunctionPanel = false;
                if (colorTransferFunctionPanel.mouseInColorMap(e)) {
                } else {
                    OpacityControlPoint ocp = colorTransferFunctionPanel.getSelectedOCP();
                    if (ocp == null) {
                        jLabelDataValue.setEnabled(false);
                        jTextFieldDataValue.setText("");
                        jTextFieldDataValue.setEnabled(false);
                        jLabelOpacity.setEnabled(false);
                        jTextFieldOpacity.setText("");
                        jTextFieldOpacity.setEnabled(false);
                    } else {
                        jLabelDataValue.setEnabled(true);
                        jTextFieldDataValue.setEnabled(true);
                        double value = ocp.getValue(cMinValue, cMaxValue);
                        jTextFieldDataValue.setText(decimalFormat.format(value));
                        jLabelOpacity.setEnabled(true);
                        jTextFieldOpacity.setEnabled(true);
                        jTextFieldOpacity.setText(new DecimalFormat("#.##").format(ocp.getOpacity()));
                    }
                }
                updateTransferFunctionPanel = true;
            }
        });
        colorTransferFunctionPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                updateTransferFunctionPanel = false;
                OpacityControlPoint ocp = colorTransferFunctionPanel.getSelectedOCP();
                if (ocp != null) {
                    jLabelDataValue.setEnabled(true);
                    jTextFieldDataValue.setEnabled(true);
                    double value = ocp.getValue(cMinValue, cMaxValue);
                    jTextFieldDataValue.setText(decimalFormat.format(value));
                    jLabelOpacity.setEnabled(true);
                    jTextFieldOpacity.setEnabled(true);
                    jTextFieldOpacity.setText(new DecimalFormat("#.##").format(ocp.getOpacity()));
                }
                updateTransferFunctionPanel = true;
            }
        });

        jLabelDataValue = new JLabel(bundle.getString("RenderDockable.dataValue"));
        jTextFieldDataValue = new JTextField(5);
        jLabelOpacity = new JLabel(bundle.getString("RenderDockable.opacity"));
        jTextFieldOpacity = new JTextField(5);
        jLabelDataValue.setEnabled(false);
        jTextFieldDataValue.setEnabled(false);
        jLabelOpacity.setEnabled(false);
        jTextFieldOpacity.setEnabled(false);
        jTextFieldDataValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDataValueChanged(e);
            }
        });
        jTextFieldOpacity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onColorOpacityChanged(e);
            }
        });

        jSliderValue = new RangeSlider();
        jSliderValue.setValue(0);
        jSliderValue.setUpperValue(100);
        jTextFieldMinValue = new JTextField(5);
        jTextFieldMaxValue = new JTextField(5);
        jTextFieldMinValue.setText(decimalFormat.format(minData));
        jTextFieldMaxValue.setText(decimalFormat.format(maxData));
        jSliderValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChanged(e);
            }
        });
        jTextFieldMinValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cMinValue = Double.parseDouble(jTextFieldMinValue.getText());
                if (cMinValue < minData) {
                    cMinValue = minData;
                    jTextFieldMinValue.setText(decimalFormat.format(cMinValue));
                }
                double min = (cMinValue - minData) / (maxData - minData) * 100;
                changeMinMaxValue = false;
                jSliderValue.setValue((int) min);
                changeMinMaxValue = true;
            }
        });
        jTextFieldMaxValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cMaxValue = Double.parseDouble(jTextFieldMaxValue.getText());
                if (cMaxValue > maxData) {
                    cMaxValue = maxData;
                    jTextFieldMaxValue.setText(decimalFormat.format(cMaxValue));
                }
                double max = (cMaxValue - minData) / (maxData - minData) * 100;
                changeMinMaxValue = false;
                jSliderValue.setUpperValue((int) max);
                changeMinMaxValue = true;
            }
        });

        //Transfer function layout
        GroupLayout layout = new GroupLayout(this.jPanelColor);
        jPanelColor.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelColorVariable)
                        .addComponent(jComboBoxColorVariable, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(colorTransferFunctionPanel)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelDataValue)
                        .addComponent(jTextFieldDataValue)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelOpacity)
                        .addComponent(jTextFieldOpacity))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldMinValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSliderValue)
                        .addComponent(jTextFieldMaxValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelColorVariable)
                        .addComponent(jComboBoxColorVariable, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(colorTransferFunctionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelDataValue)
                        .addComponent(jTextFieldDataValue)
                        .addComponent(jLabelOpacity)
                        .addComponent(jTextFieldOpacity))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jTextFieldMinValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSliderValue)
                        .addComponent(jTextFieldMaxValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        //Set layout
        layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addComponent(transferFunctionPanel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelValue)
                                .addComponent(jTextFieldValue))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelColor)
                                .addComponent(jLabelColorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelOpacity)
                                .addComponent(jSliderOpacity))
                        .addComponent(jCheckBoxColorVariable)
                        .addComponent(jPanelColor)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addComponent(transferFunctionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelValue)
                                .addComponent(jTextFieldValue))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelColor)
                                .addComponent(jLabelColorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelOpacity)
                                .addComponent(jSliderOpacity))
                        .addComponent(jCheckBoxColorVariable)
                        .addComponent(jPanelColor)
        );

        jPanelColor.setVisible(jCheckBoxColorVariable.isSelected());
        this.updateUI();
    }

    @Override
    public void setDataset(Dataset dataset) {
        if (this.dataset != null) {
            if (this.dataset.getFileName().equals(dataset.getFileName())) {
                return;
            }
        }
        this.dataset = dataset;

        List<Variable> variables3D = get3DVariables();
        this.jComboBoxVariable.removeAllItems();
        this.jComboBoxColorVariable.removeAllItems();
        for (Variable variable : variables3D) {
            this.jComboBoxVariable.addItem(variable.getName());
            this.jComboBoxColorVariable.addItem(variable.getName());
        }
        if (this.jComboBoxVariable.getItemCount() > 0) {
            this.jComboBoxVariable.setSelectedIndex(0);
        }
        if (this.jComboBoxColorVariable.getItemCount() > 0) {
            this.jComboBoxColorVariable.setSelectedIndex(0);
        }

        DimArray arr3d = this.dataset.read3DArray((String) this.jComboBoxVariable.getSelectedItem());
        float mean = (float)ArrayMath.mean(arr3d.getArray());
        this.jTextFieldValue.setText(String.valueOf(mean));
    }

    private void readDataArray(String varName) {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        data = dataset.read3DArray(varName);
        this.minValue = ArrayMath.min(data.getArray()).doubleValue();
        this.maxValue = ArrayMath.max(data.getArray()).doubleValue();
        this.transferFunctionPanel.setData(data.getArray());
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void readColorDataArray(String varName) {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        colorData = dataset.read3DArray(varName);
        this.minData = ArrayMath.min(colorData.getArray()).doubleValue();
        this.maxData = ArrayMath.max(colorData.getArray()).doubleValue();
        this.cMinValue = this.minData;
        this.cMaxValue = this.maxData;
        this.jSliderValue.setValue(0);
        this.jSliderValue.setUpperValue(100);
        this.jTextFieldMinValue.setText(decimalFormat.format(minData));
        this.jTextFieldMaxValue.setText(decimalFormat.format(maxData));
        this.colorTransferFunctionPanel.setData(colorData.getArray());
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        return createGraphic();
    }

    private Color getColor() {
        Color color = this.jLabelColorView.getBackground();
        int opacity = (int) (this.jSliderOpacity.getValue() / 100. * 255);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);

        return color;
    }

    private TriMeshGraphic createGraphic() {
        if (this.dataset == null) {
            return null;
        }

        String varName = (String) this.jComboBoxVariable.getSelectedItem();
        float value = Float.parseFloat(this.jTextFieldValue.getText());

        if (data == null) {
            data = this.dataset.read3DArray(varName);
        }
        if (this.jCheckBoxColorVariable.isSelected() && colorData != null) {
            TransferFunction transferFunction = colorTransferFunctionPanel.getTransferFunction();
            graphic = GraphicFactory.isosurface(data.getArray(), data.getXDimension().getDimValue(),
                    data.getYDimension().getDimValue(), data.getZDimension().getDimValue(), value, colorData.getArray(),
                    transferFunction, 4);
        } else {
            Color color = getColor();
            PolygonBreak pb = new PolygonBreak();
            pb.setColor(color);
            pb.setDrawOutline(false);
            LegendScheme ls = new LegendScheme(ShapeTypes.POLYGON);
            ls.addLegendBreak(pb);
            ls.setLegendType(LegendType.SINGLE_SYMBOL);
            graphic = GraphicFactory.isosurface(data.getArray(), data.getXDimension().getDimValue(),
                    data.getYDimension().getDimValue(), data.getZDimension().getDimValue(), value, ls, 4);
        }
        return graphic;
    }

    private void updateGraphicColorVariable() {
        graphic.setVertexValue(colorData.getArray(), data.getXDimension().getDimValue(),
                data.getYDimension().getDimValue(), data.getZDimension().getDimValue());
        graphic.setTransferFunction(colorTransferFunctionPanel.getTransferFunction());
    }

    private void updateGraphicColor() {
        graphic.setTransferFunction(colorTransferFunctionPanel.getTransferFunction());
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
                float mean = (float) ArrayMath.mean(data.getArray());
                this.jTextFieldValue.setText(decimalFormat.format(mean));
            }
        }
    }

    private void onValueChanged() {
        if (updateTransferFunctionPanel) {
            ControlPoint cp = transferFunctionPanel.getSelectedControlPoint();
            if (cp != null) {
                double v = Double.parseDouble(jTextFieldValue.getText());
                float ratio = (float) ((v - minValue) / (maxValue - minValue));
                if (v < cp.getMinRatio()) {
                    jTextFieldValue.setText(decimalFormat.format(ratio));
                    return;
                }
                cp.setRatio(ratio);
                transferFunctionPanel.repaint();
                transferFunctionPanel.fileTransferFunctionChangedEvent();
            }
        }
        /*Graphic graphic = createGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }*/
    }

    private void onColorActionPerformed(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        Color color = JColorChooser.showDialog(this.getRootPane().getParent(), "选择颜色", label.getBackground());
        if (color != null) {
            this.jLabelColorView.setBackground(color);
            if (graphic == null)
                graphic = createGraphic();
            else {
                color = getColor();
                graphic.setColor(color);
            }
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }

    private void onTransferFunctionChanged(TransferFunctionChangedEvent e) {
        if (this.dataset != null) {
            if (data == null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
            }

            graphic = createGraphic();
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }

    private void onOpacityChanged(ChangeEvent e) {
        if (graphic == null)
            graphic = createGraphic();
        else {
            Color color = getColor();
            graphic.setColor(color);
        }
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }

    private void onColorVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                String varName = (String) this.jComboBoxColorVariable.getSelectedItem();
                readColorDataArray(varName);
                if (graphic == null) {
                    createGraphic();
                } else {
                    updateGraphicColorVariable();
                }
                if (graphic != null) {
                    this.layer.fileGraphicChangedEvent(graphic);
                }
            }
        }
    }

    private void onDataValueChanged(ActionEvent e) {
        if (updateTransferFunctionPanel) {
            OpacityControlPoint ocp = colorTransferFunctionPanel.getSelectedOCP();
            if (ocp != null) {
                double v = Double.parseDouble(jTextFieldDataValue.getText());
                float ratio = (float) ((v - minValue) / (maxValue - minValue));
                if (v < ocp.getMinRatio()) {
                    jTextFieldDataValue.setText(decimalFormat.format(ratio));
                    return;
                }
                ocp.setRatio(ratio);
                colorTransferFunctionPanel.repaint();
                colorTransferFunctionPanel.fileTransferFunctionChangedEvent();
            }
        }
    }

    private void onColorOpacityChanged(ActionEvent e) {
        if (updateTransferFunctionPanel) {
            OpacityControlPoint ocp = colorTransferFunctionPanel.getSelectedOCP();
            if (ocp != null) {
                float opacity = Float.parseFloat(jTextFieldOpacity.getText());
                if (opacity < 0) {
                    jTextFieldOpacity.setText(new DecimalFormat("#.##").format(0));
                    return;
                } else if (opacity > 1) {
                    jTextFieldOpacity.setText(new DecimalFormat("#.##").format(1));
                }
                ocp.setOpacity(opacity);
                colorTransferFunctionPanel.repaint();
                colorTransferFunctionPanel.fileTransferFunctionChangedEvent();
            }
        }
    }

    private void setMinMaxValue() {
        colorTransferFunctionPanel.setMinValue(minValue);
        colorTransferFunctionPanel.setMaxValue(maxValue);
        OpacityControlPoint ocp = colorTransferFunctionPanel.getSelectedOCP();
        if (ocp != null) {
            updateTransferFunctionPanel = false;
            this.jTextFieldDataValue.setText(decimalFormat.format(ocp.getValue(minValue, maxValue)));
            updateTransferFunctionPanel = true;
        }
    }

    private void onColorSliderValueChanged(ChangeEvent e) {
        if (changeMinMaxValue) {
            int min = jSliderValue.getValue();
            int max = jSliderValue.getUpperValue();
            minValue = min * (maxData - minData) / 100 + minData;
            maxValue = max * (maxData - minData) / 100 + minData;
            jTextFieldMinValue.setText(decimalFormat.format(minValue));
            jTextFieldMaxValue.setText(decimalFormat.format(maxValue));
        }

        setMinMaxValue();
    }

    private void onColorTransferFunctionChanged(TransferFunctionChangedEvent e) {
        if (this.dataset != null) {
            if (data == null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
            }

            if (graphic == null) {
                graphic = createGraphic();
            }
            else {
                updateGraphicColor();
            }
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }
}
