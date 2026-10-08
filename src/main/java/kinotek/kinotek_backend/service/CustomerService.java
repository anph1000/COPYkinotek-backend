package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.user.Customer;

public interface CustomerService {
    Customer saveCustomerByEmail(String email);

}
