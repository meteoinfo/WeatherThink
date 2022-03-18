package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;

import javax.swing.*;
import javax.swing.border.Border;

public class VolumePanel extends LayerPanel {

    /**
     * Constructor
     */
    public VolumePanel() {
        Border border = BorderFactory.createTitledBorder("体绘制设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public VolumePanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("体绘制设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {

    }
}
