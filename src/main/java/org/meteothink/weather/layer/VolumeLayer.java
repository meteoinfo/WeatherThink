package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;

public class VolumeLayer extends PlotLayer {

    /**
     * Constructor
     *
     * @param colorMaps The color maps
     */
    public VolumeLayer(ColorMap[] colorMaps) {
        this.configPanel = new VolumePanel(this, colorMaps);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.VOLUME;
    }

    @Override
    public void updateGraphic() {
        if (this.graphic == null) {
            this.graphic = this.configPanel.getGraphic();
        }
    }
}
