package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LayerPanel extends JPanel {

    protected MeteoDataInfo meteoDataInfo;

    /**
     * Constructor
     */
    public LayerPanel() {

    }

    /**
     * Constructor
     * @param meteoDataInfo Meteo data info
     */
    public LayerPanel(MeteoDataInfo meteoDataInfo) {
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

}
