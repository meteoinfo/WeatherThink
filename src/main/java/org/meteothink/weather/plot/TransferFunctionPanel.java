package org.meteothink.weather.plot;

import org.meteoinfo.common.colors.ColorMap;
import org.meteoinfo.ndarray.Array;

import javax.swing.JPanel;

public class TransferFunctionPanel extends JPanel {
    private ColorMap colorMap;
    private Array data;

    /**
     * Constructor
     * @param data The data array
     * @param colorMap The color map
     */
    public TransferFunctionPanel(Array data, ColorMap colorMap) {
        super();

        this.data = data;
        this.colorMap = colorMap;
    }
}
