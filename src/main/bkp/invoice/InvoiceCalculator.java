import java.util.List;

public interface InvoiceCalculator {
    public void calculateInvoice(Invoice invoice) throws ItemNotFoundException;
}
