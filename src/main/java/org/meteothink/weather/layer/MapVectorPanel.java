package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;

public class MapVectorPanel extends LayerPanel {

    /**
     * Constructor
     */
    public MapVectorPanel() {
        Border border = BorderFactory.createTitledBorder("地图矢量设置");
        this.setBorder(border);
    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public MapVectorPanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("地图矢量设置");
        this.setBorder(border);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        super.setMeteoDataInfo(dataInfo);

        List<Variable> variables3D = get3DVariables();

    }
}
