import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Item {
    private String itemId;
    private String name;
    private Double unitPrice;
    private Double taxPercentage;
    private Unit unit;

    public Item(String name, Double unitPrice, Double taxPercentage, Unit unit) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.taxPercentage = taxPercentage;
        this.unit = unit;
    }
}
