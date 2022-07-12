package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicCollection3D;
import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.data.dimarray.DimArray;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.LegendManage;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.legend.ColorBreak;
import org.meteoinfo.geometry.legend.LegendScheme;
import org.meteoinfo.geometry.legend.PolylineBreak;
import org.meteoinfo.geometry.legend.StreamlineBreak;
import org.meteoinfo.geometry.shape.ShapeTypes;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteothink.weather.data.Dataset;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class StreamlinePanel extends LayerPanel implements ItemListener {

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/RenderDockable");

    JTabbedPane jTabbedPane;
    JPanel jPanelVariable;
    JPanel jPanelRendering;

    JPanel jPanelVariableSelection;
    JLabel jLabelVariableDimension;
    JComboBox jComboBoxVariableDimension;
    JLabel jLabelXField;
    JComboBox jComboBoxXField;
    JLabel jLabelYField;
    JComboBox jComboBoxYField;
    JLabel jLabelZField;
    JComboBox jComboBoxZField;
    JLabel jLabelColorVariable;
    JComboBox jComboBoxColorVariable;

    JPanel jPanelDataSetting;
    JLabel jLabelXSkip;
    JTextField jTextFieldXSkip;
    JLabel jLabelYSkip;
    JTextField jTextFieldYSkip;
    JLabel jLabelZDataScale;
    JTextField jTextFieldZDataScale;

    JPanel jPanelSlice;
    JLabel jLabelSliceXYZ;
    JRadioButton jRadioButtonX;
    JRadioButton jRadioButtonY;
    JRadioButton jRadioButtonZ;
    ButtonGroup buttonGroupXYZ;
    JSlider jSliderSliceValue;

    Graphic graphic;
    DimArray uArray, vArray, wArray, dataArray;
    int xSkip = 1, ySkip = 1, zDataScale=10;

    boolean isLoading=false;

    /**
     * Constructor
     */
    public StreamlinePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder(bundle.getString("RenderDockable.streamlinePanel.border.title"));
        this.setBorder(border);

        initComponents();
    }

    private void initComponents() {
        jTabbedPane = new JTabbedPane();
        jPanelVariable = new JPanel();
        jPanelRendering = new JPanel();

        //Variable selection panel
        jPanelVariableSelection = new JPanel();
        Border border = BorderFactory.createTitledBorder(bundle.getString("RenderDockable.streamlinePanel.jPanelVariableSelection.border.title"));
        jPanelVariableSelection.setBorder(border);

        jLabelVariableDimension = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelVariableDimension.text"));

        jComboBoxVariableDimension = new JComboBox();
        jComboBoxVariableDimension.addItem("3D");
        jComboBoxVariableDimension.addItem("2D");
        jComboBoxVariableDimension.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onVariableDimensionChanged(e);
            }
        });

        jLabelXField = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelXField.text"));
        jComboBoxXField = new JComboBox();
        jComboBoxXField.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onXFieldChanged(e);
            }
        });

        jLabelYField = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelYField.text"));
        jComboBoxYField = new JComboBox();
        jComboBoxYField.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onYFieldChanged(e);
            }
        });

        jLabelZField = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelZField.text"));
        jComboBoxZField = new JComboBox();
        jComboBoxZField.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onZFieldChanged(e);
            }
        });

        jLabelColorVariable = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelColorVariable.text"));
        jComboBoxColorVariable = new JComboBox();
        jComboBoxColorVariable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onColorVariableChanged(e);
            }
        });

        GroupLayout layout = new GroupLayout(jPanelVariableSelection);
        jPanelVariableSelection.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelVariableDimension)
                                .addComponent(jComboBoxVariableDimension))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelXField)
                                .addComponent(jComboBoxXField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelYField)
                                .addComponent(jComboBoxYField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelZField)
                                .addComponent(jComboBoxZField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelColorVariable)
                                .addComponent(jComboBoxColorVariable))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelVariableDimension)
                                .addComponent(jComboBoxVariableDimension))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelXField)
                                .addComponent(jComboBoxXField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelYField)
                                .addComponent(jComboBoxYField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelZField)
                                .addComponent(jComboBoxZField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelColorVariable)
                                .addComponent(jComboBoxColorVariable))
        );

        //Data setting panel
        jPanelDataSetting = new JPanel();
        jPanelDataSetting.setBorder(BorderFactory.createTitledBorder(bundle.getString("RenderDockable.streamlinePanel.jPanelDataSetting.border.title")));
        jLabelXSkip = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelXSkip.text"));
        jTextFieldXSkip = new JTextField("1");
        jTextFieldXSkip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xSkip = Integer.parseInt(jTextFieldXSkip.getText());
                if (!isLoading)
                    updateGraphic();
            }
        });
        jLabelYSkip = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelYSkip.text"));
        jTextFieldYSkip = new JTextField("1");
        jTextFieldYSkip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ySkip = Integer.parseInt(jTextFieldYSkip.getText());
                if (!isLoading)
                    updateGraphic();
            }
        });
        jLabelZDataScale = new JLabel(bundle.getString("RenderDockable.streamlinePanel.jLabelZDataScale.text"));
        jTextFieldZDataScale = new JTextField("10");
        jTextFieldZDataScale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int oldZScale = zDataScale;
                zDataScale = Integer.parseInt(jTextFieldZDataScale.getText());
                float scale = 1.0f * zDataScale / oldZScale;
                if (wArray != null) {
                    wArray.setArray(ArrayMath.mul(wArray.getArray(), scale));
                }
                if (!isLoading)
                    updateGraphic();
            }
        });

        layout = new GroupLayout(jPanelDataSetting);
        jPanelDataSetting.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelXSkip)
                                .addComponent(jTextFieldXSkip)
                                .addComponent(jLabelYSkip)
                                .addComponent(jTextFieldYSkip))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelZDataScale)
                                .addComponent(jTextFieldZDataScale))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelXSkip)
                                .addComponent(jTextFieldXSkip)
                                .addComponent(jLabelYSkip)
                                .addComponent(jTextFieldYSkip))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelZDataScale)
                                .addComponent(jTextFieldZDataScale))
        );

        //Variable panel
        layout = new GroupLayout(jPanelVariable);
        jPanelVariable.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(jPanelVariableSelection)
                        .addComponent(jPanelDataSetting)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jPanelVariableSelection)
                        .addComponent(jPanelDataSetting)
        );
        jTabbedPane.addTab(bundle.getString("RenderDockable.streamlinePanel.jTabbedPane.jPanelVariable.title"), jPanelVariable);

        //Slice panel
        jPanelSlice = new JPanel();
        jPanelSlice.setBorder(BorderFactory.createTitledBorder(bundle.getString("RenderDockable.streamlinePanel.jPanelSlice.border.title")));
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
        jSliderSliceValue = new JSlider();
        jSliderSliceValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onLocationSliderValueChanged(e);
            }
        });

        layout = new GroupLayout(jPanelSlice);
        jPanelSlice.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
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
                                .addComponent(jLabelSliceXYZ)
                                .addComponent(jRadioButtonX)
                                .addComponent(jRadioButtonY)
                                .addComponent(jRadioButtonZ))
                        .addComponent(jSliderSliceValue)
        );

        //Rendering panel
        layout = new GroupLayout(jPanelRendering);
        jPanelRendering.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(jPanelSlice)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jPanelSlice)
        );
        jTabbedPane.addTab(bundle.getString("RenderDockable.streamlinePanel.jTabbedPane.jPanelRendering.title"), jPanelRendering);

        //Layout
        layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(jTabbedPane)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jTabbedPane)
        );
    }

    @Override
    public void setDataset(Dataset dataset) {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (this.dataset != null) {
            if (this.dataset.getFileName() == dataset.getFileName()) {
                jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
        }
        this.dataset = dataset;
        int xn = (int) dataset.getXArray().getSize();
        int yn = (int) dataset.getYArray().getSize();
        if (xn > 50)
            xSkip = xn / 50;
        if (yn > 50)
            ySkip = yn / 50;
        this.jTextFieldXSkip.setText(String.valueOf(xSkip));
        this.jTextFieldYSkip.setText(String.valueOf(ySkip));

        this.jComboBoxVariableDimension.removeAllItems();
        jComboBoxVariableDimension.addItem("3D");
        jComboBoxVariableDimension.addItem("2D");
        this.jComboBoxVariableDimension.setSelectedItem("3D");

        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
            LegendScheme ls = LegendManage.createLegendScheme(ArrayMath.min(dataArray.getArray()).doubleValue(),
                    ArrayMath.max(dataArray.getArray()).doubleValue(), 10);
            ls = ls.convertTo(ShapeTypes.POLYLINE);
            for (int i = 0; i < ls.getBreakNum(); i++) {
                StreamlineBreak lb = new StreamlineBreak((PolylineBreak) ls.getLegendBreak(i));
                lb.setInterval(30);
                //lb.setWidth(1.5f);
                lb.setArrowHeadLength(2.5f);
                lb.setArrowHeadWidth(1.0f);
                ls.setLegendBreak(i, lb);
            }
            int density = 4;
            if (this.jRadioButtonX.isSelected() || this.jRadioButtonY.isSelected()) {
                density = 10;
            }
            List graphics = GraphicFactory.streamSlice(uArray.getXDimension().getDimValue(), uArray.getYDimension().getDimValue(),
                    uArray.getZDimension().getDimValue(), uArray.getArray(), vArray.getArray(), wArray.getArray(),
                    dataArray.getArray(), xSlice, ySlice, zSlice, density, ls);
            GraphicCollection3D graphic = (GraphicCollection3D) graphics.get(0);
            graphic.setUsingLight(false);

            jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            return graphic;
        } catch (InvalidRangeException e) {
            e.printStackTrace();
            jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return null;
        }
    }

    private void updateGraphic() {
        graphic = getGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }

    private void onVariableDimensionChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            isLoading = true;
            boolean is3D = this.jComboBoxVariableDimension.getSelectedItem().toString().equals("3D");
            this.jComboBoxXField.removeAllItems();
            this.jComboBoxYField.removeAllItems();
            this.jComboBoxZField.removeAllItems();
            this.jComboBoxColorVariable.removeAllItems();
            if (is3D) {
                for (Variable variable : dataset.get3DVariables()) {
                    this.jComboBoxXField.addItem(variable.getName());
                }
                Variable u = dataset.findUVariable(true);
                if (u != null) {
                    this.jComboBoxXField.setSelectedItem(u.getName());
                }

                for (Variable variable : dataset.get3DVariables()) {
                    this.jComboBoxYField.addItem(variable.getName());
                }
                Variable v = dataset.findVVariable(true);
                if (v != null) {
                    this.jComboBoxYField.setSelectedItem(v.getName());
                }

                for (Variable variable : dataset.get3DVariables()) {
                    this.jComboBoxZField.addItem(variable.getName());
                }
                Variable w = dataset.findWVariable();
                if (w != null) {
                    this.jComboBoxZField.setSelectedItem(w.getName());
                }

                for (Variable variable : dataset.get3DVariables()) {
                    this.jComboBoxColorVariable.addItem(variable.getName());
                }
            } else {
                for (Variable variable : dataset.get2DVariables()) {
                    this.jComboBoxXField.addItem(variable.getName());
                }
                Variable u = dataset.findUVariable(false);
                if (u != null) {
                    this.jComboBoxXField.setSelectedItem(u.getName());
                }

                for (Variable variable : dataset.get2DVariables()) {
                    this.jComboBoxYField.addItem(variable.getName());
                }
                Variable v = dataset.findVVariable(false);
                if (v != null) {
                    this.jComboBoxYField.setSelectedItem(v.getName());
                }

                for (Variable variable : dataset.get2DVariables()) {
                    this.jComboBoxColorVariable.addItem(variable.getName());
                }
            }
            isLoading = false;
            updateGraphic();
        }
    }

    private void onXFieldChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String uName = this.jComboBoxXField.getSelectedItem().toString();
            if (this.xSkip > 1 || this.ySkip > 1)
                this.uArray = this.dataset.read3DArray(uName, xSkip, ySkip);
            else
                this.uArray = this.dataset.read3DArray(uName);

            if (!isLoading)
                updateGraphic();
        }
    }

    private void onYFieldChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String vName = this.jComboBoxYField.getSelectedItem().toString();
            if (this.xSkip > 1 || this.ySkip > 1)
                this.vArray = this.dataset.read3DArray(vName, xSkip, ySkip);
            else
                this.vArray = this.dataset.read3DArray(vName);

            if (!isLoading)
                updateGraphic();
        }
    }

    private void onZFieldChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String wName = this.jComboBoxZField.getSelectedItem().toString();
            if (this.xSkip > 1 || this.ySkip > 1)
                this.wArray = this.dataset.read3DArray(wName, xSkip, ySkip);
            else
                this.wArray = this.dataset.read3DArray(wName);

            if (zDataScale != 1) {
                this.wArray.setArray(ArrayMath.mul(this.wArray.getArray(), zDataScale));
            }

            if (!isLoading)
                updateGraphic();
        }
    }

    private void onColorVariableChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String dataName = this.jComboBoxColorVariable.getSelectedItem().toString();
            if (this.xSkip > 1 || this.ySkip > 1)
                this.dataArray = this.dataset.read3DArray(dataName, xSkip, ySkip);
            else
                this.dataArray = this.dataset.read3DArray(dataName);
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

    private void onLocationSliderValueChanged(ChangeEvent e) {
        //TransferFunction transferFunction = this.transferFunctionPanel.getTransferFunction();
        graphic = getGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
