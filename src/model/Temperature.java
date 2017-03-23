package model;


class Temperature {

    /**
     * Daily temperature every hour, starts at 0h
     */

	/*private static final double[] environmentalTemperature = {
		21, 20, 19, 18, 18, 19,
		20, 22, 24, 26, 28, 30,
		33, 34, 35, 36, 36, 35,
		34, 31, 29, 27, 25, 22
	};*/

    private static final double[] environmentalTemperature = {
            12, 12, 11, 11, 10, 10,
            11, 11, 12, 13, 15, 16,
            17, 19, 20, 22, 21, 20,
            18, 17, 15, 14, 13, 12
    };

    static double[] getEnvironmentalTemperature() {
        return environmentalTemperature;
    }
}
