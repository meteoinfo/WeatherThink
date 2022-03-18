package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;

import javax.swing.*;
import javax.swing.border.Border;

public class StreamlinePanel extends LayerPanel {

    /**
     * Constructor
     */
    public StreamlinePanel() {
        Border border = BorderFactory.createTitledBorder("流线设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public StreamlinePanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("流线设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {

    }
}
