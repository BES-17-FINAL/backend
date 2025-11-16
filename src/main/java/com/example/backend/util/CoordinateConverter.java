package com.example.backend.util;

public class CoordinateConverter {

    public static double[] wgs84ToKakao(double longitude, double latitude) {
        return new double[]{longitude, latitude};
    }

    public static double[] kakaoToWgs84(double x, double y) {
        return new double[]{x, y};
    }
}

