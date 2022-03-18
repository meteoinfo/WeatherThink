package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;

import javax.swing.*;
import javax.swing.border.Border;

public class MapImagePanel extends LayerPanel {

    /**
     * Constructor
     */
    public MapImagePanel() {
        Border border = BorderFactory.createTitledBorder("地图图像设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public MapImagePanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("地图图像设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {

    }
}
