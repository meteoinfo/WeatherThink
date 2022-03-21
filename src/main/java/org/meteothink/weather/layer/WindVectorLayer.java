package org.meteothink.weather.layer;

public class WindVectorLayer extends PlotLayer {

    /**
     * Constructor
     */
    public WindVectorLayer() {
        this.configPanel = new WindVectorPanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.WIND_VECTOR;
    }

    @Override
    public void updateGraphic() {

    }
}
