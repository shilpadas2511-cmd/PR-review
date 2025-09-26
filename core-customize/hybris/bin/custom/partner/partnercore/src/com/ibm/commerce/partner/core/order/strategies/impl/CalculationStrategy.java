import java.util.List;

public class CalculationStrategyTest {

    // Hardcoded password - a critical security issue
    private static final String PASSWORD = "SuperSeddfhgf12456637ghh";

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
}