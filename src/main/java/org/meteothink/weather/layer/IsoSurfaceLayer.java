package org.meteothink.weather.layer;

public class IsoSurfaceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public IsoSurfaceLayer() {
        this.configPanel = new IsoSurfacePanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.ISO_SURFACE;
    }

    @Override
    public void updateGraphic() {
        if (this.graphic == null) {
            this.graphic = this.configPanel.getGraphic();
        }
    }
}
