package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteothink.weather.data.Dataset;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LayerPanel extends JPanel {

    protected PlotLayer layer;
    protected Dataset dataset;

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
     * @param dataset Dataset
     */
    public LayerPanel(PlotLayer layer, Dataset dataset) {
        this(layer);
        this.dataset = dataset;
    }

    /**
     * Set dataset
     * @param dataset Dataset
     */
    public void setDataset(Dataset dataset) {
        if (this.dataset != null) {
            if (this.dataset.getFileName() == dataset.getFileName()) {
                return;
            }
        }
        this.dataset = dataset;
    };

    /**
     * Get 3D variables
     *
     * @return
     */
    public List<Variable> get3DVariables() {
        List<Variable> variables = new ArrayList<>();
        for (Variable variable : this.dataset.getVariables()) {
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
