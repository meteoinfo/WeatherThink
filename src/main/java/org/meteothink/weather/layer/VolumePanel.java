package org.meteothink.weather.layer;

import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geo.legend.FrmLegendBreaks;
import org.meteoinfo.geometry.colors.Normalize;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.geometry.graphic.GraphicCollection;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.ui.ColorComboBoxModel;
import org.meteoinfo.ui.ColorListCellRender;
import org.meteothink.weather.data.Dataset;
import org.meteothink.weather.util.DataUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolumePanel extends LayerPanel {

    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;
    JLabel jLabelColorMap;
    JComboBox jComboBoxColorMap;

    Array data;

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
        jLabelColorMap = new JLabel("颜色方案:");
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
        jComboBoxColorMap.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onColormapChanged(e);
            }
        });

        //Set layout
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelVariable)
                                .addComponent(jLabelColorMap))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jComboBoxVariable)
                                .addComponent(jComboBoxColorMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelVariable)
                                .addComponent(jComboBoxVariable))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelColorMap)
                                .addComponent(jComboBoxColorMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
        return createGraphic();
    }

    private Graphic createGraphic() {
        if (this.dataset == null) {
            return null;
        }

        if (data == null) {
            try {
                String varName = (String) this.jComboBoxVariable.getSelectedItem();
                data = dataset.read3DArray(varName, 0);
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
        GraphicCollection graphic = JOGLUtil.volume(data, dataset.getXArray(), dataset.getYArray(),
                dataset.getZArray(), colorMap, normalize, null, opacityLevels);
        return graphic;
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

    private void onColormapChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (this.dataset != null) {
                try {
                    if (data == null) {
                        data = dataset.read3DArray((String) this.jComboBoxVariable.getSelectedItem(), 0);
                    }
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
}
