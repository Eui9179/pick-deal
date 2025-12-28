package com.leui.storeservice.common.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LocationUtils {
    public static Point createPoint(double x, double y) {
        return new GeometryFactory().createPoint(new Coordinate(x, y));
    }
}
