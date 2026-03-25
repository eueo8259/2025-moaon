package moaon.backend.project.infrastructure;

import java.util.Optional;
import moaon.backend.project.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
