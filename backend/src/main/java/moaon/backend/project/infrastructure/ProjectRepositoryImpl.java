package moaon.backend.project.infrastructure;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.global.query.FilteredIds;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.application.repository.ProjectQueryRepository;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.project.domain.Projects;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.project.infrastructure.dao.ProjectDao;
import moaon.backend.project.infrastructure.sort.ProjectSortSpec;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository, ProjectQueryRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectDao projectDao;

    @Override
    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    @Override
    public java.util.Optional<Project> findById(Long id) {
        return projectJpaRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return projectJpaRepository.existsById(id);
    }

    @Override
    public void increaseViewCountById(Long id) {
        projectDao.increaseViewCountById(id);
    }

    @Override
    public Projects findWithSearchConditions(
            ProjectQueryCondition condition,
            ProjectSortSpec sortSpec,
            CursorToken cursorToken
    ) {
        int limit = condition.limit();
        List<String> techStackNames = condition.techStackNames();
        SearchKeyword search = condition.search();
        List<String> categoryNames = condition.categoryNames();

        FilteredIds filteredIds = FilteredIds.init();
        filteredIds = applyTechStacks(filteredIds, techStackNames);
        filteredIds = applyCategories(filteredIds, categoryNames);
        filteredIds = applySearch(filteredIds, search);

        if (filteredIds.hasEmptyResult()) {
            return Projects.empty(limit);
        }

        List<Project> projects = projectDao.findProjects(condition, filteredIds.getIds(), sortSpec, cursorToken);
        return new Projects(projects, calculateTotalCount(filteredIds), limit);
    }

    @Override
    public List<ProjectCategory> findProjectCategoriesByProjectId(Long id) {
        return projectDao.findProjectCategoriesByProjectId(id);
    }

    @Override
    public List<ProjectTechStack> findProjectTechStacksByProjectId(Long id) {
        return projectDao.findProjectTechStacksByProjectId(id);
    }

    @Override
    public Project findProjectWithMemberJoin(Long id) {
        return projectDao.findProjectById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private FilteredIds applyTechStacks(FilteredIds filteredIds, List<String> techStack) {
        if (filteredIds.hasEmptyResult() || CollectionUtils.isEmpty(techStack)) {
            return filteredIds;
        }

        java.util.Set<Long> projectIdsByTechStacks = projectDao.findProjectIdsByTechStacks(filteredIds, techStack);
        return filteredIds.addFilterResult(projectIdsByTechStacks);
    }

    private FilteredIds applyCategories(FilteredIds filteredIds, List<String> categories) {
        if (filteredIds.hasEmptyResult() || CollectionUtils.isEmpty(categories)) {
            return filteredIds;
        }

        java.util.Set<Long> projectIdsByCategories = projectDao.findProjectIdsByCategories(filteredIds, categories);
        return filteredIds.addFilterResult(projectIdsByCategories);
    }

    private FilteredIds applySearch(FilteredIds filteredIds, SearchKeyword keyword) {
        if (filteredIds.hasEmptyResult() || keyword == null || !keyword.hasValue()) {
            return filteredIds;
        }

        java.util.Set<Long> projectIdsBySearchKeyword = projectDao.findProjectIdsBySearchKeyword(filteredIds, keyword);
        return filteredIds.addFilterResult(projectIdsBySearchKeyword);
    }

    private long calculateTotalCount(FilteredIds filteredIds) {
        if (filteredIds.isEmpty()) {
            return projectDao.count();
        }

        return filteredIds.size();
    }
}
