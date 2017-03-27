package model;


class Light {

    /**
     * Daily luminosity every hour, starts at 0h
     */

    private static final double[] environmentalLight = {
            20, 20, 20, 40, 60, 60,
            100, 120, 140, 200, 400, 600,
            800, 900, 1000, 1200, 1400, 1500,
            1400, 1200, 600, 200, 100, 20
    };

    static double[] getEnvironmentalLight() {
        return environmentalLight;
    }
}
