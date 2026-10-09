package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.cinema.Invoice;
import kinotek.kinotek_backend.model.user.Customer;

public interface InvoiceService {

    public Invoice createInvoice(Customer customer);
    public Invoice findInvoiceById(int id);
}
