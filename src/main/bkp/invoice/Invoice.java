import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Invoice {

    private  List<LineItem> lineItems = new ArrayList<>();
    private Double totalPrice;
    private Double totalTax;
    public Invoice(List<LineItem> items){
        this.lineItems  = items;
    }
}
