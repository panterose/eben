public class Bid {
    private String username;
    private long itemId;
    private double price;

    public Bid(String username, long itemId, double price) {
        this.username = username;
        this.itemId = itemId;
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public long getItemId() {
        return itemId;
    }

    public double getPrice() {
        return price;
    }
}
