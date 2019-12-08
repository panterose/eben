import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;
import static jdk.nashorn.internal.objects.NativeMath.max;

public class Item implements Comparable<Item> {
    private final long itemId;
    private final String description;
    private final long startingPrice;

    private AtomicLong winingBidInLong = new AtomicLong(doubleToLongBits(0.0));
    private Set<Bid> bids = new ConcurrentSkipListSet<>((a,b) -> a.getPrice() > b.getPrice() ? 1 : -1);

    public Item(long itemId, String description, double startingPrice) {
        if (startingPrice <= 0) {
            throw new IllegalArgumentException("Can't have a 0 or negative starting price");
        }
        this.itemId = itemId;
        this.description = description;
        this.startingPrice = doubleToLongBits(startingPrice);
    }

    public long getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return longBitsToDouble(startingPrice);
    }

    public Bid getWiningBid() {
        double price = longBitsToDouble(winingBidInLong.get());
        if (price == 0.0) {
            return null;
        } else {
            return bids.stream()
                    .filter(b -> b.getPrice() == price)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Shouldn't happened has there should be a bid at that price"));
        }
    }

    public List<Bid> getBids() {
        // this is only for reading: so we're copying in order
        return new ArrayList<>(bids);
    }

    public boolean tryToBid(Bid bid) {
        long bidPrice = doubleToLongBits(bid.getPrice());
        long currentPrice;

        while ((currentPrice = winingBidInLong.get()) < bidPrice && bidPrice >= startingPrice) {
            if (winingBidInLong.compareAndSet(currentPrice, bidPrice)) {
                bids.add(bid);
                return true;
            } else {
                // changed in between -> might need to spin or engineer a way out
            }
        }

        return false;
    }

    @Override
    public int compareTo(Item item) {
        return (int)(item.getItemId() - this.getItemId());
    }
}
