import org.junit.Test;

import static org.junit.Assert.*;

public class EbenTest {
    private final User ben = new User("ben");
    private final User ali = new User("ali");

    private final Item table = new Item(1, "table", 50.0);
    private final Item chair = new Item(2, "chair", 10.0);

    @Test
    public void register() {
        Eben ebay = new Eben();
        ebay.register(ben);
        ebay.register(ali);
        assertEquals("Should contain 2 users", 2, ebay.getUsers().size());
        assertTrue("Should contain ben", ebay.getUsers().contains(ben));
        assertTrue("Should contain ali", ebay.getUsers().contains(ali));
    }

    @Test
    public void enlist() {
        Eben ebay = new Eben();
        ebay.enlist(table);
        ebay.enlist(chair);
        assertEquals("Should contain 2 items", 2, ebay.getItems().size());
        assertTrue("Should contain table", ebay.getItems().contains(table));
        assertTrue("Should contain chair", ebay.getItems().contains(chair));
    }

    @Test
    public void bid() {
        // GIVEN
        Eben ebay = new Eben();
        ebay.register(ben);
        ebay.enlist(table);

        // WHEN
        boolean success = ebay.bid(table.getItemId(), ben.getName(), 51.0);

        // THEN
        assertTrue("bid accepted", success);
    }

    @Test(expected = RuntimeException.class)
    public void bidNotEnlisted() {
        Eben ebay = new Eben();
        ebay.bid(0, ben.getName(), 51.0);
    }

    @Test(expected = RuntimeException.class)
    public void bidNotRegistered() {
        Eben ebay = new Eben();
        ebay.bid(table.getItemId(), "blabla", 51.0);
    }

    @Test
    public void getWiningBid() {
        // GIVEN
        Eben ebay = new Eben();
        ebay.register(ben);
        ebay.enlist(table);
        ebay.bid(table.getItemId(), ben.getName(), 51.0);

        // WHEN
        Bid winingBid = ebay.getWiningBid(table.getItemId());

        // THEN
        assertEquals(table.getItemId(), winingBid.getItemId());
        assertEquals(ben.getName(), winingBid.getUsername());
        assertEquals(51.0, winingBid.getPrice(), 0.0001);
    }

    @Test
    public void findAllItemBidsByUser() {
    }

    @Test
    public void getAllBids() {
    }
}