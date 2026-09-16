package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartSteps {

    private double subtotal;
    private double total;

    @Given("a shopping cart with a subtotal of {double}")
    public void aShoppingCartWithSubtotal(double subtotal) {
        this.subtotal = subtotal;
        this.total = subtotal;
    }

    @When("a {int} percent discount is applied")
    public void aPercentDiscountIsApplied(int percent) {
        this.total = subtotal - (subtotal * percent / 100.0);
    }

    @Then("the cart total should be {double}")
    public void theCartTotalShouldBe(double expected) {
        assertEquals(expected, total, 0.001);
    }
}
