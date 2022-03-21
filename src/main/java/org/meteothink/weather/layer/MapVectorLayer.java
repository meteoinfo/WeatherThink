package org.meteothink.weather.layer;

public class MapVectorLayer extends PlotLayer {

    /**
     * Constructor
     */
    public MapVectorLayer() {
        this.configPanel = new MapVectorPanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.MAP_VECTOR;
    }

    @Override
    public void updateGraphic() {

    }
}
