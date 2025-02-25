package org.example.Barnes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PurchaseSummary {
    private int totalPrice;
    private Map<Book, Integer> unavailable;

    public PurchaseSummary() {
        this.unavailable = new HashMap<>();
        this.totalPrice = 0;
    }

    public void addUnavailable(Book book, int unavailableQty){
        this.unavailable.put(book, unavailableQty);
    }

    public void addToTotalPrice(int valueToAdd) {
        totalPrice += valueToAdd;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public Map<Book, Integer> getUnavailable() {
        return Collections.unmodifiableMap(unavailable);
    }
}