package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.SurfaceGraphics;
import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.FrmLegendBreaks;
import org.meteoinfo.geo.legend.LegendManage;
import org.meteoinfo.geometry.colors.Normalize;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.graphic.GraphicCollection;
import org.meteoinfo.geometry.legend.LegendScheme;
import org.meteoinfo.geometry.shape.ShapeTypes;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.ui.ColorComboBoxModel;
import org.meteoinfo.ui.ColorListCellRender;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.util.DataUtil;
import org.python.antlr.ast.Num;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SlicePanel extends LayerPanel implements ItemListener {

    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    JLabel jLabelColorMap;
    JComboBox jComboBoxColorMap;
    JLabel jLabelSliceXYZ;
    JRadioButton jRadioButtonX;
    JRadioButton jRadioButtonY;
    JRadioButton jRadioButtonZ;
    ButtonGroup buttonGroupXYZ;
    JSlider jSliderValue;

    Array data;

    /**
     * Constructor
     */
    public SlicePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("数据切片设置");
        this.setBorder(border);

        initComponents();
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

        //Color map
        jLabelColorMap = new JLabel("颜色方案:");
        this.add(jLabelColorMap);
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
        } catch (IOException ex) {
            Logger.getLogger(FrmLegendBreaks.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.add(jComboBoxColorMap);

        //Slice direction
        jLabelSliceXYZ = new JLabel("切片方向");
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
        this.add(jLabelSliceXYZ);
        this.add(jRadioButtonX);
        this.add(jRadioButtonY);
        this.add(jRadioButtonZ);

        //Value slider
        jSliderValue = new JSlider();
        jSliderValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onSliderValueChanged(e);
            }
        });
        this.add(jSliderValue);
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
        return createGraphic();
    }

    private Graphic createGraphic() {
        if (this.dataset == null) {
            return null;
        }

        if (data == null) {
            try {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                data = this.dataset.read3DArray(varName, 0);
            } catch (InvalidRangeException e) {
                e.printStackTrace();
            }
        }
        ColorMap colorMap = (ColorMap) this.jComboBoxColorMap.getSelectedItem();
        double vMin = ArrayMath.min(data).doubleValue();
        double vMax = ArrayMath.max(data).doubleValue();
        Normalize normalize = new Normalize(vMin, vMax, true);
        List<Number> opacityLevels = new ArrayList<>();
        opacityLevels.add(0.0f);
        opacityLevels.add(1.0f);
        LegendScheme ls = LegendManage.createLegendScheme(vMin, vMax, colorMap);
        ls = ls.convertTo(ShapeTypes.POLYGON);
        int v = this.jSliderValue.getValue();
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
            List graphics = JOGLUtil.slice(data, dataset.getXArray(), dataset.getYArray(),
                    dataset.getZArray(), xSlice, ySlice, zSlice, ls);
            SurfaceGraphics graphic = (SurfaceGraphics) graphics.get(0);
            graphic.setFaceInterp(true);
            return graphic;
        } catch (InvalidRangeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onVariableChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                try {
                    data = dataset.read3DArray((String) this.jComboBoxVariable.getSelectedItem(), 0);
                    Graphic graphic = createGraphic();
                    if (graphic != null) {
                        this.layer.fileGraphicChangedEvent(graphic);
                    }
                } catch (InvalidRangeException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof JRadioButton) {
            if (((JRadioButton) e.getSource()).isSelected())
                this.jSliderValue.setValue(50);
        }
    }

    private void onSliderValueChanged(ChangeEvent e) {
        Graphic graphic = createGraphic();
        if (graphic != null) {
            this.layer.fileGraphicChangedEvent(graphic);
        }
    }
}
