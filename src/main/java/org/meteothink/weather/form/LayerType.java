package org.meteothink.weather.form;

public enum LayerType {
    MAP_IMAGE {
        public String getNameCN() {
            return "地图图像";
        }
    },
    MAP_VECTOR {
        public String getNameCN() {
            return "地图界限";
        }
    },
    SLICE {
        public String getNameCN() {
            return "数据切片";
        }
    },
    VOLUME {
        public String getNameCN() {
            return "体绘制";
        }
    },
    ISO_SURFACE {
        public String getNameCN() {
            return "等值面";
        }
    },
    STREAM_LINE {
        public String getNameCN() {
            return "流线";
        }
    },
    WIND_VECTOR {
        public String getNameCN() {
            return "风场矢量";
        }
    };

    public abstract String getNameCN();

    public String toString() {
        return getNameCN();
    }
}
