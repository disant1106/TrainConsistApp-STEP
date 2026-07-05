import java.time.LocalDate;

public class Coach {
    private String coachNumber;
    private CoachType type;
    private int capacity;
    private double weightTons;
    private MaintenanceStatus maintenanceStatus;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDueDate;
    private String remarks;

    public Coach(String coachNumber, CoachType type) {
        this(coachNumber, type, type.getDefaultCapacity(), type.getApproximateWeightTons(),
                MaintenanceStatus.FIT, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(4), "");
    }

    public Coach(String coachNumber, CoachType type, int capacity, double weightTons, MaintenanceStatus maintenanceStatus,
                 LocalDate lastServiceDate, LocalDate nextServiceDueDate, String remarks) {
        if (coachNumber == null || coachNumber.trim().isEmpty()) throw new IllegalArgumentException("Coach number is required.");
        this.coachNumber = coachNumber.trim().toUpperCase();
        this.type = type;
        this.capacity = Math.max(0, capacity);
        this.weightTons = Math.max(0, weightTons);
        this.maintenanceStatus = maintenanceStatus;
        this.lastServiceDate = lastServiceDate;
        this.nextServiceDueDate = nextServiceDueDate;
        this.remarks = remarks == null ? "" : remarks.trim();
    }

    public String getCoachNumber() { return coachNumber; }
    public CoachType getType() { return type; }
    public int getCapacity() { return capacity; }
    public double getWeightTons() { return weightTons; }
    public MaintenanceStatus getMaintenanceStatus() { return maintenanceStatus; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public LocalDate getNextServiceDueDate() { return nextServiceDueDate; }
    public String getRemarks() { return remarks; }

    public void updateMaintenance(MaintenanceStatus status, LocalDate lastServiceDate, LocalDate nextServiceDueDate, String remarks) {
        this.maintenanceStatus = status;
        this.lastServiceDate = lastServiceDate;
        this.nextServiceDueDate = nextServiceDueDate;
        this.remarks = remarks == null ? "" : remarks.trim();
    }

    public boolean isMaintenanceDue(LocalDate date) {
        return maintenanceStatus == MaintenanceStatus.OVERDUE || maintenanceStatus == MaintenanceStatus.OUT_OF_SERVICE
                || (nextServiceDueDate != null && !nextServiceDueDate.isAfter(date));
    }

    public String toDataRow() {
        return String.join("|", coachNumber, type.toString(), String.valueOf(capacity), String.valueOf(weightTons),
                maintenanceStatus.toString(), lastServiceDate.toString(), nextServiceDueDate.toString(), remarks.replace("|", " "));
    }

    public static Coach fromDataRow(String row) {
        String[] p = row.split("\\|", -1);
        return new Coach(p[0], CoachType.valueOf(p[1]), Integer.parseInt(p[2]), Double.parseDouble(p[3]),
                MaintenanceStatus.valueOf(p[4]), LocalDate.parse(p[5]), LocalDate.parse(p[6]), p.length > 7 ? p[7] : "");
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-16s | Capacity: %3d | Weight: %5.1f tons | %-14s | Due: %s | %s",
                coachNumber, type.getDisplayName(), capacity, weightTons, maintenanceStatus, nextServiceDueDate, remarks);
    }
}
