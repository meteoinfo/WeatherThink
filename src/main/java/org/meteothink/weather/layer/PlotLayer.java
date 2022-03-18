package org.meteothink.weather.layer;

import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.JPanel;

public abstract class PlotLayer {
    protected Graphic graphic;
    protected LayerPanel configPanel;

    /**
     * Constructor
     */
    public PlotLayer() {

    }

    /**
     * Factory method
     * @param layerType Layer type
     * @return PlotLayer object
     */
    public static PlotLayer factory(LayerType layerType) {
        switch (layerType) {
            case MAP_IMAGE:
                return new MapImageLayer();
            case MAP_VECTOR:
                return new MapVectorLayer();
            case SLICE:
                return new SliceLayer();
            case ISO_SURFACE:
                return new IsoSurfaceLayer();
            case VOLUME:
                return new VolumeLayer();
            case STREAMLINE:
                return new StreamlineLayer();
            case WIND_VECTOR:
                return new WindVectorLayer();
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Get layer type
     * @return Layer type
     */
    public abstract LayerType getLayerType();

    /**
     * Get graphic
     * @return The graphic
     */
    public Graphic getGraphic() {
        return graphic;
    }

    /**
     * Set graphic
     * @param graphic The graphic
     */
    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    /**
     * Set config panel
     * @return The config panel
     */
    public LayerPanel getConfigPanel() {
        return configPanel;
    }

    /**
     * Set config panel
     * @param configPanel The config panel
     */
    public void setConfigPanel(LayerPanel configPanel) {
        this.configPanel = configPanel;
    }

    /**
     * Update graphic
     */
    public abstract void updateGraphic();

    /**
     * To string
     * @return String
     */
    public String toString() {
        return getLayerType().toString();
    }
}
