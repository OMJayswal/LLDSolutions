/*
 Item
 -id
 -name
 -price
 -tax percentage

-LineItem
-id
-item_id
-Qty
-price
-tax

 Invoice
 -List<LineItem> lineItems
 -TotalTax
 -TotalAmount
 */

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


public class Main {

    public static void main(String[] args) throws Exception {
        ItemRepository itemRepository = new ItemRepository();
        itemRepository.addItem(new Item("Book",50.0,10.0,Unit.PIECE));
        itemRepository.addItem(new Item("Oil",70.0,12.0,Unit.LITRE));
        itemRepository.addItem(new Item("Rice",90.0,8.0,Unit.KG));

        List<LineItem> lineItems = new ArrayList<>();
        lineItems.add(new LineItem("1",5.0));
        lineItems.add(new LineItem("2",1.5));
        lineItems.add(new LineItem("3",2.5));
        Invoice invoice = new Invoice(lineItems);
        InvoiceCalculator invoiceCalculator = new PercentageInvoiceCalculator(itemRepository);

        invoiceCalculator.calculateInvoice(invoice);

        System.out.println("Total Tax: "+invoice.getTotalTax());
        System.out.println("Total Price: "+invoice.getTotalPrice());
    }
}
