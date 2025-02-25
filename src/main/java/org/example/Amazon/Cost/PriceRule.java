package org.example.Amazon.Cost;

import org.example.Amazon.Item;

import java.util.List;

public interface PriceRule {
    double priceToAggregate(List<Item> cart);
}
