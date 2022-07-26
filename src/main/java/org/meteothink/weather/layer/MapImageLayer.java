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
}
