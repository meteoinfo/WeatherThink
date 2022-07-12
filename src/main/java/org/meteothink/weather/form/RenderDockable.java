package org.meteothink.weather.form;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.meteoinfo.chart.jogl.Plot3DGL;
import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.common.colors.ColorUtil;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.ui.CheckBoxListEntry;
import org.meteoinfo.ui.JCheckBoxList;
import org.meteothink.weather.data.Dataset;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RenderDockable extends DefaultSingleCDockable {

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/RenderDockable");
    private JScrollPane jScrollPaneLayers;
    private JCheckBoxList jCheckBoxListLayers;
    private LayerPanel layerPanel;
    private JScrollPane jScrollPaneConfig;

    private FrmMain parent;
    private Plot3DGL plot3DGL;
    private String startPath;
    private ColorMap[] colorMaps;

    private List<PlotLayer> layers = new ArrayList<>();

    private Dataset dataset;

    public RenderDockable(String s, CAction... cActions) {
        super(s, cActions);

        initComponents();
    }

    public RenderDockable(final FrmMain parent, String s, String startPath, CAction... cActions) {
        super(s, cActions);

        this.parent = parent;
        this.plot3DGL = parent.getFigureDockable().getPlot();
        this.startPath = startPath;

        this.setTitleIcon(new FlatSVGIcon("icons/setting.svg"));

        String path = System.getProperty("user.dir");
        path = path + File.separator + "data" + File.separator + "colormaps";
        try {
            colorMaps = ColorUtil.getColorMaps(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        jScrollPaneLayers = new JScrollPane();
        jCheckBoxListLayers = new JCheckBoxList();

        DefaultListModel listModel = new DefaultListModel();
        LayerType[] layerTypes = new LayerType[]{LayerType.MAP_IMAGE, LayerType.SLICE, LayerType.STREAMLINE,
                LayerType.ISO_SURFACE, LayerType.VOLUME};
        for (LayerType layerType : layerTypes) {
            PlotLayer layer = PlotLayer.factory(layerType, colorMaps);
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
        Border border = BorderFactory.createTitledBorder(bundle.getString("RenderDockable.layerBorder.title"));
        jScrollPaneLayers.setBorder(border);
        jScrollPaneLayers.setViewportView(jCheckBoxListLayers);
        this.getContentPane().add(jScrollPaneLayers, BorderLayout.NORTH);

        this.jScrollPaneConfig = new JScrollPane();
    }

    private void onLayerSelected(ListSelectionEvent e) {
        CheckBoxListEntry cb = (CheckBoxListEntry) this.jCheckBoxListLayers.getSelectedValue();
        PlotLayer layer = (PlotLayer) cb.getValue();
        if (layerPanel != null) {
            this.getContentPane().remove(this.layerPanel);
        }
        this.layerPanel = layer.getConfigPanel();
        this.layerPanel.setColorMaps(this.colorMaps);
        this.jScrollPaneConfig.setViewportView(this.layerPanel);
        this.getContentPane().add(jScrollPaneConfig, BorderLayout.CENTER);
        if (this.dataset != null) {
            this.layerPanel.setDataset(this.dataset);
        }
        this.jScrollPaneConfig.updateUI();
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
            this.plot3DGL.removeGraphic(oldGraphic);
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
     * Set dataset
     * @param dataset Dataset
     */
    public void setDataset(Dataset dataset) {
        boolean projChanged = false;
        if (this.dataset == null) {
            projChanged = true;
        } else {
            if (!this.dataset.getProjInfo().equals(dataset.getProjInfo())) {
                projChanged = true;
            }
        }

        this.dataset = dataset;
        this.setTitleText(new File(dataset.getDataInfo().getFileName()).getName());
        if (this.layerPanel != null) {
            this.layerPanel.setDataset(dataset);
            this.layerPanel.updateUI();
        }

        if (projChanged) {
            for (PlotLayer layer : this.layers) {
                if (layer.getLayerType() == LayerType.MAP_IMAGE) {
                    layer.setGraphic(null);
                }
            }
        }
    }
}
