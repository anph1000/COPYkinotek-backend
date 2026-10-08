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
    public Customer saveCustomerByEmail(String email) {

        //VALIDATE EMAIL IS NOT NULL
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email skal angives");
        }

        //NORMALIZE
        String normalized = email.trim().toLowerCase();

        //VALIDATE FORMAT
        if (!normalized.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email har ugyldigt format");
        }

        //CHECK IF EMAIL EXISTS
        if (customerRepository.existsByEmail(normalized)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Der findes allerede en kunde med denne email");
        }

        Customer guest = new Customer();
        guest.setEmail(normalized);
        return customerRepository.save(guest);
    }
}
