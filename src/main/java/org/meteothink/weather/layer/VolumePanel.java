package org.meteothink.weather.layer;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.meteoinfo.chart.graphic.VolumeGraphics;
import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.chart.render.TransferFunction;
import org.meteoinfo.chart.render.jogl.RayCastingType;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.FrmLegendBreaks;
import org.meteoinfo.geometry.colors.Normalize;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.ui.ColorComboBoxModel;
import org.meteoinfo.ui.ColorListCellRender;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolumePanel extends LayerPanel {

    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    JComboBox jComboBoxColorMap;
    TransferFunctionPanel transferFunctionPanel;
    JLabel jLabelDataValue;
    JTextField jTextFieldDataValue;
    JLabel jLabelOpacity;
    JTextField jTextFieldOpacity;
    RangeSlider jSliderValue;
    JTextField jTextFieldMinValue;
    JTextField jTextFieldMaxValue;
    JLabel jLabelRayCasting;
    JComboBox jComboBoxRayCasting;
    JLabel jLabelBrightness;
    JSpinner jSpinnerBrightness;

    Array data;
    double minData = 0;
    double maxData = 1;
    double minValue = 0;
    double maxValue = 1;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    VolumeGraphics graphic;
    boolean changeMinMaxValue = true;
    boolean updateTransferFunctionPanel = true;

    /**
     * Constructor
     */
    public VolumePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("体绘制设置");
        this.setBorder(border);

        initComponents();
    }

    private void initComponents() {
        //Variable
        this.jLabelVariable = new JLabel("变量:");
        this.jComboBoxVariable = new JComboBox();
        jComboBoxVariable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onVariableChanged(e);
            }
        });

        //Color map
        jComboBoxColorMap = new JComboBox();
        jComboBoxColorMap.setPreferredSize(new Dimension(200, 20));
        ColorMap[] colorTables;
        try {
            colorTables = ColorUtil.getColorTables();
            ColorListCellRender render = new ColorListCellRender();
            render.setPreferredSize(new Dimension(62, 21));
            this.jComboBoxColorMap.setModel(new ColorComboBoxModel(colorTables));
            this.jComboBoxColorMap.setRenderer(render);
            ColorMap ct = ColorUtil.findColorTable(colorTables, "matlab_jet");
            if (ct != null) {
                this.jComboBoxColorMap.setSelectedItem(ct);
            } else {
                this.jComboBoxColorMap.setSelectedIndex(0);
            }
            transferFunctionPanel = new TransferFunctionPanel(this.data, ct);
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
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            jComboBoxColorMap.setLocation(transferFunctionPanel.getX(), transferFunctionPanel.getY() +
                                    transferFunctionPanel.getHeight() - 20);
                            jComboBoxColorMap.setSize(transferFunctionPanel.getWidth(), 20);
                            jComboBoxColorMap.showPopup();
                        }
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
                            jTextFieldOpacity.setText(decimalFormat.format(ocp.getOpacity()));
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
                        jTextFieldOpacity.setText(decimalFormat.format(ocp.getOpacity()));
                    }
                    updateTransferFunctionPanel = true;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(FrmLegendBreaks.class.getName()).log(Level.SEVERE, null, ex);
        }
        jComboBoxColorMap.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onColormapChanged(e);
            }
        });

        jLabelDataValue = new JLabel("数据值:");
        jTextFieldDataValue = new JTextField(5);
        jLabelOpacity = new JLabel("不透明度:");
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

        //Ray casting
        jLabelRayCasting = new JLabel("光线投影:");
        jComboBoxRayCasting = new JComboBox();
        for (RayCastingType rayCastingType : RayCastingType.values()) {
            jComboBoxRayCasting.addItem(rayCastingType);
        }
        jComboBoxRayCasting.setSelectedItem(RayCastingType.MAX_VALUE);
        jComboBoxRayCasting.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onRayCastingChanged(e);
            }
        });

        //Brightness
        jLabelBrightness = new JLabel("亮度:");
        SpinnerModel spinnerModel = new SpinnerNumberModel(1.0f, 0.0f, 2.0f, 0.1f);
        jSpinnerBrightness = new JSpinner(spinnerModel);
        jSpinnerBrightness.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onBrightnessChanged(e);
            }
        });

        //Set layout
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addComponent(jComboBoxColorMap)
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
                                .addComponent(jLabelRayCasting)
                                .addComponent(jComboBoxRayCasting)
                                .addComponent(jLabelBrightness)
                                .addComponent(jSpinnerBrightness))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addComponent(jComboBoxColorMap, 0, 0, 0)
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
                                .addComponent(jLabelRayCasting)
                                .addComponent(jComboBoxRayCasting)
                                .addComponent(jLabelBrightness)
                                .addComponent(jSpinnerBrightness))
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
        return createGraphic(colorMap, null);
    }

    private VolumeGraphics createGraphic(ColorMap colorMap, TransferFunction transferFunction) {
        if (this.dataset == null) {
            return null;
        }

        if (data == null) {
            String varName = (String) this.jComboBoxVariable.getSelectedItem();
            readDataArray(varName);
        }
        Normalize normalize = new Normalize(this.minData, this.maxData, true);
        if (transferFunction == null) {
            List<Number> opacityLevels = new ArrayList<>();
            opacityLevels.add(0.0f);
            opacityLevels.add(1.0f);
            transferFunction = new TransferFunction(null, opacityLevels, normalize);
        }
        graphic = (VolumeGraphics) JOGLUtil.volume(data, dataset.getXArray(), dataset.getYArray(),
                dataset.getZArray(), colorMap, normalize, transferFunction);
        return graphic;
    }

    private void readDataArray(String varName) {
        try {
            data = dataset.read3DArray(varName, 0);
            this.minData = ArrayMath.min(data).doubleValue();
            this.maxData = ArrayMath.max(data).doubleValue();
            this.minValue = this.minData;
            this.maxValue = this.maxData;
            this.jSliderValue.setValue(0);
            this.jSliderValue.setUpperValue(100);
            this.jTextFieldMinValue.setText(decimalFormat.format(minData));
            this.jTextFieldMaxValue.setText(decimalFormat.format(maxData));
            this.transferFunctionPanel.setData(data);
        } catch (InvalidRangeException ex) {
            ex.printStackTrace();
        }
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
                ColorMap colorMap = this.transferFunctionPanel.getColorMap();
                VolumeGraphics graphic = createGraphic(colorMap, null);
                if (graphic != null) {
                    graphic.setRayCastingType((RayCastingType) this.jComboBoxRayCasting.getSelectedItem());
                    graphic.setBrightness(Float.valueOf(this.jSpinnerBrightness.getValue().toString()));
                    this.layer.fileGraphicChangedEvent(graphic);
                }
            }
        }
    }

    private void onColormapChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            ColorMap colorMap = (ColorMap) this.jComboBoxColorMap.getSelectedItem();
            this.transferFunctionPanel.setColorMap(colorMap);
            this.transferFunctionPanel.updateUI();
            this.transferFunctionPanel.fileTransferFunctionChangedEvent();
        }
        this.jComboBoxColorMap.setSize(this.jComboBoxColorMap.getWidth(), 0);
    }

    private void onTransferFunctionChanged(TransferFunctionChangedEvent e) {
        if (this.dataset != null) {
            if (data == null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
            }
            ColorMap colorMap = this.transferFunctionPanel.getColorMap();
            TransferFunction transferFunction = this.transferFunctionPanel.getTransferFunction();
            if (graphic == null) {
                graphic = createGraphic(colorMap, transferFunction);
            }
            else {
                graphic.setColorMap(colorMap);
                graphic.setTransferFunction(transferFunction);
                graphic.updateColors();
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
                    jTextFieldOpacity.setText(decimalFormat.format(0));
                    return;
                } else if (opacity > 1) {
                    jTextFieldOpacity.setText(decimalFormat.format(1));
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

    private void onRayCastingChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            RayCastingType rayCastingType = (RayCastingType) this.jComboBoxRayCasting.getSelectedItem();
            if (graphic != null) {
                graphic.setRayCastingType(rayCastingType);
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }

    private void onBrightnessChanged(ChangeEvent e) {
        double brightness = (double) this.jSpinnerBrightness.getValue();
        if (graphic != null) {
            graphic.setBrightness((float) brightness);
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
