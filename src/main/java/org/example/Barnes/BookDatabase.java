package org.example.Barnes;

public interface BookDatabase {
        Book findByISBN(String ISBN);
}