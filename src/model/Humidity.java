package model;


class Humidity {

    /**
     * Daily humidity every hour, starts at 0h
     */

    private static final double[] environmentalHumidity = {
            48, 48, 50, 54, 56, 56,
            55, 50, 47, 42, 38, 35,
            33, 31, 29, 29, 29, 32,
            35, 37, 38, 41, 44, 48
    };

    static double[] getEnvironmentalHumidity() {
        return environmentalHumidity;
    }
}
