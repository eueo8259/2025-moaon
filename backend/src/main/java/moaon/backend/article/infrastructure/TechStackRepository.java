package moaon.backend.article.infrastructure;

import java.util.Optional;
import moaon.backend.shared.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("articleTechStackRepository")
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    Optional<TechStack> findByName(String name);
}
