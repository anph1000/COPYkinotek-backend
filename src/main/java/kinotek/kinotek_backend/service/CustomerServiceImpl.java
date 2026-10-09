package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.user.Customer;
import kinotek.kinotek_backend.repository.user.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        validateEmailNotNull(email);
        email = normalizeEmail(email);
        validateEmailRegex(email);

        return customerRepository.findCustomerByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        validateEmailNotNull(email);
        email = normalizeEmail(email);
        validateEmailRegex(email);

        return customerRepository.existsByEmail(email);
    }

    @Override
    public Customer saveGuestByEmail(String email) {
        validateEmailNotNull(email);
        email = normalizeEmail(email);
        validateEmailRegex(email);

        Customer guest = new Customer();
        guest.setEmail(email);
        return customerRepository.save(guest);
    }

    private void validateEmailNotNull(String email) {
        //VALIDATE EMAIL IS NOT NULL
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email skal angives");
        }

    }

    private String normalizeEmail(String email) {
        //NORMALIZE
        String normalized = email.trim().toLowerCase();
        return normalized;
    }

    private void validateEmailRegex(String email) {
        //VALIDATE FORMAT
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email har ugyldigt format");
        }
    }
}
