package kinotek.kinotek_backend.repository.user;

import kinotek.kinotek_backend.model.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByEmployeeName(String employeeName);

    Optional<Employee> findByEmployeeName(String employeeName);
    Optional<Employee> findByEmployeeNameAndPassword(String employeeName, String password);


}
