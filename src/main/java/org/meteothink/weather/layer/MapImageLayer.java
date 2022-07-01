package org.meteothink.weather.layer;

import org.meteoinfo.chart.graphic.GraphicCollection3D;
import org.meteoinfo.chart.graphic.GraphicFactory;
import org.meteoinfo.chart.jogl.JOGLUtil;
import org.meteoinfo.chart.shape.TextureShape;
import org.meteoinfo.geo.layer.ImageLayer;
import org.meteoinfo.geo.mapdata.MapDataManage;

import java.io.File;

public class MapImageLayer extends PlotLayer {

    /**
     * Constructor
     */
    public MapImageLayer() {
        this.configPanel = new MapImagePanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.MAP_IMAGE;
    }

    @Override
    public void updateGraphic() {
        if (this.graphic == null) {
            this.graphic = this.configPanel.getGraphic();
        }
    }

    protected void loadMapImage() {
        String startPath = System.getProperty("user.dir");
        String fileName = startPath + File.separator + "data/map/world_topo.jpg";
        try {
            ImageLayer layer = (ImageLayer) MapDataManage.loadLayer(fileName);
            this.graphic = GraphicFactory.geoSurface(layer, 0, 0, 360, 180);
            ((GraphicCollection3D)this.graphic).setUsingLight(false);
            TextureShape shape = (TextureShape) ((GraphicCollection3D)this.graphic).get(0).getShape();
            shape.setXRepeat(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
