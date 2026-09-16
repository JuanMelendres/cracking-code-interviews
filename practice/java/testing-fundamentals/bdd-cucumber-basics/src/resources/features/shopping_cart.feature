Feature: Shopping cart discount
  As a shopper, when a discount is applied to my cart, the total should
  reflect the discount correctly, regardless of the exact percentage.

  Scenario: Applying a percentage discount to a cart
    Given a shopping cart with a subtotal of 100.00
    When a 20 percent discount is applied
    Then the cart total should be 80.00

  Scenario Outline: Applying various discount percentages
    Given a shopping cart with a subtotal of <subtotal>
    When a <percent> percent discount is applied
    Then the cart total should be <expected>

    Examples:
      | subtotal | percent | expected |
      | 100.00   | 10      | 90.00    |
      | 200.00   | 25      | 150.00   |
      | 50.00    | 0       | 50.00    |
