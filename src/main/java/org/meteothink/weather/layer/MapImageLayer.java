package org.meteothink.weather.layer;

import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.geo.layer.ImageLayer;
import org.meteoinfo.geo.mapdata.MapDataManage;

import java.io.File;

public class MapImageLayer extends PlotLayer {

    /**
     * Constructor
     */
    public MapImageLayer() {
        this.configPanel = new MapImagePanel();
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.MAP_IMAGE;
    }

    @Override
    public void updateGraphic() {
        if (this.graphic == null) {
            loadMapImage();
        }
    }

    protected void loadMapImage() {
        String startPath = System.getProperty("user.dir");
        String fileName = startPath + File.separator + "data/map/world_topo.jpg";
        try {
            ImageLayer layer = (ImageLayer) MapDataManage.loadLayer(fileName);
            this.graphic = JOGLUtil.createTexture(layer, 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
