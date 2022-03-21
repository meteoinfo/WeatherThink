package org.meteothink.weather.event;

import org.meteoinfo.geometry.graphic.Graphic;

import java.util.EventObject;

public class GraphicChangedEvent extends EventObject {
    private Graphic graphic;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @param graphic the graphic object
     * @throws IllegalArgumentException if source is null
     */
    public GraphicChangedEvent(Object source, Graphic graphic) {
        super(source);
        this.graphic = graphic;
    }

    public Graphic getGraphic() {
        return this.graphic;
    }
}
