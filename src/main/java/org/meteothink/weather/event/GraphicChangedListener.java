package org.meteothink.weather.event;

import java.util.EventListener;

public interface GraphicChangedListener extends EventListener {
    public void graphicChangedEvent(GraphicChangedEvent e);
}
