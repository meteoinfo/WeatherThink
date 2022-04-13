package org.meteothink.weather.data;

import org.meteoinfo.common.Extent3D;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.math.meteo.MeteoMath;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.Dimension;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.Range;
import org.meteoinfo.ndarray.math.ArrayMath;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private MeteoDataInfo dataInfo;
    private Array xArray;
    private Array yArray;
    private Array zArray;

    /**
     * Constructor
     * @param dataInfo Meteo data info
     */
    public Dataset(MeteoDataInfo dataInfo) {
        this.dataInfo = dataInfo;
        Dimension dimX = dataInfo.getDataInfo().getXDimension();
        Dimension dimY = dataInfo.getDataInfo().getYDimension();
        Dimension dimZ = dataInfo.getDataInfo().getZDimension();
        xArray = dimX.getDimArray();
        yArray = dimY.getDimArray();
        zArray = dimZ.getDimArray();
        switch (dimZ.getUnit()) {
            case "hpa":
                zArray = MeteoMath.press2Height(zArray);
                break;
            case "pa":
                zArray = MeteoMath.press2Height(ArrayMath.div(zArray, 1000));
                break;
        }
    }

    /**
     * Get meteo data info
     * @return Meteo data info
     */
    public MeteoDataInfo getDataInfo() {
        return this.dataInfo;
    }

    /**
     * Get file name
     * @return File name
     */
    public String getFileName() {
        return this.dataInfo.getFileName();
    }

    /**
     * Get variables
     * @return Variables
     */
    public List<Variable> getVariables() {
        return this.dataInfo.getDataInfo().getVariables();
    }

    /**
     * Get x coordinate array
     * @return X coordinate array
     */
    public Array getXArray() {
        return this.xArray;
    }

    /**
     * Get y coordinate array
     * @return Y coordinate array
     */
    public Array getYArray() {
        return this.yArray;
    }

    /**
     * Get z coordinate array
     * @return Z coordinate array
     */
    public Array getZArray() {
        return this.zArray;
    }

    /**
     * Get minimum x coordinate value
     * @return Minimum x coordinate value
     */
    public double getXMin() {
        return this.xArray.getDouble(0);
    }

    /**
     * Get maximum x coordinate value
     * @return Maximum x coordinate value
     */
    public double getXMax() {
        return this.xArray.getDouble((int) this.xArray.getSize() - 1);
    }

    /**
     * Get minimum y coordinate value
     * @return Minimum y coordinate value
     */
    public double getYMin() {
        return this.yArray.getDouble(0);
    }

    /**
     * Get maximum y coordinate value
     * @return Maximum y coordinate value
     */
    public double getYMax() {
        return this.yArray.getDouble((int) this.yArray.getSize() - 1);
    }

    /**
     * Get minimum z coordinate value
     * @return Minimum z coordinate value
     */
    public double getZMin() {
        return this.zArray.getDouble(0);
    }

    /**
     * Get maximum z coordinate value
     * @return Maximum z coordinate value
     */
    public double getZMax() {
        return this.zArray.getDouble((int) this.zArray.getSize() - 1);
    }

    /**
     * Get extent 3d
     * @return Get extent 3d
     */
    public Extent3D getExtent3D() {
        Extent3D extent3D = new Extent3D(getXMin(), getXMax(), getYMin(), getYMax(),
                getZMin(), getZMax());
        return extent3D;
    }

    /**
     * Read 3D array from meteo data info
     *
     * @param varName Variable name
     * @param timeIndex Time index
     * @return 3D array
     * @throws InvalidRangeException
     */
    public Array read3DArray(String varName, int timeIndex) throws InvalidRangeException {
        Variable variable = dataInfo.getDataInfo().getVariable(varName);
        List<Range> ranges = new ArrayList<>();
        List<Dimension> dimensions = variable.getDimensions();
        int n = dimensions.size();
        if (n == 4) {
            ranges.add(new Range(timeIndex, timeIndex));
        }
        for (int i = n - 3; i < n; i++) {
            ranges.add(new Range(0, dimensions.get(i).getLength() - 1));
        }
        Array r = dataInfo.read(varName, ranges);
        r = r.reduce();
        ArrayMath.missingToNaN(r, variable.getFillValue());
        return r;
    }
}
