package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.*;
import javax.swing.border.Border;

public class WindVectorPanel extends LayerPanel {

    /**
     * Constructor
     */
    public WindVectorPanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("流场矢量设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public WindVectorPanel(PlotLayer layer, MeteoDataInfo meteoDataInfo) {
        super(layer, meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("流场矢量设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        this.meteoDataInfo = dataInfo;
        this.meteoDataInfo.getDataInfo().getVariables();
    }

    /**
     * Get graphic
     * @return The graphic
     */
    @Override
    public Graphic getGraphic() {
        return null;
    }
}
