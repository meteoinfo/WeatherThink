package org.meteothink.weather.layer;

import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.geometry.graphic.Graphic;
import org.meteothink.weather.data.Dataset;

import javax.swing.*;
import javax.swing.border.Border;

public class MapImagePanel extends LayerPanel {

    JLabel jLabelImage;
    JComboBox jComboBoxImage;

    /**
     * Constructor
     */
    public MapImagePanel(PlotLayer layer) {
        super(layer);
        Border border = BorderFactory.createTitledBorder("地图图像设置");
        this.setBorder(border);
    }

    private void initComponents() {
        jLabelImage = new JLabel("图像文件:");
        jComboBoxImage = new JComboBox();
        
    }

    @Override
    public void setDataset(Dataset dataset) {

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
