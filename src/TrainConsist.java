import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TrainConsist {
    private String trainName;
    private final List<Coach> coaches = new ArrayList<>();
    private final List<RouteStop> routeStops = new ArrayList<>();

    public TrainConsist(String trainName) { this.trainName = trainName; }

    public void addCoach(Coach coach) {
        if (findCoach(coach.getCoachNumber()) != null) throw new IllegalArgumentException("Duplicate coach number not allowed.");
        coaches.add(coach);
    }

    public boolean removeCoach(String coachNumber) { return coaches.removeIf(c -> c.getCoachNumber().equalsIgnoreCase(coachNumber)); }

    public Coach findCoach(String coachNumber) {
        for (Coach coach : coaches) if (coach.getCoachNumber().equalsIgnoreCase(coachNumber)) return coach;
        return null;
    }

    public void moveCoach(String coachNumber, int newPosition) {
        if (newPosition < 1 || newPosition > coaches.size()) throw new IllegalArgumentException("Invalid position.");
        Coach coach = findCoach(coachNumber);
        if (coach == null) throw new IllegalArgumentException("Coach not found.");
        coaches.remove(coach);
        coaches.add(newPosition - 1, coach);
    }

    public void sortByCoachNumber() { coaches.sort(Comparator.comparing(Coach::getCoachNumber)); }

    public void addRouteStop(RouteStop stop) {
        routeStops.add(stop);
        routeStops.sort(Comparator.comparingInt(RouteStop::getDistanceFromOriginKm));
    }

    public void displayCoaches() {
        if (coaches.isEmpty()) { System.out.println("No coaches added."); return; }
        for (int i = 0; i < coaches.size(); i++) System.out.printf("%02d. %s%n", i + 1, coaches.get(i));
    }

    public void displayRoute() {
        if (routeStops.isEmpty()) { System.out.println("No route stops added."); return; }
        routeStops.forEach(System.out::println);
        System.out.println("Total route distance: " + getTotalRouteDistanceKm() + " km");
        Duration duration = getTravelDuration();
        if (duration != null) {
            System.out.printf("Approx travel time: %d hours %d minutes%n", duration.toHours(), duration.toMinutesPart());
            if (duration.toHours() > 0) System.out.printf("Average speed: %.1f km/h%n", getTotalRouteDistanceKm() / (duration.toMinutes() / 60.0));
        }
    }

    public int getTotalPassengerCapacity() { return coaches.stream().mapToInt(Coach::getCapacity).sum(); }
    public double getTotalWeightTons() { return coaches.stream().mapToDouble(Coach::getWeightTons).sum(); }
    public int getTotalRouteDistanceKm() { return routeStops.stream().mapToInt(RouteStop::getDistanceFromOriginKm).max().orElse(0); }

    public Duration getTravelDuration() {
        if (routeStops.size() < 2) return null;
        LocalTime start = routeStops.get(0).getDepartureTime();
        LocalTime end = routeStops.get(routeStops.size() - 1).getArrivalTime();
        Duration duration = Duration.between(start, end);
        if (duration.isNegative() || duration.isZero()) duration = duration.plusHours(24);
        return duration;
    }

    public void displayTypeSummary() {
        Map<CoachType, Integer> counts = new EnumMap<>(CoachType.class);
        for (Coach coach : coaches) counts.put(coach.getType(), counts.getOrDefault(coach.getType(), 0) + 1);
        System.out.println("--- Coach Type Summary ---");
        for (Map.Entry<CoachType, Integer> entry : counts.entrySet()) {
            System.out.printf("%-18s : %d%n", entry.getKey().getDisplayName(), entry.getValue());
        }
        System.out.println("Total coaches: " + coaches.size());
        System.out.println("Total passenger capacity: " + getTotalPassengerCapacity());
        System.out.printf("Approx total weight: %.1f tons%n", getTotalWeightTons());
    }

    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        if (coaches.isEmpty()) { result.addError("Train consist has no coaches."); return result; }
        if (coaches.get(0).getType() != CoachType.ENGINE) result.addError("First coach should be an engine.");
        CoachType lastType = coaches.get(coaches.size() - 1).getType();
        if (lastType != CoachType.BRAKE_VAN && lastType != CoachType.LUGGAGE) result.addWarning("Last coach is usually expected to be a brake/guard van or luggage van.");
        long pantryCount = coaches.stream().filter(c -> c.getType() == CoachType.PANTRY).count();
        if (pantryCount > 1) result.addWarning("More than one pantry car found.");
        if (coaches.size() > 24) result.addWarning("Train is very long. Check platform length and operating constraints.");
        for (Coach coach : coaches) {
            if (coach.getMaintenanceStatus() == MaintenanceStatus.OUT_OF_SERVICE) result.addError(coach.getCoachNumber() + " is out of service.");
            if (coach.isMaintenanceDue(LocalDate.now())) result.addWarning(coach.getCoachNumber() + " has maintenance due/overdue.");
        }
        for (int i = 1; i < routeStops.size(); i++) {
            if (routeStops.get(i).getDistanceFromOriginKm() <= routeStops.get(i - 1).getDistanceFromOriginKm()) {
                result.addWarning("Route distances should strictly increase between stops."); break;
            }
        }
        return result;
    }

    public int calculateSafetyScore() {
        ValidationResult result = validate();
        int score = 100 - result.getErrors().size() * 25 - result.getWarnings().size() * 8;
        if (getTotalWeightTons() > 1300) score -= 10;
        return Math.max(0, Math.min(100, score));
    }

    public void displayMaintenanceDue() {
        boolean found = false;
        System.out.println("--- Maintenance Due / Overdue ---");
        for (Coach coach : coaches) {
            if (coach.isMaintenanceDue(LocalDate.now())) { System.out.println(coach); found = true; }
        }
        if (!found) System.out.println("No coaches currently due for maintenance.");
    }

    public Path exportReport() throws IOException {
        Path dir = Paths.get("reports");
        Files.createDirectories(dir);
        Path file = dir.resolve("train_consist_report.txt");
        List<String> lines = new ArrayList<>();
        lines.add("TRAIN CONSIST REPORT");
        lines.add("Train: " + trainName);
        lines.add("Total coaches: " + coaches.size());
        lines.add("Passenger capacity: " + getTotalPassengerCapacity());
        lines.add(String.format("Approx weight: %.1f tons", getTotalWeightTons()));
        lines.add("Route distance: " + getTotalRouteDistanceKm() + " km");
        lines.add("Safety score: " + calculateSafetyScore() + "/100");
        lines.add(""); lines.add("Coach Order:");
        for (int i = 0; i < coaches.size(); i++) lines.add(String.format("%02d. %s", i + 1, coaches.get(i)));
        lines.add(""); lines.add("Route Stops:");
        for (RouteStop stop : routeStops) lines.add(stop.toString());
        ValidationResult validation = validate();
        lines.add(""); lines.add("Validation Errors: " + validation.getErrors().size());
        for (String error : validation.getErrors()) lines.add("ERROR: " + error);
        lines.add("Validation Warnings: " + validation.getWarnings().size());
        for (String warning : validation.getWarnings()) lines.add("WARNING: " + warning);
        Files.write(file, lines);
        return file;
    }

    public void saveToFiles(String folderName) throws IOException {
        Path dir = Paths.get(folderName);
        Files.createDirectories(dir);
        List<String> coachRows = new ArrayList<>();
        for (Coach coach : coaches) coachRows.add(coach.toDataRow());
        Files.write(dir.resolve("coaches.csv"), coachRows);
        List<String> routeRows = new ArrayList<>();
        for (RouteStop stop : routeStops) routeRows.add(stop.toDataRow());
        Files.write(dir.resolve("route.csv"), routeRows);
    }

    public void loadFromFiles(String folderName) throws IOException {
        Path dir = Paths.get(folderName);
        Path coachFile = dir.resolve("coaches.csv");
        Path routeFile = dir.resolve("route.csv");
        if (!Files.exists(coachFile)) throw new IOException("coaches.csv not found.");
        coaches.clear(); routeStops.clear();
        for (String line : Files.readAllLines(coachFile)) if (!line.trim().isEmpty()) addCoach(Coach.fromDataRow(line));
        if (Files.exists(routeFile)) for (String line : Files.readAllLines(routeFile)) if (!line.trim().isEmpty()) addRouteStop(RouteStop.fromDataRow(line));
    }

    public void loadSampleData() {
        coaches.clear(); routeStops.clear();
        addCoach(new Coach("ENG-01", CoachType.ENGINE));
        addCoach(new Coach("A1", CoachType.AC_FIRST));
        addCoach(new Coach("B1", CoachType.AC_THREE_TIER));
        addCoach(new Coach("S1", CoachType.SLEEPER));
        addCoach(new Coach("S2", CoachType.SLEEPER));
        addCoach(new Coach("GS1", CoachType.GENERAL));
        addCoach(new Coach("PC1", CoachType.PANTRY));
        addCoach(new Coach("LV1", CoachType.LUGGAGE));
        addCoach(new Coach("BV1", CoachType.BRAKE_VAN));
        addRouteStop(new RouteStop("Kolkata", 0, LocalTime.of(6, 0), LocalTime.of(6, 15)));
        addRouteStop(new RouteStop("Kharagpur", 116, LocalTime.of(8, 10), LocalTime.of(8, 15)));
        addRouteStop(new RouteStop("Bhubaneswar", 441, LocalTime.of(13, 30), LocalTime.of(13, 45)));
        addRouteStop(new RouteStop("Visakhapatnam", 884, LocalTime.of(21, 0), LocalTime.of(21, 10)));
    }
}
