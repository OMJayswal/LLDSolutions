import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineItem {
    private String itemId;
    private Double qty;
    private Double cost;
    private Double tax;

    public LineItem(String itemId, Double qty) {
        this.itemId = itemId;
        this.qty = qty;
        this.cost = 0.0;
        this.tax = 0.0;
    }
}
