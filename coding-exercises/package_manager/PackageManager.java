public class PackageManager {

    // Necessary constants for size and dimension thresolds
    private static final int BULKY_VOLUME_THRESHOLD = 1000000;
    private static final int DIMENSION_THRESHOLD = 150;
    private static final int HEAVY_MASS_THRESHOLD = 20;

    // Necessary constants to depict Stack name
    private static final String STANDARD = "STANDARD";
    private static final String SPECIAL = "SPECIAL";
    private static final String REJECTED = "REJECTED";

    /**
     * Primary method to sort by evaluating stack-name based on the provided details.<br/>
     * It first checks, a package is:<br/>
     * - "Bulky" if its volume (Width × Height × Length) is &gt;= 1,000,000 cubic-cm OR if any single
     * dimension is &gt;= 150 cm.<br/>
     * - "Heavy" if its mass is &gt;= 20 kg.<br/><br/>
     * Then it designate the package to the following stack based on:<br/>
     * - **STANDARD**: standard packages (those that are not bulky or heavy) can be handled normally.<br/>
     * - **SPECIAL**: packages that are either heavy or bulky can't be handled automatically.<br/>
     * - **REJECTED**: packages that are **both** heavy and bulky are rejected.<br/><br/>
     * Then returns the name of the stack where the package should go.
     * @param width - Width of package.
     * @param height - Height of package.
     * @param length - Length of package.
     * @param mass - Mass of package
     * @return Evaluated stack-name based on the provided details
     */
    public static String sort(int width, int height, int length, int mass) {
        if (width <= 0 || height <= 0 || length <= 0 || mass <= 0)
            throw new IllegalArgumentException("All inputs must be positive");

        if (mass == 0)
            throw new IllegalArgumentException("Mass cannot be ZERO");

        int volume = width * height * length;
        if (volume == 0)
            throw new IllegalArgumentException("Volume cannot be ZERO");

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

    public static void printAndAssertStackName(int width, int height,
                                               int length, int mass,
                                               String expectedStackName) {
        String stackName = sort(width, height, length, mass);
        System.out.println(String.format("Expected Stack = %s; Actual Stack = %s",
                expectedStackName, stackName));
        assert expectedStackName.equals(stackName) : "Should be " + expectedStackName;
    }

    public static void main(String[] args) {
        printAndAssertStackName(10, 10, 10, 10, STANDARD);
        printAndAssertStackName(58, 27, 15, 18, STANDARD);

        printAndAssertStackName(11, 12, 13, 21, SPECIAL);
        printAndAssertStackName(200, 10, 10, 10, SPECIAL);

        printAndAssertStackName(100, 100, 100, 20, REJECTED);
        printAndAssertStackName(200, 200, 200, 25, REJECTED);
    }

}
