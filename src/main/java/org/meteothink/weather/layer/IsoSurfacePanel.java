package org.meteothink.weather.layer;

import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.graphic.GraphicCollection;
import org.meteoinfo.geometry.legend.PolygonBreak;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
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

    /**
     * Constructor
     */
    public IsoSurfacePanel(PlotLayer layer, MeteoDataInfo meteoDataInfo) {
        super(layer, meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("等值面设置");
        this.setBorder(border);

        initComponents();

        this.setMeteoDataInfo(meteoDataInfo);
    }

    private void initComponents() {
        //Variable
        this.jLabelVariable = new JLabel("变量:");
        this.jComboBoxVariable = new JComboBox();
        this.add(jLabelVariable);
        this.add(jComboBoxVariable);
        jComboBoxVariable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onVariableChanged(e);
            }
        });

        //Value
        this.jLabelValue = new JLabel("等值面值:");
        this.jTextFieldValue = new JTextField(10);
        jTextFieldValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    onValueChanged();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        this.add(jLabelValue);
        this.add(jTextFieldValue);

        //Color
        jLabelColor = new JLabel("颜色:");
        jLabelColorView = new JLabel();
        jLabelColorView.setOpaque(true);
        jLabelColorView.setBackground(Color.white);
        jLabelColorView.setPreferredSize(new Dimension(50, 20));
        jLabelColorView.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onColorActionPerformed(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(jLabelColor);
        this.add(jLabelColorView);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        if (this.meteoDataInfo != null) {
            if (this.meteoDataInfo.getFileName() == dataInfo.getFileName()) {
                return;
            }
        }
        this.meteoDataInfo = dataInfo;

        List<Variable> variables3D = get3DVariables();
        this.jComboBoxVariable.removeAllItems();
        for (Variable variable : variables3D) {
            this.jComboBoxVariable.addItem(variable.getName());
        }
        if (this.jComboBoxVariable.getItemCount() > 0) {
            this.jComboBoxVariable.setSelectedIndex(0);
        }

        try {
            Array arr3d = DataUtil.read3DArray(dataInfo, (String) this.jComboBoxVariable.getSelectedItem(), 0);
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
        if (this.meteoDataInfo == null) {
            return null;
        }

        String varName = (String) this.jComboBoxVariable.getSelectedItem();
        float value = Float.parseFloat(this.jTextFieldValue.getText());
        PolygonBreak pb = new PolygonBreak();
        pb.setColor(this.jLabelColorView.getBackground());
        pb.setDrawOutline(false);

        if (data == null) {
            try {
                data = DataUtil.read3DArray(meteoDataInfo, varName, 0);
            } catch (InvalidRangeException e) {
                e.printStackTrace();
            }
        }
        Array[] xyz = DataUtil.readXYZArray(meteoDataInfo);
        GraphicCollection graphic = JOGLUtil.isosurface(data, xyz[0], xyz[1], xyz[2], value, pb, 4);
        return graphic;
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.meteoDataInfo != null) {
                try {
                    Array arr3d = DataUtil.read3DArray(this.meteoDataInfo, (String) this.jComboBoxVariable.getSelectedItem(), 0);
                    float mean = (float) ArrayMath.mean(arr3d);
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
