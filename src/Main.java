import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final TrainConsist consist = new TrainConsist("Demo Express");

    public static void main(String[] args) {
        System.out.println("=== Train Consist Management App ===");
        consist.loadSampleData();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1: consist.displayCoaches(); break;
                    case 2: addCoach(); break;
                    case 3: removeCoach(); break;
                    case 4: moveCoach(); break;
                    case 5: searchCoach(); break;
                    case 6: consist.sortByCoachNumber(); System.out.println("Sorted by coach number."); break;
                    case 7: updateMaintenance(); break;
                    case 8: consist.displayMaintenanceDue(); break;
                    case 9: addRouteStop(); break;
                    case 10: consist.displayRoute(); break;
                    case 11: consist.displayTypeSummary(); break;
                    case 12: consist.validate().print(); System.out.println("Safety score: " + consist.calculateSafetyScore() + "/100"); break;
                    case 13: saveData(); break;
                    case 14: loadData(); break;
                    case 15: exportReport(); break;
                    case 16: consist.loadSampleData(); System.out.println("Sample train data reloaded."); break;
                    case 0: running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Display train consist");
        System.out.println("2. Add coach");
        System.out.println("3. Remove coach");
        System.out.println("4. Move coach position");
        System.out.println("5. Search coach");
        System.out.println("6. Sort coaches by number");
        System.out.println("7. Update coach maintenance");
        System.out.println("8. Show maintenance due/overdue");
        System.out.println("9. Add route stop");
        System.out.println("10. Display route summary");
        System.out.println("11. Coach type/capacity summary");
        System.out.println("12. Validate consist + safety score");
        System.out.println("13. Save data to files");
        System.out.println("14. Load data from files");
        System.out.println("15. Export train report");
        System.out.println("16. Reload sample data");
        System.out.println("0. Exit");
    }

    private static void addCoach() {
        String number = readLine("Coach number: ");
        CoachType.printOptions();
        CoachType type = chooseCoachType("Coach type: ");
        consist.addCoach(new Coach(number, type));
        System.out.println("Coach added.");
    }

    private static void removeCoach() {
        System.out.println(consist.removeCoach(readLine("Coach number to remove: ")) ? "Coach removed." : "Coach not found.");
    }

    private static void moveCoach() {
        consist.moveCoach(readLine("Coach number: "), readInt("New position (1-based): "));
        System.out.println("Coach moved.");
    }

    private static void searchCoach() {
        Coach coach = consist.findCoach(readLine("Coach number: "));
        System.out.println(coach == null ? "Coach not found." : coach);
    }

    private static void updateMaintenance() {
        Coach coach = consist.findCoach(readLine("Coach number: "));
        if (coach == null) throw new IllegalArgumentException("Coach not found.");
        MaintenanceStatus[] statuses = MaintenanceStatus.values();
        for (int i = 0; i < statuses.length; i++) System.out.println((i + 1) + ". " + statuses[i]);
        int choice = readInt("Maintenance status: ");
        if (choice < 1 || choice > statuses.length) throw new IllegalArgumentException("Invalid status.");
        LocalDate lastService = readDate("Last service date (YYYY-MM-DD): ");
        LocalDate nextDue = readDate("Next service due date (YYYY-MM-DD): ");
        String remarks = readLine("Remarks: ");
        coach.updateMaintenance(statuses[choice - 1], lastService, nextDue, remarks);
        System.out.println("Maintenance updated.");
    }

    private static void addRouteStop() {
        String station = readLine("Station name: ");
        int distance = readInt("Distance from origin (km): ");
        LocalTime arrival = readTime("Arrival time (HH:mm): ");
        LocalTime departure = readTime("Departure time (HH:mm): ");
        consist.addRouteStop(new RouteStop(station, distance, arrival, departure));
        System.out.println("Route stop added.");
    }

    private static void saveData() throws IOException { consist.saveToFiles("data"); System.out.println("Saved coaches and route to data/ folder."); }
    private static void loadData() throws IOException { consist.loadFromFiles("data"); System.out.println("Loaded coaches and route from data/ folder."); }
    private static void exportReport() throws IOException { System.out.println("Report generated: " + consist.exportReport().toAbsolutePath()); }

    private static CoachType chooseCoachType(String prompt) {
        int typeChoice = readInt(prompt);
        CoachType[] types = CoachType.values();
        if (typeChoice < 1 || typeChoice > types.length) throw new IllegalArgumentException("Invalid coach type.");
        return types[typeChoice - 1];
    }

    private static String readLine(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private static int readInt(String prompt) { while (true) { try { return Integer.parseInt(readLine(prompt)); } catch (NumberFormatException e) { System.out.println("Enter a valid integer."); } } }
    private static LocalDate readDate(String prompt) { while (true) { try { return LocalDate.parse(readLine(prompt)); } catch (DateTimeParseException e) { System.out.println("Enter date in YYYY-MM-DD format."); } } }
    private static LocalTime readTime(String prompt) { while (true) { try { return LocalTime.parse(readLine(prompt), DateTimeFormatter.ofPattern("HH:mm")); } catch (Exception e) { System.out.println("Enter time in HH:mm format."); } } }
}
