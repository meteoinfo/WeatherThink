package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.chart.graphic.TriMeshGraphic;
import org.meteoinfo.data.dimarray.DimArray;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.legend.PolygonBreak;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.event.TransferFunctionChangedEvent;
import org.meteothink.weather.event.TransferFunctionChangedListener;
import org.meteothink.weather.plot.ControlPoint;
import org.meteothink.weather.plot.TransferFunctionPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;

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

    DimArray data;
    double minValue = 0;
    double maxValue = 1;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    TriMeshGraphic graphic;
    boolean updateTransferFunctionPanel = true;

    /**
     * Constructor
     *
     * @param layer The plot layer
     */
    public IsoSurfacePanel(PlotLayer layer) {
        super(layer);

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
                } else {
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
                        .addComponent(transferFunctionPanel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelValue)
                                .addComponent(jTextFieldValue))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelColor)
                                .addComponent(jLabelColorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelOpacity)
                                .addComponent(jSliderOpacity))
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

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        return createGraphic();
    }

    private Graphic createGraphic() {
        if (this.dataset == null) {
            return null;
        }

        String varName = (String) this.jComboBoxVariable.getSelectedItem();
        float value = Float.parseFloat(this.jTextFieldValue.getText());
        Color color = this.jLabelColorView.getBackground();
        int opacity = (int) (this.jSliderOpacity.getValue() / 100. * 255);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        PolygonBreak pb = new PolygonBreak();
        pb.setColor(color);
        pb.setDrawOutline(false);

        if (data == null) {
            data = this.dataset.read3DArray(varName);
        }
        graphic = GraphicFactory.isosurface(data.getArray(), data.getXDimension().getDimValue(),
                data.getYDimension().getDimValue(), data.getZDimension().getDimValue(), value, pb, 4);
        return graphic;
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                readDataArray(varName);
                float mean = (float) ArrayMath.mean(data.getArray());
                this.jTextFieldValue.setText(String.valueOf(mean));
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
                graphic = (TriMeshGraphic) createGraphic();
            else
                graphic.setColor(color);
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

            if (graphic == null) {
                graphic = (TriMeshGraphic) createGraphic();
            }
            else {
                graphic = (TriMeshGraphic) createGraphic();
            }
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }

    private void onOpacityChanged(ChangeEvent e) {
        if (graphic == null)
            graphic = (TriMeshGraphic) createGraphic();
        else {
            Color color = this.jLabelColorView.getBackground();
            int opacity = (int) (this.jSliderOpacity.getValue() / 100. * 255);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
            graphic.setColor(color);
        }
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
