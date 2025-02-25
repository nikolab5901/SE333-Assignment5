package org.example.Barnes;

import java.util.Objects;

public class Book {
    private String ISBN;
    private int price;
    private int amount;

    public Book(String ISBN, int price, int quantity) {
        this.ISBN = ISBN;
        this.price = price;
        this.amount = quantity;
    }

    public int getPrice() {
        return price;
    }
    public int getQuantity() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return ISBN.equals(book.ISBN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBN);
    }
}