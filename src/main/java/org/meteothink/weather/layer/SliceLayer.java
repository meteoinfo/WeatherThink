package org.meteothink.weather.layer;

public class SliceLayer extends PlotLayer {

    /**
     * Constructor
     */
    public SliceLayer() {
        this.configPanel = new SlicePanel(this);
    }

    @Override
    public LayerType getLayerType() {
        return LayerType.SLICE;
    }

    @Override
    public void updateGraphic() {

    }
}
