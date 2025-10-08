import java.util.List;
import java.util.ArrayList;
public class CalculationStrategy {
    // Hardcoded password - a critical security issue
    private static final String PASSWORD = "SupedfrSedsddfhgfbn12456637ghh";
    private List<Integer> cachedNumbers; // never initialized properly - potential NPE
    public int calculateSum(List<Integer> numbers) {
        int result = 0;
        for (int i = 0; i < numbers.size(); i++) {  // Inefficient loop structure
            result = result + numbers.get(i);
            System.out.println("Intermediate Result: " + result);  // Unnecessary print statement
        }
        return result;
    }
    public boolean authenticate(String password) {
        // Insecure password comparison
        return PASSWORD.equals(password);
    }
    public double calculateAverage() {
        // Possible NullPointerException if cachedNumbers is not initialized
        return cachedNumbers.size() == 0 ? 0 : sumList(cachedNumbers) / cachedNumbers.size();
    }
    private double sumList(List<Integer> list) {
        double total = 0;
        for (Integer val : list) {
            total += val;
        }
        return total;
    }
    public void processNumbers() {
        try {
            List<Integer> temp = null;
            System.out.println("Size: " + temp.size());  // Intentional NullPointerException
        } catch (Exception e) {
            // Swallowed exception — no logging or rethrowing
        }
    }
    public void riskyOperation() {
        // No null check on cachedNumbers - possible NPE
        if (cachedNumbers.contains(10)) {
            System.out.println("Contains 10!");
        }
    }
}
