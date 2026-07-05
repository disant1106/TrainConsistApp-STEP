public enum CoachType {
    ENGINE("Engine", 0, 120.0),
    AC_FIRST("AC First Class", 24, 55.0),
    AC_THREE_TIER("AC Three Tier", 64, 52.0),
    SLEEPER("Sleeper", 72, 48.0),
    GENERAL("General", 100, 45.0),
    PANTRY("Pantry Car", 0, 50.0),
    LUGGAGE("Luggage Van", 0, 42.0),
    BRAKE_VAN("Brake / Guard Van", 0, 40.0);

    private final String displayName;
    private final int defaultCapacity;
    private final double approximateWeightTons;

    CoachType(String displayName, int defaultCapacity, double approximateWeightTons) {
        this.displayName = displayName;
        this.defaultCapacity = defaultCapacity;
        this.approximateWeightTons = approximateWeightTons;
    }

    public String getDisplayName() { return displayName; }
    public int getDefaultCapacity() { return defaultCapacity; }
    public double getApproximateWeightTons() { return approximateWeightTons; }

    public static void printOptions() {
        CoachType[] values = CoachType.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%d. %s | Capacity: %d | Weight: %.1f tons%n",
                    i + 1, values[i].displayName, values[i].defaultCapacity, values[i].approximateWeightTons);
        }
    }
}
