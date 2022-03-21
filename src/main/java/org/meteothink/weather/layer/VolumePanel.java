package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.*;
import javax.swing.border.Border;

public class VolumePanel extends LayerPanel {

    /**
     * Constructor
     */
    public VolumePanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("体绘制设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public VolumePanel(PlotLayer layer, MeteoDataInfo meteoDataInfo) {
        super(layer, meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("体绘制设置");
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
