package org.meteothink.weather.layer;

public enum LayerType {

    MAP_IMAGE {
        public String getName() {
            return bundle.getString("RenderDockable.layer.MAP_IMAGE.name");
        }
    },
    MAP_VECTOR {
        public String getName() {
            return bundle.getString("RenderDockable.layer.MAP_VECTOR.name");
        }
    },
    SLICE {
        public String getName() {
            return bundle.getString("RenderDockable.layer.SLICE.name");
        }
    },
    VOLUME {
        public String getName() {
            return bundle.getString("RenderDockable.layer.VOLUME.name");
        }
    },
    ISO_SURFACE {
        public String getName() {
            return bundle.getString("RenderDockable.layer.ISO_SURFACE.name");
        }
    },
    STREAMLINE {
        public String getName() {
            return bundle.getString("RenderDockable.layer.STREAMLINE.name");
        }
    },
    WIND_VECTOR {
        public String getName() {
            return bundle.getString("RenderDockable.layer.WIND_VECTOR.name");
        }
    };

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/RenderDockable");

    public abstract String getName();

    public String toString() {
        return getName();
    }
}
