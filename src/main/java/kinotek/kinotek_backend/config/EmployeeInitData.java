package kinotek.kinotek_backend.config;

import kinotek.kinotek_backend.model.user.Employee;
import kinotek.kinotek_backend.model.user.EmployeeRole;
import kinotek.kinotek_backend.repository.user.EmployeeRepository;
import kinotek.kinotek_backend.repository.user.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class EmployeeInitData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeInitData(RoleRepository roleRepository, EmployeeRepository employeeRepository) {
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() > 0) return;
        EmployeeRole admin = createRole("Admin");
        createRole("Operator");
        createRole("SalesAssistant");
        createEmployee("admin", "admin", admin);
    }

    private EmployeeRole createRole(String roleName) {
        EmployeeRole role = new EmployeeRole();
        role.setRoleName(roleName);
        return roleRepository.save(role);
    }

    private void createEmployee(String name, String password, EmployeeRole role) {
        Employee employee = new Employee();
        employee.setEmployeeName(name);
        employee.setPassword(password);
        employee.setRole(role);
        employeeRepository.save(employee);
    }
}
