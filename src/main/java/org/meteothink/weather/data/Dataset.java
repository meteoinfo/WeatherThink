package org.meteothink.weather.data;

import org.meteoinfo.common.Extent3D;
import org.meteoinfo.data.dimarray.DimArray;
import org.meteoinfo.data.dimarray.DimensionType;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteoinfo.data.meteodata.MeteoDataType;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.data.meteodata.netcdf.Conventions;
import org.meteoinfo.data.meteodata.netcdf.NetCDFDataInfo;
import org.meteoinfo.data.meteodata.util.WRFUtil;
import org.meteoinfo.math.meteo.MeteoMath;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.data.dimarray.Dimension;
import org.meteoinfo.ndarray.InvalidRangeException;
import org.meteoinfo.ndarray.Range;
import org.meteoinfo.ndarray.math.ArrayMath;
import org.meteoinfo.projection.ProjectionInfo;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private MeteoDataInfo dataInfo;
    private Array xArray;
    private Array yArray;
    private Array zArray;
    private int timeIndex = 0;
    private boolean zReverse = false;

    /**
     * Constructor
     * @param dataInfo Meteo data info
     */
    public Dataset(MeteoDataInfo dataInfo) {
        this.dataInfo = dataInfo;
        Dimension dimX = dataInfo.getDataInfo().getXDimension();
        Dimension dimY = dataInfo.getDataInfo().getYDimension();
        Dimension dimZ = null;
        for (Dimension dim : dataInfo.getDataInfo().getDimensions()) {
            if (dim.getDimType() == DimensionType.Z) {
                if (dimZ == null)
                    dimZ = dim;
                else {
                    if (dim.getLength() > dimZ.getLength()) {
                        dimZ = dim;
                    }
                }
            }
        }
        xArray = dimX.getDimValue();
        yArray = dimY.getDimValue();
        if (dimY.isDescending()) {
            yArray = yArray.flip(0).copy();
        }
        zArray = dimZ.getDimValue();
        switch (dimZ.getUnit()) {
            case "hpa":
                zArray = MeteoMath.pressure2Height(zArray);
                break;
            case "pa":
                zArray = MeteoMath.pressure2Height(ArrayMath.div(zArray, 100));
                break;
            case "eta":
                if (isWRF()) {
                    zArray = WRFUtil.getGPM1D(this.dataInfo.getDataInfo()).getArray();
                }
                break;
        }
        if (zArray.getSize() > 1) {
            if (zArray.getDouble(1) - zArray.getDouble(0) < 0) {
                zArray = zArray.flip(0).copy();
            }
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
     * Get projection
     * @return Projection
     */
    public ProjectionInfo getProjInfo() {
        return this.dataInfo.getProjectionInfo();
    }

    /**
     * Get whether the dataset is from WRF model
     * @return Boolean
     */
    public boolean isWRF() {
        if (this.dataInfo.getDataType() == MeteoDataType.NETCDF) {
            if (((NetCDFDataInfo)this.dataInfo.getDataInfo()).getConvention() == Conventions.WRFOUT) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get time index
     * @return Time index
     */
    public int getTimeIndex() {
        return this.timeIndex;
    }

    /**
     * Set time index
     * @param value Time index
     */
    public void setTimeIndex(int value) {
        this.timeIndex = value;
    }

    /**
     * Read 3D array from meteo data info
     *
     * @param varName Variable name
     * @return 3D array
     */
    public DimArray read3DArray(String varName) {
        try {
            Variable variable = dataInfo.getDataInfo().getVariable(varName);
            List<Range> ranges = new ArrayList<>();
            List<Dimension> dimensions = variable.getDimensions();
            int n = dimensions.size();
            if (n == 4) {
                ranges.add(new Range(timeIndex, timeIndex));
            }
            for (int i = n - 3; i < n; i++) {
                ranges.add(new Range(dimensions.get(i).getLength()));
            }
            DimArray array = dataInfo.getDataInfo().readDimArray(varName, ranges);
            if (isWRF())
                array = WRFUtil.deStagger(array);

            Dimension zDim = array.getZDimension();
            if (zDim != null) {
                switch (zDim.getUnit()) {
                    case "hpa":
                        zDim.setDimValue(MeteoMath.pressure2Height(zDim.getDimValue()));
                        zDim.setUnit("m");
                        break;
                    case "pa":
                        zDim.setDimValue(MeteoMath.pressure2Height(ArrayMath.div(zDim.getDimValue(), 100)));
                        zDim.setUnit("m");
                        break;
                    case "eta":
                        if (isWRF()) {
                            zDim.setDimValue(WRFUtil.getGPM1D(this.dataInfo.getDataInfo()).getArray());
                            zDim.setUnit("m");
                        }
                        break;
                }
            }

            array.asAscending();

            return array;
        } catch (InvalidRangeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
