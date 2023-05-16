package org.meteothink.weather.layer;

import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteothink.weather.event.GraphicChangedEvent;
import org.meteothink.weather.event.GraphicChangedListener;

import javax.swing.event.EventListenerList;

public abstract class PlotLayer {
    private final EventListenerList listeners = new EventListenerList();
    protected boolean selected = false;
    protected Graphic graphic;
    protected LayerPanel configPanel;

    /**
     * Constructor
     */
    public PlotLayer() {

    }

    /**
     * Factory method
     *
     * @param layerType Layer type
     * @param colorMaps Color maps
     * @return PlotLayer object
     */
    public static PlotLayer factory(LayerType layerType, ColorMap[] colorMaps) {
        switch (layerType) {
            case MAP_IMAGE:
                return new MapImageLayer();
            case MAP_VECTOR:
                return new MapVectorLayer();
            case SLICE:
                return new DataSliceLayer(colorMaps);
            case ISO_SURFACE:
                return new IsoSurfaceLayer(colorMaps);
            case VOLUME:
                return new VolumeLayer(colorMaps);
            case STREAMLINE:
                return new StreamlineLayer(colorMaps);
            case WIND_VECTOR:
                return new WindVectorLayer();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void addGraphicChangedListener(GraphicChangedListener listener) {
        this.listeners.add(GraphicChangedListener.class, listener);
    }

    public void removeGraphicChangedListener(GraphicChangedListener listener) {
        this.listeners.remove(GraphicChangedListener.class, listener);
    }

    public void fileGraphicChangedEvent(Graphic graphic) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == GraphicChangedListener.class) {
                ((GraphicChangedListener) listeners[i + 1]).graphicChangedEvent(new GraphicChangedEvent(this, graphic));
            }
        }
    }

    /**
     * Get whether selected
     * @return Whether selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Set whether selected
     * @param value Whether selected
     */
    public void setSelected(boolean value) {
        this.selected = value;
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
