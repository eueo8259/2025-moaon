package moaon.backend.project.domain.repository;

import java.util.Optional;
import moaon.backend.project.domain.Project;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(Long id);

    boolean existsById(Long id);

    void increaseViewCountById(Long id);
}
