package org.meteothink.weather.layer;

public class IsoSurfaceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public IsoSurfaceLayer() {
        this.configPanel = new IsoSurfacePanel();
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.ISO_SURFACE;
    }

    @Override
    public void updateGraphic() {

    }
}
