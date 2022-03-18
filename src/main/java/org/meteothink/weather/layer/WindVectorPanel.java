package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;

import javax.swing.*;
import javax.swing.border.Border;

public class WindVectorPanel extends LayerPanel {

    /**
     * Constructor
     */
    public WindVectorPanel() {
        Border border = BorderFactory.createTitledBorder("流场矢量设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public WindVectorPanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("流场矢量设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        this.meteoDataInfo = dataInfo;
        this.meteoDataInfo.getDataInfo().getVariables();
    }
}
