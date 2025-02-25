package org.example.Amazon;

import java.util.List;

    public interface ShoppingCart{

        public void add(Item item);
        public List<Item> getItems();
        public int numberOfItems();
    }
