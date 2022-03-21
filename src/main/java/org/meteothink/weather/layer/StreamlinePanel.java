package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.*;
import javax.swing.border.Border;

public class StreamlinePanel extends LayerPanel {

    /**
     * Constructor
     */
    public StreamlinePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("流线设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public StreamlinePanel(PlotLayer layer, MeteoDataInfo meteoDataInfo) {
        super(layer, meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("流线设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {

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
