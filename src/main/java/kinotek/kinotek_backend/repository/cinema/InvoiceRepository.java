package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
