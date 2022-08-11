package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.chart.graphic.MeshGraphic;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.data.dimarray.DimArray;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.LegendManage;
import org.meteoinfo.geometry.colors.Normalize;
import org.meteoinfo.geometry.colors.TransferFunction;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.legend.LegendScheme;
import org.meteoinfo.geometry.shape.ShapeTypes;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.ui.slider.RangeSlider;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.event.TransferFunctionChangedEvent;
import org.meteothink.weather.event.TransferFunctionChangedListener;
import org.meteothink.weather.plot.OpacityControlPoint;
import org.meteothink.weather.plot.TransferFunctionPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DataSlicePanel extends LayerPanel implements ItemListener {

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/RenderDockable");
    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    TransferFunctionPanel transferFunctionPanel;
    JLabel jLabelDataValue;
    JTextField jTextFieldDataValue;
    JLabel jLabelOpacity;
    JTextField jTextFieldOpacity;
    RangeSlider jSliderValue;
    JTextField jTextFieldMinValue;
    JTextField jTextFieldMaxValue;
    JLabel jLabelSliceXYZ;
    JRadioButton jRadioButtonX;
    JRadioButton jRadioButtonY;
    JRadioButton jRadioButtonZ;
    ButtonGroup buttonGroupXYZ;
    JSlider jSliderSliceValue;

    DimArray data;
    double minData = 0;
    double maxData = 1;
    double minValue = 0;
    double maxValue = 1;
    DecimalFormat decimalFormat = new DecimalFormat("#.##E0");
    MeshGraphic graphic;
    boolean changeMinMaxValue = true;
    boolean updateTransferFunctionPanel = true;

    /**
     * Constructor
     * @param layer The plot layer
     * @param colorMaps The color maps
     */
    public DataSlicePanel(PlotLayer layer, ColorMap[] colorMaps) {
        super(layer, colorMaps);

        Border border = BorderFactory.createTitledBorder(bundle.getString("RenderDockable.dataSlicePanel.border.title"));
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
        ColorMap ct = ColorUtil.findColorTable(colorMaps, "matlab_jet");
        if (this.data == null)
            transferFunctionPanel = new TransferFunctionPanel(null, ct, colorMaps, 1.0f, 1);
        else
            transferFunctionPanel = new TransferFunctionPanel(this.data.getArray(), ct, colorMaps, 1.0f, 1);
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
                } else {
                    OpacityControlPoint ocp = transferFunctionPanel.getSelectedOCP();
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
                        double value = ocp.getValue(minValue, maxValue);
                        jTextFieldDataValue.setText(decimalFormat.format(value));
                        jLabelOpacity.setEnabled(true);
                        jTextFieldOpacity.setEnabled(true);
                        jTextFieldOpacity.setText(new DecimalFormat("#.##").format(ocp.getOpacity()));
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
                OpacityControlPoint ocp = transferFunctionPanel.getSelectedOCP();
                if (ocp != null) {
                    jLabelDataValue.setEnabled(true);
                    jTextFieldDataValue.setEnabled(true);
                    double value = ocp.getValue(minValue, maxValue);
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
                onOpacityChanged(e);
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
                onSliderValueChanged(e);
            }
        });
        jTextFieldMinValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minValue = Double.parseDouble(jTextFieldMinValue.getText());
                if (minValue < minData) {
                    minValue = minData;
                    jTextFieldMinValue.setText(decimalFormat.format(minValue));
                }
                double min = (minValue - minData) / (maxData - minData) * 100;
                changeMinMaxValue = false;
                jSliderValue.setValue((int) min);
                changeMinMaxValue = true;
            }
        });
        jTextFieldMaxValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maxValue = Double.parseDouble(jTextFieldMaxValue.getText());
                if (maxValue > maxData) {
                    maxValue = maxData;
                    jTextFieldMaxValue.setText(decimalFormat.format(maxValue));
                }
                double max = (maxValue - minData) / (maxData - minData) * 100;
                changeMinMaxValue = false;
                jSliderValue.setUpperValue((int) max);
                changeMinMaxValue = true;
            }
        });

        //Slice direction
        jLabelSliceXYZ = new JLabel(bundle.getString("RenderDockable.dataSlicePanel.jLabelSliceXYZ"));
        buttonGroupXYZ = new ButtonGroup();
        jRadioButtonX = new JRadioButton("X", true);
        jRadioButtonY = new JRadioButton("Y");
        jRadioButtonZ = new JRadioButton("Z");
        jRadioButtonX.addItemListener(this);
        jRadioButtonY.addItemListener(this);
        jRadioButtonZ.addItemListener(this);
        buttonGroupXYZ.add(jRadioButtonX);
        buttonGroupXYZ.add(jRadioButtonY);
        buttonGroupXYZ.add(jRadioButtonZ);

        //Value slider
        jSliderSliceValue = new JSlider();
        jSliderSliceValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onLocationSliderValueChanged(e);
            }
        });

        //Layout
        GroupLayout layout = new GroupLayout(this);
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
                                .addComponent(jLabelDataValue)
                                .addComponent(jTextFieldDataValue)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelOpacity)
                                .addComponent(jTextFieldOpacity))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldMinValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSliderValue)
                                .addComponent(jTextFieldMaxValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelSliceXYZ)
                                .addComponent(jRadioButtonX)
                                .addComponent(jRadioButtonY)
                                .addComponent(jRadioButtonZ))
                        .addComponent(jSliderSliceValue)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addComponent(transferFunctionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelDataValue)
                                .addComponent(jTextFieldDataValue)
                                .addComponent(jLabelOpacity)
                                .addComponent(jTextFieldOpacity))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(jTextFieldMinValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSliderValue)
                                .addComponent(jTextFieldMaxValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelSliceXYZ)
                                .addComponent(jRadioButtonX)
                                .addComponent(jRadioButtonY)
                                .addComponent(jRadioButtonZ))
                        .addComponent(jSliderSliceValue)
        );
    }

    @Override
    public void setDataset(Dataset dataset) {
        if (this.dataset != null) {
            if (this.dataset.getFileName() == dataset.getFileName()) {
                return;
            }
        }
        this.dataset = dataset;

        List<Variable> variables3D = get3DVariables();
        this.jComboBoxVariable.removeAllItems();
        for (Variable variable : variables3D) {
            this.jComboBoxVariable.addItem(variable.getName());
        }
        if (this.jComboBoxVariable.getItemCount() > 0) {
            this.jComboBoxVariable.setSelectedIndex(0);
        }
    }

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        ColorMap colorMap = this.transferFunctionPanel.getColorMap();
        return createGraphic(colorMap);
    }

    private MeshGraphic createGraphic(ColorMap colorMap) {
        if (this.dataset == null) {
            return null;
        }

        if (data == null) {
            String varName = (String) this.jComboBoxVariable.getSelectedItem();
            data = this.dataset.read3DArray(varName);
        }
        double vMin = ArrayMath.min(data.getArray()).doubleValue();
        double vMax = ArrayMath.max(data.getArray()).doubleValue();
        Normalize normalize = new Normalize(vMin, vMax, true);
        List<Number> opacityLevels = new ArrayList<>();
        opacityLevels.add(0.0f);
        opacityLevels.add(1.0f);
        LegendScheme ls = LegendManage.createLegendScheme(vMin, vMax, colorMap);
        ls = ls.convertTo(ShapeTypes.POLYGON);
        int v = this.jSliderSliceValue.getValue();
        List<Number> xSlice = new ArrayList<>();
        List<Number> ySlice = new ArrayList<>();
        List<Number> zSlice = new ArrayList<>();
        if (this.jRadioButtonX.isSelected()) {
            xSlice.add(v / 100. * (dataset.getXMax() - dataset.getXMin()) + dataset.getXMin());
        } else if (this.jRadioButtonY.isSelected()) {
            ySlice.add(v / 100. * (dataset.getYMax() - dataset.getYMin()) + dataset.getYMin());
        } else if (this.jRadioButtonZ.isSelected()) {
            zSlice.add(v / 100. * (dataset.getZMax() - dataset.getZMin()) + dataset.getZMin());
        }

        try {
            List graphics = GraphicFactory.slice(data.getArray(), dataset.getXArray(), dataset.getYArray(),
                    dataset.getZArray(), xSlice, ySlice, zSlice, ls);
            MeshGraphic graphic = (MeshGraphic) graphics.get(0);
            graphic.setFaceInterp(true);
            return graphic;
        } catch (InvalidRangeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private MeshGraphic createGraphic(TransferFunction transferFunction) {
        if (this.dataset == null) {
            return null;
        }

        if (data == null) {
            String varName = (String) this.jComboBoxVariable.getSelectedItem();
            readDataArray(varName);
        }

        int v = this.jSliderSliceValue.getValue();
        List<Number> xSlice = new ArrayList<>();
        List<Number> ySlice = new ArrayList<>();
        List<Number> zSlice = new ArrayList<>();
        if (this.jRadioButtonX.isSelected()) {
            xSlice.add(v / 100. * (dataset.getXMax() - dataset.getXMin()) + dataset.getXMin());
        } else if (this.jRadioButtonY.isSelected()) {
            ySlice.add(v / 100. * (dataset.getYMax() - dataset.getYMin()) + dataset.getYMin());
        } else if (this.jRadioButtonZ.isSelected()) {
            zSlice.add(v / 100. * (dataset.getZMax() - dataset.getZMin()) + dataset.getZMin());
        }

        try {
            List graphics = GraphicFactory.slice(data.getArray(), dataset.getXArray(), dataset.getYArray(),
                    dataset.getZArray(), xSlice, ySlice, zSlice, transferFunction);
            if (graphics.isEmpty())
                return null;

            MeshGraphic graphic = (MeshGraphic) graphics.get(0);
            graphic.setFaceInterp(true);
            graphic.setUsingLight(false);
            return graphic;
        } catch (InvalidRangeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
                ColorMap colorMap = this.transferFunctionPanel.getColorMap();
                Graphic graphic = createGraphic(colorMap);
                if (graphic != null) {
                    this.layer.fileGraphicChangedEvent(graphic);
                }
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof JRadioButton) {
            if (((JRadioButton) e.getSource()).isSelected()) {
                int value = this.jSliderSliceValue.getValue();
                this.jSliderSliceValue.setValue(50);
                if (value == 50) {
                    this.onLocationSliderValueChanged(null);
                }
            }
        }
    }

    private void readDataArray(String varName) {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        data = dataset.read3DArray(varName);
        this.minData = ArrayMath.min(data.getArray()).doubleValue();
        this.maxData = ArrayMath.max(data.getArray()).doubleValue();
        this.minValue = this.minData;
        this.maxValue = this.maxData;
        this.jSliderValue.setValue(0);
        this.jSliderValue.setUpperValue(100);
        this.jTextFieldMinValue.setText(decimalFormat.format(minData));
        this.jTextFieldMaxValue.setText(decimalFormat.format(maxData));
        this.transferFunctionPanel.setData(data.getArray());
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void onTransferFunctionChanged(TransferFunctionChangedEvent e) {
        if (this.dataset != null) {
            if (data == null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
            }

            TransferFunction transferFunction = this.transferFunctionPanel.getTransferFunction();
            if (graphic == null) {
                graphic = createGraphic(transferFunction);
            }
            else {
                graphic.setTransferFunction(transferFunction);
            }
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }

    private void onDataValueChanged(ActionEvent e) {
        if (updateTransferFunctionPanel) {
            OpacityControlPoint ocp = transferFunctionPanel.getSelectedOCP();
            if (ocp != null) {
                double v = Double.parseDouble(jTextFieldDataValue.getText());
                float ratio = (float) ((v - minValue) / (maxValue - minValue));
                if (v < ocp.getMinRatio()) {
                    jTextFieldDataValue.setText(decimalFormat.format(ratio));
                    return;
                }
                ocp.setRatio(ratio);
                transferFunctionPanel.repaint();
                transferFunctionPanel.fileTransferFunctionChangedEvent();
            }
        }
    }

    private void onOpacityChanged(ActionEvent e) {
        if (updateTransferFunctionPanel) {
            OpacityControlPoint ocp = transferFunctionPanel.getSelectedOCP();
            if (ocp != null) {
                float opacity = Float.parseFloat(jTextFieldOpacity.getText());
                if (opacity < 0) {
                    jTextFieldOpacity.setText(new DecimalFormat("#.##").format(0));
                    return;
                } else if (opacity > 1) {
                    jTextFieldOpacity.setText(new DecimalFormat("#.##").format(1));
                }
                ocp.setOpacity(opacity);
                transferFunctionPanel.repaint();
                transferFunctionPanel.fileTransferFunctionChangedEvent();
            }
        }
    }

    private void setMinMaxValue() {
        transferFunctionPanel.setMinValue(minValue);
        transferFunctionPanel.setMaxValue(maxValue);
        OpacityControlPoint ocp = transferFunctionPanel.getSelectedOCP();
        if (ocp != null) {
            updateTransferFunctionPanel = false;
            this.jTextFieldDataValue.setText(decimalFormat.format(ocp.getValue(minValue, maxValue)));
            updateTransferFunctionPanel = true;
        }
    }

    private void onSliderValueChanged(ChangeEvent e) {
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

    private void onLocationSliderValueChanged(ChangeEvent e) {
        TransferFunction transferFunction = this.transferFunctionPanel.getTransferFunction();
        graphic = createGraphic(transferFunction);
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
