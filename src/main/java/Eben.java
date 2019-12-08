import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class Eben {
    private Set<Item> items = new ConcurrentSkipListSet<>();
    private Set<User> users = new ConcurrentSkipListSet<>();

    public void register(User user) {
        users.add(user);
    }

    public void enlist(Item item) {
        items.add(item);
    }

    public boolean bid(long itemId, String username, double price) {
        Item item = findItem(itemId)
                .orElseThrow(() -> new RuntimeException("Can't find any Item with Id " + itemId));

        User user = findUser(username)
                .orElseThrow(() -> new RuntimeException("No user registerd with " + username ));

        return item.tryToBid(new Bid(username, itemId, price));
    }

    public Bid getWiningBid(long itemId) {
        return findItem(itemId)
                .map(i -> i.getWiningBid())
                .orElse(null);
    }

    public Set<Item> findAllItemBidsByUser(String username) {
        User user = findUser(username)
                .orElseThrow(() -> new RuntimeException("No user registerd with " + username ));

        return items.stream()
                .filter(i -> i.getBids().stream().anyMatch(b -> b.getUsername().equals(username)))
                .collect(Collectors.toSet());

    }

    public List<Bid> getAllBids(long itemId) {
        return findItem(itemId)
                .map(i -> i.getBids())
                .orElse(null);
    }

    public Set<Item> getItems() {
        return items;
    }

    public Set<User> getUsers() {
        return users;
    }

    protected Optional<Item> findItem(long itemId) {
        return items.stream()
                .filter(t -> t.getItemId() == itemId)
                .findFirst();
    }

    protected Optional<User> findUser(String username) {
        return users.stream()
                .filter(t -> t.getName() == username)
                .findFirst();
    }
}
