package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;

public class StreamlineLayer extends PlotLayer {

    /**
     * Constructor
     */
    public StreamlineLayer(ColorMap[] colorMaps) {
        this.configPanel = new StreamlinePanel(this, colorMaps);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.STREAMLINE;
    }

    @Override
    public void updateGraphic() {

    }
}
