package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geometry.graphic.Graphic;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LayerPanel extends JPanel {

    protected PlotLayer layer;
    protected MeteoDataInfo meteoDataInfo;

    /**
     * Constructor
     *
     * @param layer The plot layer
     */
    public LayerPanel(PlotLayer layer) {
        this.layer = layer;
    }

    /**
     * Constructor
     *
     * @param layer The plot layer
     * @param meteoDataInfo Meteo data info
     */
    public LayerPanel(PlotLayer layer, MeteoDataInfo meteoDataInfo) {
        this(layer);
        this.meteoDataInfo = meteoDataInfo;
    }

    /**
     * Set meteo data info
     * @param dataInfo Meteo data info
     */
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        if (this.meteoDataInfo != null) {
            if (this.meteoDataInfo.getFileName() == dataInfo.getFileName()) {
                return;
            }
        }
        this.meteoDataInfo = dataInfo;
    };

    /**
     * Get 3D variables
     *
     * @return
     */
    public List<Variable> get3DVariables() {
        List<Variable> variables = new ArrayList<>();
        for (Variable variable : this.meteoDataInfo.getDataInfo().getVariables()) {
            if (variable.getXDimension() != null && variable.getYDimension() != null &&
                variable.getZDimension() != null) {
                variables.add(variable);
            }
        }

        return variables;
    }

    /**
     * Get graphic
     * @return Graphic
     */
    public abstract Graphic getGraphic();
}
