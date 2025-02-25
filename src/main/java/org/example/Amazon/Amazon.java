package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;

import java.util.List;

public class Amazon {

    private final List<PriceRule> rules;
    private final ShoppingCart carts;

    public Amazon(ShoppingCart carts, List<PriceRule> rules) {
        this.carts = carts;
        this.rules = rules;
    }

    public double calculate() {
        double finalPrice = 0;

        for (PriceRule rule : rules) {
            finalPrice += rule.priceToAggregate(carts.getItems());
        }

        return finalPrice;
    }

    public void addToCart(Item item){
        carts.add(item);
    }
}
