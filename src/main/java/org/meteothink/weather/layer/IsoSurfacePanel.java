package org.meteothink.weather.layer;

import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.layout.LayoutGraphic;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.graphic.GraphicCollection;
import org.meteoinfo.geometry.legend.PolygonBreak;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.util.DataUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class IsoSurfacePanel extends LayerPanel {

    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    JLabel jLabelValue;
    JTextField jTextFieldValue;
    JLabel jLabelColor;
    JLabel jLabelColorView;

    Array data;

    /**
     * Constructor
     *
     * @param layer The plot layer
     */
    public IsoSurfacePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("等值面设置");
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

        //Value
        this.jLabelValue = new JLabel("等值面值:");
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
        jLabelColor = new JLabel("颜色:");
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

        //Set layout
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelVariable)
                        .addComponent(jLabelValue)
                        .addComponent(jLabelColor))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBoxVariable)
                        .addComponent(jTextFieldValue)
                        .addComponent(jLabelColorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelVariable)
                        .addComponent(jComboBoxVariable))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelValue)
                        .addComponent(jTextFieldValue))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelColor)
                        .addComponent(jLabelColorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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

        try {
            Array arr3d = this.dataset.read3DArray((String) this.jComboBoxVariable.getSelectedItem(), 0);
            float mean = (float)ArrayMath.mean(arr3d);
            this.jTextFieldValue.setText(String.valueOf(mean));
        } catch (InvalidRangeException e) {
            e.printStackTrace();
        }
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
        PolygonBreak pb = new PolygonBreak();
        pb.setColor(this.jLabelColorView.getBackground());
        pb.setDrawOutline(false);

        if (data == null) {
            try {
                data = this.dataset.read3DArray(varName, 0);
            } catch (InvalidRangeException e) {
                e.printStackTrace();
            }
        }
        GraphicCollection graphic = JOGLUtil.isosurface(data, dataset.getXArray(), dataset.getYArray(),
                dataset.getZArray(), value, pb, 4);
        return graphic;
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                try {
                    data = this.dataset.read3DArray((String) this.jComboBoxVariable.getSelectedItem(), 0);
                    float mean = (float) ArrayMath.mean(data);
                    this.jTextFieldValue.setText(String.valueOf(mean));
                } catch (InvalidRangeException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void onValueChanged() {
        Graphic graphic = createGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }

    private void onColorActionPerformed(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        Color color = JColorChooser.showDialog(this.getRootPane().getParent(), "选择颜色", label.getBackground());
        if (color != null) {
            this.jLabelColorView.setBackground(color);
        }
        Graphic graphic = createGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
