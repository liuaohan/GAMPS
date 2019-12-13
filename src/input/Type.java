package input;

public class Type {
    private static final String CO2 = "CO2\\";
    private static final String HUMI = "Humidity\\";
    private static final String LYS = "Lysimeter\\";
    private static final String MOI = "Moisture\\";
    private static final String PRE = "Pressure\\";
    private static final String RAD = "Radiation\\";
    private static final String SNOW = "Snow_height\\";
    private static final String TEMP = "Temperature\\";
    private static final String VOL = "Voltage\\";
    private static final String WINDD = "Wind_direction\\";
    private static final String WINDS = "Wind_speed\\";

    static String getFileName(String type) {
        String res = "";
        switch (type) {
            case "CO2":
                res = CO2;
                break;
            case "HUMI":
                res = HUMI;
                break;
            case "LYS":
                res = LYS;
                break;
            case "MOI":
                res = MOI;
                break;
            case "PRE":
                res = PRE;
                break;
            case "RAD":
                res = RAD;
                break;
            case "SNOW":
                res = SNOW;
                break;
            case "TEMP":
                res = TEMP;
                break;
            case "VOL":
                res = VOL;
                break;
            case "WINDD":
                res = WINDD;
                break;
            case "WINDS":
                res = WINDS;
                break;
            default:
        }
        return res;
    }
}
