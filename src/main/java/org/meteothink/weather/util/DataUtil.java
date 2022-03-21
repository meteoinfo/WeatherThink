package org.meteothink.weather.util;

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

public class DataUtil {
    /**
     * Read 3D array from meteo data info
     * @param dataInfo Meteo data info
     * @param varName Variable name
     * @param timeIndex Time index
     * @return 3D array
     * @throws InvalidRangeException
     */
    public static Array read3DArray(MeteoDataInfo dataInfo, String varName, int timeIndex) throws InvalidRangeException {
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

    /**
     * Read X, Y and Z arrays
     * @param dataInfo The meteo data info
     * @return X, Y, Z arrays
     */
    public static Array[] readXYZArray(MeteoDataInfo dataInfo) {
        Dimension dimX = dataInfo.getDataInfo().getXDimension();
        Dimension dimY = dataInfo.getDataInfo().getYDimension();
        Dimension dimZ = dataInfo.getDataInfo().getZDimension();
        Array zArr = dimZ.getDimArray();
        zArr = MeteoMath.press2Height(zArr);

        return new Array[]{dimX.getDimArray(), dimY.getDimArray(), zArr};
    }
}
