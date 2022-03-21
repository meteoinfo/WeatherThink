package org.meteothink.weather.form;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import org.meteoinfo.chart.jogl.Plot3DGL;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.ui.CheckBoxListEntry;
import org.meteoinfo.ui.JCheckBoxList;
import org.meteothink.weather.event.GraphicChangedEvent;
import org.meteothink.weather.event.GraphicChangedListener;
import org.meteothink.weather.layer.LayerPanel;
import org.meteothink.weather.layer.LayerType;
import org.meteothink.weather.layer.PlotLayer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigDockable extends DefaultSingleCDockable {

    private JPanel jPanelLayers;
    private JScrollPane jScrollPaneLayers;
    private JCheckBoxList jCheckBoxListLayers;
    private LayerPanel configPanel;

    private FrmMain parent;
    private Plot3DGL plot3DGL;
    private String startPath;

    private List<PlotLayer> layers = new ArrayList<>();

    private MeteoDataInfo meteoDataInfo;

    public ConfigDockable(String s, CAction... cActions) {
        super(s, cActions);

        initComponents();
    }

    public ConfigDockable(final FrmMain parent, String s, String startPath, CAction... cActions) {
        super(s, cActions);

        this.parent = parent;
        this.plot3DGL = parent.getFigureDockable().getPlot();
        this.startPath = startPath;

        initComponents();
    }

    private void initComponents() {
        jPanelLayers = new JPanel();
        jScrollPaneLayers = new JScrollPane();
        jCheckBoxListLayers = new JCheckBoxList();

        DefaultListModel listModel = new DefaultListModel();
        for (LayerType layerType : LayerType.values()) {
            PlotLayer layer = PlotLayer.factory(layerType);
            layer.addGraphicChangedListener(new GraphicChangedListener() {
                @Override
                public void graphicChangedEvent(GraphicChangedEvent e) {
                    onLayerGraphicChanged(e);
                }
            });
            this.layers.add(layer);
            CheckBoxListEntry checkBoxListEntry = new CheckBoxListEntry(layer, false);
            checkBoxListEntry.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    onLayerCheckChanged(e);
                }
            });
            listModel.add(0, checkBoxListEntry);
        }
        jCheckBoxListLayers.setModel(listModel);
        jCheckBoxListLayers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                onLayerSelected(e);
            }
        });
        Border border = BorderFactory.createTitledBorder("图层");
        jScrollPaneLayers.setBorder(border);
        jScrollPaneLayers.setViewportView(jCheckBoxListLayers);
        this.getContentPane().add(jScrollPaneLayers, BorderLayout.NORTH);
    }

    private void onLayerSelected(ListSelectionEvent e) {
        CheckBoxListEntry cb = (CheckBoxListEntry) this.jCheckBoxListLayers.getSelectedValue();
        PlotLayer layer = (PlotLayer) cb.getValue();
        if (configPanel != null) {
            this.getContentPane().remove(this.configPanel);
        }
        this.configPanel = layer.getConfigPanel();
        if (this.meteoDataInfo != null) {
            this.configPanel.setMeteoDataInfo(this.meteoDataInfo);
        }
        this.getContentPane().add(this.configPanel, BorderLayout.CENTER);
        this.configPanel.updateUI();
    }

    private void onLayerCheckChanged(ChangeEvent e) {
        CheckBoxListEntry cb = (CheckBoxListEntry) e.getSource();
        PlotLayer layer = (PlotLayer) cb.getValue();
        layer.setSelected(cb.isSelected());
        layer.updateGraphic();
        Graphic graphic = layer.getGraphic();
        updateLayerGraphic(layer, graphic);
    }

    private void onLayerGraphicChanged(GraphicChangedEvent e) {
        PlotLayer layer = (PlotLayer) e.getSource();
        Graphic graphic = e.getGraphic();
        updateLayerGraphic(layer, graphic);
    }

    /**
     * Update layer and graphic
     * @param layer The layer
     * @param graphic The graphic
     */
    public void updateLayerGraphic(PlotLayer layer, Graphic graphic) {
        this.parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        Graphic oldGraphic = layer.getGraphic();
        if (oldGraphic != null) {
            if (this.plot3DGL.getGraphics().contains(oldGraphic))
                this.plot3DGL.getGraphics().remove(oldGraphic);
        }
        layer.setGraphic(graphic);
        if (graphic != null && layer.isSelected()) {
            this.plot3DGL.addGraphic(graphic);
            if (this.plot3DGL.getZMin() == this.plot3DGL.getZMax()) {
                this.plot3DGL.setZMax(this.plot3DGL.getZMin() + 10);
            }
        }
        this.parent.getFigureDockable().rePaint();
        this.parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Set meteo data info
     * @param meteoDataInfo Meteo data info
     */
    public void setMeteoDataInfo(MeteoDataInfo meteoDataInfo) {
        this.meteoDataInfo = meteoDataInfo;
        this.setTitleText(new File(meteoDataInfo.getFileName()).getName());
        this.configPanel.setMeteoDataInfo(meteoDataInfo);
        this.configPanel.updateUI();
    }
}
