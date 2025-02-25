package org.example.Barnes;

import java.util.Map;

class BarnesAndNoble {

    private BookDatabase bookDatabase;
    private BuyBookProcess process;


    public BarnesAndNoble(BookDatabase bookDatabase, BuyBookProcess process) {
        this.bookDatabase = bookDatabase;
        this.process = process;
    }

    private void retrieveBook(String ISBN, int quantity, PurchaseSummary purchaseSummary) {
        Book book = bookDatabase.findByISBN(ISBN);
        if (book.getQuantity() < quantity) {
            purchaseSummary.addUnavailable(book, quantity - book.getQuantity());
            quantity = book.getQuantity();
        }

        purchaseSummary.addToTotalPrice(quantity * book.getPrice());
        process.buyBook(book, quantity);
    }

    // get the total price in the cart.
    public PurchaseSummary getPriceForCart(Map<String, Integer> order) {
        if(order==null)
            return null;

        PurchaseSummary purchaseSummary = new PurchaseSummary();
        for (String ISBN : order.keySet())
            retrieveBook(ISBN, order.get(ISBN), purchaseSummary);
        return purchaseSummary;
    }
}