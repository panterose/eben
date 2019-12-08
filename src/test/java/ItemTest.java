import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Double.doubleToLongBits;
import static org.junit.Assert.*;


public class ItemTest {
    @Test(expected = IllegalArgumentException.class)
    public void itemWithZeroPrice() {
        new Item(0, "d", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void itemWithNegativePrice() {
        new Item(0, "d", -1);
    }

    @Test
    public void testLongToDoubleArithmetic() {
        assertTrue("Test 2 integers", doubleToLongBits(1.0) < doubleToLongBits( 2.0));
        assertTrue("Test with decimal", doubleToLongBits(1.0) < doubleToLongBits( 1.1));
        assertTrue("Test with power", doubleToLongBits(1.1) < doubleToLongBits( 10.1));
        assertTrue("Test with close", doubleToLongBits(1.0000000000001) < doubleToLongBits( 1.00000000000011));
    }


    @Test
    public void sequentialBidding() {
        Item item = new Item(0, "item1", 10.0);
        Bid low = new Bid("user", 0, 8.0);
        Bid asking = new Bid("user", 0, 10.0);

        // initial bidding
        assertFalse("Can't bid lower than asking price", item.tryToBid(low));
        assertTrue("Should accept asking price", item.tryToBid(asking));
        assertEquals("Should see accepting price", 10.0, item.getWiningBid().getPrice(), 0.01);
        assertEquals("Should have 1 bid", 1, item.getBids().size(), 0);

        // same bid again
        assertFalse("Same bid should reject", item.tryToBid(asking));
        assertEquals("Still have 1 bid", 1, item.getBids().size());

        // new bid
        Bid higher = new Bid("user", 0, 11.0);
        assertTrue("Should accept new bid", item.tryToBid(higher));
        assertEquals("Should see higher price", 11.0, item.getWiningBid().getPrice(), 0.01);
        assertEquals("Still have 2 bids", 2, item.getBids().size());
    }

    @Test
    public void parallelBidding() {
        Item item = new Item(0, "item1", 10.0);
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> tests = new ArrayList<>();

        // many same bid
        for (int num = 0 ; num < 1000; num++) {
            tests.add(es.submit(() -> item.tryToBid(new Bid("u", 0, 10.0))));
        }
        long acceptedBids = tests.stream().map(this::read).filter(b -> b).count();
        assertEquals("only one bid accepted", 1, acceptedBids);
        assertEquals("Still have 1 bid", 1, item.getBids().size());

        // many bid going up
        for (int num = 0 ; num < 1000; num++) {
            int toAdd = (int)(num / 100);
            tests.add(es.submit(() -> item.tryToBid(new Bid("u", 0, 10.0 + toAdd))));
        }
        acceptedBids = tests.stream().map(this::read).filter(b -> b).count();
        assertEquals("only one bid accepted", 10, acceptedBids);
        assertEquals("Now have 10 bid", 10, item.getBids().size());
    }

    private boolean read(Future<Boolean> f) {
        try {
            return f.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}