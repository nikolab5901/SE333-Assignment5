package org.example;

import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;
    private PriceRule mockRule1;
    private PriceRule mockRule2;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        mockCart = mock(ShoppingCart.class);
        mockRule1 = mock(PriceRule.class);
        mockRule2 = mock(PriceRule.class);

        List<PriceRule> rules = Arrays.asList(mockRule1, mockRule2);

        amazon = new Amazon(mockCart, rules);
    }

    @Test
    @DisplayName("specification-based tests")
    void testAddToCart_CallsCartAdd() {


        Item dummyItem = mock(Item.class);

        amazon.addToCart(dummyItem);


        verify(mockCart, times(1)).add(dummyItem);
    }

    @Test
    @DisplayName("structural-based tests")
    void testCalculate_SumsUpAllRules() {

        List<Item> dummyItems = Arrays.asList(mock(Item.class), mock(Item.class));
        when(mockCart.getItems()).thenReturn(dummyItems);

        when(mockRule1.priceToAggregate(dummyItems)).thenReturn(10.5);
        when(mockRule2.priceToAggregate(dummyItems)).thenReturn(5.0);

        double total = amazon.calculate();


        assertEquals(15.5, total, 0.001);
        verify(mockRule1, times(1)).priceToAggregate(dummyItems);
        verify(mockRule2, times(1)).priceToAggregate(dummyItems);
    }
}