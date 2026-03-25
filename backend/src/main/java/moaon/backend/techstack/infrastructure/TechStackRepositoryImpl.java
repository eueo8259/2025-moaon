package moaon.backend.techstack.infrastructure;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import moaon.backend.techstack.domain.TechStack;
import moaon.backend.techstack.domain.repository.TechStackRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechStackRepository {

    private final TechStackJpaRepository techStackJpaRepository;

    @Override
    public Optional<TechStack> findByName(String name) {
        return techStackJpaRepository.findByName(name);
    }

    @Override
    public TechStack save(TechStack techStack) {
        return techStackJpaRepository.save(techStack);
    }

    @Override
    public List<TechStack> saveAll(List<TechStack> techStacks) {
        return techStackJpaRepository.saveAll(techStacks);
    }
}
