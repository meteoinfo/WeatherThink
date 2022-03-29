package org.meteothink.weather.layer;

public class VolumeLayer extends PlotLayer {

    /**
     * Constructor
     */
    public VolumeLayer() {
        this.configPanel = new VolumePanel(this);
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
