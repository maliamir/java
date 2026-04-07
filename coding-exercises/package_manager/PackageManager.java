public class PackageManager {

    private static final int BULKY_VOLUME_THRESHOLD = 1000000;
    private static final int DIMENSION_THRESHOLD = 150;
    private static final int HEAVY_MASS_THRESHOLD = 20;

    private static final String STANDARD = "STANDARD";
    private static final String SPECIAL = "SPECIAL";
    private static final String REJECTED = "REJECTED";

    public static String sort(int width, int height, int length, int mass) {
        int volume = width * height * length;
        boolean isBulky = (volume >= BULKY_VOLUME_THRESHOLD ||
                width >= DIMENSION_THRESHOLD ||
                height >= DIMENSION_THRESHOLD ||
                length >= DIMENSION_THRESHOLD);
        boolean isHeavy = mass >= HEAVY_MASS_THRESHOLD;

        if (isBulky && isHeavy) {
            return REJECTED;
        } else if (isBulky || isHeavy) {
            return SPECIAL;
        } else {
            return STANDARD;
        }
    }

    public static void main(String[] args) {
        assert STANDARD.equals(sort(10, 10, 10, 10)) : "Should be " + STANDARD;
        assert STANDARD.equals(sort(-200, -200, -200, -25)) : "Should be " + STANDARD;

        assert SPECIAL.equals(sort(11, 12, 13, 21)) : "Should be " + SPECIAL + " (heavy)";
        assert SPECIAL.equals(sort(200, 10, 10, 10)) : "Should be " + SPECIAL + " (bulky)";

        assert REJECTED.equals(sort(100, 100, 100, 20)) : "Should be " + REJECTED;
        assert REJECTED.equals(sort(200, 200, 200, 25)) : "Should be " + REJECTED;
    }

}
