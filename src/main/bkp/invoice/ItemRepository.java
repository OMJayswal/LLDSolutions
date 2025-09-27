import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemRepository {
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    Map<String,Item> items = new HashMap<>();
    public void addItem(Item item){
        item.setItemId(String.valueOf(atomicInteger.addAndGet(1)));
        items.put(item.getItemId(),item);
    }
    public Item getItem(String itemId){
        return items.getOrDefault(itemId,null);
    }
}
