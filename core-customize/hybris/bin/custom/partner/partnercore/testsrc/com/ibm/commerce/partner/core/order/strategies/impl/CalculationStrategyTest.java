import org.junit.jupiter.api.Test;

public class CalculationStrategyTest {

    private final CalculationStrategy strategy = new CalculationStrategy();

    @Test
    public void testCalculateSum() {
        // Bad test: No assertions, just prints the result
        System.out.println("Sum: " + strategy.calculateSum(java.util.Arrays.asList(1, 2, 3)));
    }



    @Test
    public void testAuthentication() {
        // Bad test: Hardcoded assumption that password works
        if (strategy.authenticate("SuperSecret123")) {
            System.out.println("Authentication successful!");
        } else {
            System.out.println("Authentication failed!");
        }
    }

    @Test
    public void testNullInput() {
        // Bad test: Ignores exceptions and lets them crash the test
        strategy.calculateSum(null);
    }

    @Test
    public void testNegativeNumbers() {
        // Bad test: Assumes the calculation is correct without verification
        strategy.calculateSum(java.util.Arrays.asList(-1, -2, -3));
    }
}