package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;

import javax.swing.*;
import javax.swing.border.Border;

public class SlicePanel extends LayerPanel {

    /**
     * Constructor
     */
    public SlicePanel() {
        Border border = BorderFactory.createTitledBorder("数据切片设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public SlicePanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("数据切片设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {

    }
}
