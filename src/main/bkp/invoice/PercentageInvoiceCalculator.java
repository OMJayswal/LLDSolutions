public class PercentageInvoiceCalculator implements InvoiceCalculator{

    ItemRepository itemRepository;

    public PercentageInvoiceCalculator(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    @Override
    public void calculateInvoice(Invoice invoice) throws ItemNotFoundException{
        for(LineItem lineItem: invoice.getLineItems()){
            Item item = itemRepository.getItem(lineItem.getItemId());
            if(item == null){
                throw new ItemNotFoundException("Item Not Found Exception");
            }
            lineItem.setCost(item.getUnitPrice()*lineItem.getQty());
            lineItem.setTax(lineItem.getCost()*item.getTaxPercentage()/100);
        }
        Double totalCost = invoice.getLineItems().stream().mapToDouble(LineItem::getCost).sum();
        Double totalTax = invoice.getLineItems().stream().mapToDouble(LineItem::getTax).sum();
        invoice.setTotalPrice(totalCost+totalTax);
        invoice.setTotalTax(totalTax);
    }

}
