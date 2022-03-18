package org.meteothink.weather.layer;

public class VolumeLayer extends PlotLayer {

    /**
     * Constructor
     */
    public VolumeLayer() {
        this.configPanel = new VolumePanel();
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.VOLUME;
    }

    @Override
    public void updateGraphic() {

    }
}
