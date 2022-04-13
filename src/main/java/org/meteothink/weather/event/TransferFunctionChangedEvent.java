package org.meteothink.weather.event;

import java.util.EventObject;

public class TransferFunctionChangedEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public TransferFunctionChangedEvent(Object source) {
        super(source);
    }
}
