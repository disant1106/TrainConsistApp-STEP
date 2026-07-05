import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void addError(String error) { errors.add(error); }
    public void addWarning(String warning) { warnings.add(warning); }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    public boolean isValid() { return errors.isEmpty(); }

    public void print() {
        System.out.println("--- Consist Validation ---");
        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println("No issues found. Train consist looks valid.");
            return;
        }
        if (!errors.isEmpty()) {
            System.out.println("Errors:");
            for (String error : errors) System.out.println("- " + error);
        }
        if (!warnings.isEmpty()) {
            System.out.println("Warnings:");
            for (String warning : warnings) System.out.println("- " + warning);
        }
    }
}
