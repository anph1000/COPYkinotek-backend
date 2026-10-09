package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.cinema.Invoice;
import kinotek.kinotek_backend.model.user.Customer;
import kinotek.kinotek_backend.repository.cinema.InvoiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl (InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice createInvoice(Customer customer) {
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setPurchaseTime(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice findInvoiceById(int id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre med id " + id + " eksisterer ikke"));
    }

}
