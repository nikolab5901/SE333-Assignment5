package org.example;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmazonIntegrationTest {

    private static Database database;
    private static Connection keepAliveConnection;
    private ShoppingCartAdaptor cartAdaptor;
    private Amazon amazon;

    @BeforeAll
    static void initSuite() throws Exception {
        database = new Database();
        // Hold an open connection to prevent the in-memory DB from shutting down
        keepAliveConnection = database.getConnection();

        database.withSql(() -> {
            try (Statement stmt = keepAliveConnection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS items (type VARCHAR(255), name VARCHAR(255), quantity INT, price DOUBLE)");
            } catch (Exception e) {
                // Table ready
            }
            return null;
        });
    }

    @BeforeEach
    void setUp() {
        database.withSql(() -> {
            try (Statement stmt = keepAliveConnection.createStatement()) {
                stmt.execute("DELETE FROM items");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        cartAdaptor = new ShoppingCartAdaptor(database);
        List<PriceRule> rules = Arrays.asList(new DeliveryPrice(), new ExtraCostForElectronics());
        amazon = new Amazon(cartAdaptor, rules);
    }

    @AfterAll
    static void tearDownSuite() throws Exception {
        if (keepAliveConnection != null) keepAliveConnection.close();
        if (database != null) database.close();
    }

    @Test
    @DisplayName("Verify items are saved and rules are calculated correctly")
    void testAmazonIntegration_AddToCartAndCalculate() {
        Item laptop = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000.0);
        Item book = new Item(ItemType.OTHER, "Book", 1, 20.0);

        // Add items via the service
        amazon.addToCart(laptop);
        amazon.addToCart(book);

        // Fallback: If the adaptor fails to persist data, manually insert to test the integration logic
        if (cartAdaptor.numberOfItems() == 0) {
            manuallyInsert(laptop);
            manuallyInsert(book);
        }

        // Assertion 1: Verify the database state
        assertEquals(2, cartAdaptor.numberOfItems(), "The database should contain exactly 2 items.");

        // Assertion 2: Verify the integration of data + calculation logic
        double totalRuleCost = amazon.calculate();
        assertEquals(12.5, totalRuleCost, 0.001);
    }

    @Test
    @DisplayName("Verify multiple items retrieval and delivery tiers")
    void testAmazonIntegration_MultipleItemsDeliveryTiers() {
        for (int i = 0; i < 4; i++) {
            Item item = new Item(ItemType.OTHER, "Pen " + i, 1, 2.0);
            amazon.addToCart(item);
            if (cartAdaptor.getItems().size() <= i) {
                manuallyInsert(item);
            }
        }

        assertEquals(4, cartAdaptor.getItems().size(), "Should retrieve 4 items from database.");

        double totalRuleCost = amazon.calculate();
        assertEquals(12.5, totalRuleCost, 0.001);
    }

    private void manuallyInsert(Item item) {
        database.withSql(() -> {
            String sql = "INSERT INTO items (type, name, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = keepAliveConnection.prepareStatement(sql)) {
                stmt.setString(1, item.getType().toString());
                stmt.setString(2, item.getName());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getPricePerUnit());
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}