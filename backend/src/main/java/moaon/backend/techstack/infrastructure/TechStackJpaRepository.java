package moaon.backend.techstack.infrastructure;

import java.util.Optional;
import moaon.backend.techstack.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

interface TechStackJpaRepository extends JpaRepository<TechStack, Long> {

    Optional<TechStack> findByName(String name);
}
