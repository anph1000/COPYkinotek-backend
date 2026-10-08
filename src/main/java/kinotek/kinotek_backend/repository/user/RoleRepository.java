package kinotek.kinotek_backend.repository.user;

import kinotek.kinotek_backend.model.user.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<EmployeeRole, Integer> {
}
