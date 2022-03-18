package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;

public class IsoSurfacePanel extends LayerPanel {

    JLabel jLabelVariable;
    JComboBox jComboBoxVariable;

    /**
     * Constructor
     */
    public IsoSurfacePanel() {
        super();

        Border border = BorderFactory.createTitledBorder("等值面设置");
        this.setBorder(border);

        initComponents();
    }

    /**
     * Constructor
     */
    public IsoSurfacePanel(MeteoDataInfo meteoDataInfo) {
        super(meteoDataInfo);

        Border border = BorderFactory.createTitledBorder("等值面设置");
        this.setBorder(border);

        initComponents();
    }

    private void initComponents() {
        this.jLabelVariable = new JLabel("变量:");
        this.jComboBoxVariable = new JComboBox();

        this.add(jLabelVariable);
        this.add(jComboBoxVariable);
    }

    @Override
    public void setMeteoDataInfo(MeteoDataInfo dataInfo) {
        super.setMeteoDataInfo(dataInfo);

        List<Variable> variables3D = get3DVariables();
        this.jComboBoxVariable.removeAllItems();
        for (Variable variable : variables3D) {
            this.jComboBoxVariable.addItem(variable);
        }
        if (this.jComboBoxVariable.getItemCount() > 0) {
            this.jComboBoxVariable.setSelectedIndex(0);
        }
    }
}
