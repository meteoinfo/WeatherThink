package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;

public class DataSliceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public DataSliceLayer(ColorMap[] colorMaps) {
        this.configPanel = new DataSlicePanel(this, colorMaps);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.SLICE;
    }

    @Override
    public void updateGraphic() {

    }
}
