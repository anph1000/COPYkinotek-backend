package kinotek.kinotek_backend.controller;

import kinotek.kinotek_backend.model.user.Employee;
import kinotek.kinotek_backend.repository.user.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


// OBS - crossorigin her = forbigå CORS violation når vi tilgår backend via nginx osv
@RestController
@RequestMapping("/api/employee")
@CrossOrigin
public class EmployeeRestController {


    // simple stupid:
    // records = DTO som bor i controller for formålet af opgaven.
    // ikke konventionelt = hurtigt og nemt for formålet
    record LoginRequest(String name, String password) {}
    record LoginResponse(String name, String role) {}

    private final EmployeeRepository employeeRepository;

    public EmployeeRestController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    // vi sammenligner vores response med vores DB indhold
    // 0 security, 0 sessions
    // UI gate, ikke reel login
    // proof of concept = ikke opgavekrav.
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Employee employee = employeeRepository.findByEmployeeNameAndPassword(request.name(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong name or password"));
        return new LoginResponse(employee.getEmployeeName(), employee.getRole().getRoleName());
    }

}
