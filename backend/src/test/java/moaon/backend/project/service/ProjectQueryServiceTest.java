package moaon.backend.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import moaon.backend.fixture.ProjectFixtureBuilder;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.application.ProjectQueryService;
import moaon.backend.project.application.dto.PagedProjectResponse;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.application.dto.ProjectSummaryResponse;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectSortType;
import moaon.backend.project.domain.Projects;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.project.infrastructure.sort.ProjectSortSpec;
import moaon.backend.project.infrastructure.sort.ProjectSortSpecFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectQueryServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectSortSpecFactory projectSortSpecFactory;

    @Mock
    private ProjectSortSpec projectSortSpec;

    @InjectMocks
    private ProjectQueryService projectQueryService;

    @Disabled
    @DisplayName("ID에 해당하는 프로젝트가 존재하지 않으면 예외가 발생한다.")
    @Test
    void getProjectById() {
        assertThatThrownBy(() -> projectQueryService.getById(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROJECT_NOT_FOUND.getMessage());
    }

    @DisplayName("다음 페이지가 존재하면 nextCursor를 포함해 프로젝트를 반환한다.")
    @Test
    void getPagedProjectsWhenHasNext() {
        Project project1 = new ProjectFixtureBuilder()
                .id(1L)
                .build();
        Project project2 = new ProjectFixtureBuilder()
                .id(2L)
                .build();
        Project project3 = new ProjectFixtureBuilder()
                .id(3L)
                .build();
        List<Project> projects = List.of(project1, project2, project3);

        ProjectQueryCondition projectQueryCondition = new ProjectQueryCondition(
                null,
                null,
                null,
                ProjectSortType.CREATED_AT,
                2,
                null
        );

        Mockito.when(projectSortSpecFactory.get(ProjectSortType.CREATED_AT)).thenReturn(projectSortSpec);
        Mockito.when(projectRepository.findWithSearchConditions(eq(projectQueryCondition), eq(projectSortSpec), eq(null)))
                .thenReturn(new Projects(projects, 5, projectQueryCondition.limit()));
        Mockito.when(projectSortSpec.createNextCursor(project2)).thenReturn("next-cursor");

        ProjectSummaryResponse projectSummaryResponse1 = ProjectSummaryResponse.from(project1);
        ProjectSummaryResponse projectSummaryResponse2 = ProjectSummaryResponse.from(project2);

        PagedProjectResponse actual = projectQueryService.getPagedProjects(projectQueryCondition);

        assertAll(
                () -> assertThat(actual.contents()).containsExactly(projectSummaryResponse1, projectSummaryResponse2),
                () -> assertThat(actual.hasNext()).isTrue(),
                () -> assertThat(actual.totalCount()).isEqualTo(5L),
                () -> assertThat(actual.nextCursor()).isEqualTo("next-cursor")
        );
    }

    @DisplayName("다음 페이지가 없으면 nextCursor 없이 반환한다.")
    @Test
    void getPagedProjectsWhenHasNoNext() {
        Project project1 = new ProjectFixtureBuilder()
                .id(1L)
                .build();
        Project project2 = new ProjectFixtureBuilder()
                .id(2L)
                .build();
        Project project3 = new ProjectFixtureBuilder()
                .id(3L)
                .build();

        List<Project> projects = List.of(project1, project2, project3);

        ProjectQueryCondition projectQueryCondition = new ProjectQueryCondition(
                null,
                null,
                null,
                ProjectSortType.CREATED_AT,
                3,
                null
        );

        Mockito.when(projectSortSpecFactory.get(ProjectSortType.CREATED_AT)).thenReturn(projectSortSpec);
        Mockito.when(projectRepository.findWithSearchConditions(eq(projectQueryCondition), eq(projectSortSpec), eq(null)))
                .thenReturn(new Projects(projects, 3, projectQueryCondition.limit()));

        ProjectSummaryResponse projectSummaryResponse1 = ProjectSummaryResponse.from(project1);
        ProjectSummaryResponse projectSummaryResponse2 = ProjectSummaryResponse.from(project2);
        ProjectSummaryResponse projectSummaryResponse3 = ProjectSummaryResponse.from(project3);

        PagedProjectResponse actual = projectQueryService.getPagedProjects(projectQueryCondition);

        assertAll(
                () -> assertThat(actual.contents()).containsExactly(projectSummaryResponse1, projectSummaryResponse2,
                        projectSummaryResponse3),
                () -> assertThat(actual.hasNext()).isFalse(),
                () -> assertThat(actual.nextCursor()).isNull()
        );
    }
}
