package org.example;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.Amazon.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmazonIntegrationTest {

    private static Database database;
    private ShoppingCartAdaptor cartAdaptor;
    private Amazon amazon;

    @BeforeAll
    static void setupDatabase() {
        database = new Database();
    }

    @BeforeEach
    void setUp() {
        database.resetDatabase();

        cartAdaptor = new ShoppingCartAdaptor(database);
        List<PriceRule> rules = Arrays.asList(new DeliveryPrice(), new ExtraCostForElectronics());

        amazon = new Amazon(cartAdaptor, rules);
    }

    @AfterAll
    static void tearDown() {
        database.close();
    }

    @Test
    @DisplayName("specification-based tests")
    void testAmazonIntegration_AddToCartAndCalculate() {

        Item laptop = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000.0);
        Item book = new Item(ItemType.OTHER, "Book", 1, 20.0);

        amazon.addToCart(laptop);
        amazon.addToCart(book);

        double totalRuleCost = amazon.calculate();


        assertEquals(2, cartAdaptor.numberOfItems());
        assertEquals(12.5, totalRuleCost, 0.001);
    }

    @Test
    @DisplayName("structural-based tests")
    void testAmazonIntegration_MultipleItemsDeliveryTiers() {


        for (int i = 0; i < 4; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Pen " + i, 1, 2.0));
        }

        double totalRuleCost = amazon.calculate();


        assertEquals(4, cartAdaptor.getItems().size());
        assertEquals(12.5, totalRuleCost, 0.001);
    }
}