package org.meteothink.weather.layer;

public class StreamlineLayer extends PlotLayer {

    /**
     * Constructor
     */
    public StreamlineLayer() {
        this.configPanel = new StreamlinePanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.STREAMLINE;
    }

    @Override
    public void updateGraphic() {

    }
}
