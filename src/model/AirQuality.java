package model;


class AirQuality {

    /**
     * Daily air quality every hour, starts at 0h
     */

    private static final double[] airQuality = {
            4, 4, 4, 4, 4, 4,
            4, 4, 4, 4, 4, 4,
            4, 4, 4, 4, 4, 4,
            4, 4, 4, 4, 4, 4
    };

    static double[] getAirQuality() {
        return airQuality;
    }
}
