import java.time.LocalTime;

public class RouteStop {
    private String stationName;
    private int distanceFromOriginKm;
    private LocalTime arrivalTime;
    private LocalTime departureTime;

    public RouteStop(String stationName, int distanceFromOriginKm, LocalTime arrivalTime, LocalTime departureTime) {
        if (stationName == null || stationName.trim().isEmpty()) throw new IllegalArgumentException("Station name is required.");
        if (distanceFromOriginKm < 0) throw new IllegalArgumentException("Distance cannot be negative.");
        this.stationName = stationName.trim();
        this.distanceFromOriginKm = distanceFromOriginKm;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getStationName() { return stationName; }
    public int getDistanceFromOriginKm() { return distanceFromOriginKm; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public LocalTime getDepartureTime() { return departureTime; }

    public String toDataRow() {
        return stationName.replace("|", " ") + "|" + distanceFromOriginKm + "|" + arrivalTime + "|" + departureTime;
    }

    public static RouteStop fromDataRow(String row) {
        String[] p = row.split("\\|", -1);
        return new RouteStop(p[0], Integer.parseInt(p[1]), LocalTime.parse(p[2]), LocalTime.parse(p[3]));
    }

    @Override
    public String toString() {
        return String.format("%-20s | %4d km | Arr: %s | Dep: %s", stationName, distanceFromOriginKm, arrivalTime, departureTime);
    }
}
