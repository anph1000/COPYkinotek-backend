package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.user.Customer;

public interface CustomerService {
    Customer saveGuestByEmail(String email);
    public boolean existsByEmail(String email);
    public Customer findCustomerByEmail(String email);

}
