package org.meteothink.weather.event;

import java.util.EventListener;

public interface TransferFunctionChangedListener extends EventListener {
    public void transferFunctionChangedEvent(TransferFunctionChangedEvent e);
}
