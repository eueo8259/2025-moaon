package moaon.backend.project.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.project.application.dto.PagedProjectResponse;
import moaon.backend.project.application.dto.ProjectDetailResponse;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.project.domain.Projects;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.project.infrastructure.sort.ProjectSortSpec;
import moaon.backend.project.infrastructure.sort.ProjectSortSpecFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectQueryService {

    private final ProjectRepository projectRepository;
    private final ProjectSortSpecFactory projectSortSpecFactory;

    public ProjectDetailResponse getById(Long id) {
        Project project = projectRepository.findProjectWithMemberJoin(id);
        List<ProjectTechStack> stacks = projectRepository.findProjectTechStacksByProjectId(id);
        List<ProjectCategory> categories = projectRepository.findProjectCategoriesByProjectId(id);

        return ProjectDetailResponse.from(project, stacks, categories);
    }

    public PagedProjectResponse getPagedProjects(ProjectQueryCondition projectQueryCondition) {
        CursorToken cursorToken = CursorCodec.decode(projectQueryCondition.cursor());
        ProjectSortSpec sortSpec = projectSortSpecFactory.get(projectQueryCondition.projectSortType());
        Projects projects = projectRepository.findWithSearchConditions(projectQueryCondition, sortSpec, cursorToken);

        List<Project> projectsToReturn = projects.getProjectsToReturn();
        long count = projects.getCount();
        boolean hasNext = projects.hasNext();
        String nextCursor = projects.getNextCursor(sortSpec);

        return PagedProjectResponse.from(projectsToReturn, count, hasNext, nextCursor);
    }
}
