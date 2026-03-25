package moaon.backend.techstack.domain.repository;

import java.util.List;
import java.util.Optional;
import moaon.backend.techstack.domain.TechStack;

public interface TechStackRepository {

    Optional<TechStack> findByName(String name);

    TechStack save(TechStack techStack);

    List<TechStack> saveAll(List<TechStack> techStacks);
}
