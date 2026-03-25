package moaon.backend.project.application.repository;

import java.util.List;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.project.domain.Projects;
import moaon.backend.project.infrastructure.sort.ProjectSortSpec;

public interface ProjectQueryRepository {

    Projects findWithSearchConditions(
            ProjectQueryCondition projectQueryCondition,
            ProjectSortSpec sortSpec,
            CursorToken cursorToken
    );

    Project findProjectWithMemberJoin(Long id);

    List<ProjectCategory> findProjectCategoriesByProjectId(Long id);

    List<ProjectTechStack> findProjectTechStacksByProjectId(Long id);
}
