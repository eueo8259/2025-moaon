package moaon.backend.project.domain.repository;

import java.util.List;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.project.domain.Projects;

public interface CustomizedProjectRepository {

    Projects findWithSearchConditions(ProjectQueryCondition projectQueryCondition);

    Project findProjectWithMemberJoin(Long id);

    void increaseViewCountById(Long id);

    List<ProjectCategory> findProjectCategoriesByProjectId(Long id);

    List<ProjectTechStack> findProjectTechStacksByProjectId(Long id);
}
