package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.chart.graphic.GraphicProjectionUtil;
import org.meteoinfo.chart.graphic.MeshGraphic;
import org.meteoinfo.geo.layer.ImageLayer;
import org.meteoinfo.geo.mapdata.MapDataManage;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteoinfo.projection.ProjectionInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapImagePanel extends LayerPanel {

    JLabel jLabelImage;
    JComboBox jComboBoxImage;
    String mapPath;

    MeshGraphic graphic;

    /**
     * Constructor
     */
    public MapImagePanel(PlotLayer layer) {
        super(layer);
        Border border = BorderFactory.createTitledBorder("地图图像设置");
        this.setBorder(border);

        initComponents();
    }

    private void initComponents() {
        jLabelImage = new JLabel("图像文件:");
        jComboBoxImage = new JComboBox();

        mapPath = System.getProperty("user.dir");
        mapPath = mapPath + File.separator + "data/map";
        List<File> imageFiles = new ArrayList<>();
        for (File file : new File(mapPath).listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jpg")) {
                imageFiles.add(file);
                jComboBoxImage.addItem(file.getName());
            }
        }
        jComboBoxImage.setSelectedItem("world_topo.jpg");
        jComboBoxImage.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onImageFileChanged(e);
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
                                .addComponent(jLabelImage)
                                .addComponent(jComboBoxImage))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelImage)
                                .addComponent(jComboBoxImage))
        );
    }

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        String fileName = this.mapPath + File.separator + jComboBoxImage.getSelectedItem().toString();
        try {
            ImageLayer layer = (ImageLayer) MapDataManage.loadLayer(fileName);
            ProjectionInfo projInfo = ProjectionInfo.LONG_LAT;
            if (this.dataset != null) {
                projInfo = this.dataset.getProjInfo();
            }
            this.graphic = GraphicFactory.geoSurface(layer, 0, 0, 360, 180, projInfo);
            if (!projInfo.isLonLat()) {
                graphic = (MeshGraphic) GraphicProjectionUtil.projectClipGraphic(graphic, ProjectionInfo.LONG_LAT, projInfo);
            }
            this.graphic.setUsingLight(false);
            return graphic;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onImageFileChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            graphic = (MeshGraphic) this.getGraphic();
            if (graphic != null) {
                this.layer.fileGraphicChangedEvent(graphic);
            }
        }
    }
}
