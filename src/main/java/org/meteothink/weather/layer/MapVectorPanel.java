package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;

public class MapVectorPanel extends LayerPanel {

    /**
     * Constructor
     */
    public MapVectorPanel(PlotLayer layer) {
        super(layer);

        Border border = BorderFactory.createTitledBorder("地图矢量设置");
        this.setBorder(border);
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
