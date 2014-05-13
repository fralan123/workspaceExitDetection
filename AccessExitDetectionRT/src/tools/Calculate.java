package tools;


/**
 *
 * @author Luis Aram Tafoya Diaz (aramtd at gmail.com)
 */
public class Calculate {

    public static int taos(int VisibleLight) {
        final int CHORD_VAL[] = {0, 16, 49, 115, 247, 511, 1039, 2095};
        final int STEP_VAL[] = {1, 2, 4, 8, 16, 32, 64, 128};
        int chordVal, stepVal;
        int lightVal = 0;

        chordVal = (VisibleLight >> 4) & 7;
        stepVal = VisibleLight & 15;
        //ADC Count Value
        lightVal = CHORD_VAL[chordVal] + stepVal * STEP_VAL[chordVal];
        return lightVal;
    }

    public static double sensirion(float Temperature, int Humidity) {
        double converted;

        converted = (-2.0468 + 0.0367 * (double) Humidity - 0.0000015955 * Math.pow((double) Humidity, (double) 2))
                + (Temperature - 25) * (0.01 + 0.00008 * (double) Humidity);

        return converted;
    }
    
    public static float intersema(int Temperature){
        float temp = Temperature;
        return temp/10;
    }

}
