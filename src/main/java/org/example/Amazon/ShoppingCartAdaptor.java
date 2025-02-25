package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Class responsible for querying and saving invoices in the database
public class ShoppingCartAdaptor implements ShoppingCart {

    private Database connection;  // Represents the database connection object

    // Constructor that initializes the database connection using dependency injection
    public ShoppingCartAdaptor(Database connection) {
        this.connection = connection;
    }

    @Override
    public void add(Item item) {
        connection.withSql(() -> {  // Executes SQL operations within the database connection
            try (var ps = connection.getConnection().prepareStatement("insert into shoppingcart (name, type, quantity, priceperunit) values (?,?,?,?)")) {  // Prepares the SQL query to insert a new invoice
                ps.setString(1, item.getName());  // Sets the customer name in the query
                ps.setString(2, item.getType().name());  // Sets the invoice value in the query
                ps.setInt(3, item.getQuantity());  // Sets the invoice value in the query
                ps.setDouble(4, item.getPricePerUnit());  // Sets the invoice value in the query
                ps.execute();  // Executes the insert query

                connection.getConnection().commit();  // Commits the transaction to make the changes permanent
            }
            return null;  // Returns null as this operation does not need to return any value
        });
    }

    @Override
    public List<Item> getItems() {
        return connection.withSql(() -> {  // Executes SQL operations within the database connection
            try (var ps = connection.getConnection().prepareStatement("select * from shoppingcart")) {  // Prepares the SQL query to select all invoices
                final var rs = ps.executeQuery();  // Executes the query and stores the result set

                List<Item> ShoppingCart = new ArrayList<>();  // Creates a list to store all retrieved invoices
                while (rs.next()) {  // Iterates through each row in the result set
                    ShoppingCart.add(new Item(ItemType.valueOf(rs.getString("type")),rs.getString("name"),
                            rs.getInt("quantity"),rs.getDouble("priceperunit")));  // Creates a new Invoice object and adds it to the list
                }

                return ShoppingCart;  //  Returns the list of all invoices
            }
        });
    }

    @Override
    public int numberOfItems() {
        return connection.withSql(() -> {
            try(var ps = connection.getConnection().prepareStatement("select count(*) from shoppingcart")){
                return ps.getFetchSize();
            }
        });
    }
}
