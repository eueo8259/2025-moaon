package moaon.backend.project.infrastructure;

import moaon.backend.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProjectJpaRepository extends JpaRepository<Project, Long> {
}
