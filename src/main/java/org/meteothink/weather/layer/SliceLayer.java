package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;

public class SliceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public SliceLayer(ColorMap[] colorMaps) {
        this.configPanel = new SlicePanel(this, colorMaps);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.SLICE;
    }

    @Override
    public void updateGraphic() {

    }
}
