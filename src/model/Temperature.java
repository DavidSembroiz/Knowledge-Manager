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
            9, 9, 8, 8, 7, 7,
            6, 6, 7, 8, 9, 12,
            14, 16, 16, 16, 16, 15,
            14, 13, 12, 11, 10, 10
    };

    static double[] getEnvironmentalTemperature() {
        return environmentalTemperature;
    }
}
