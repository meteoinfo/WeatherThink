package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;

public class IsoSurfaceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public IsoSurfaceLayer(ColorMap[] colorMaps) {
        this.configPanel = new IsoSurfacePanel(this, colorMaps);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.ISO_SURFACE;
    }

    @Override
    public void updateGraphic() {
        if (this.graphic == null) {
            this.graphic = this.configPanel.getGraphic();
        }
    }
}
