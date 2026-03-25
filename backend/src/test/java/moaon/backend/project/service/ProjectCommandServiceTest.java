package moaon.backend.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import moaon.backend.fixture.ProjectFixtureBuilder;
import moaon.backend.project.application.ProjectCommandService;
import moaon.backend.project.application.dto.ProjectDetailResponse;
import moaon.backend.project.application.repository.ProjectQueryRepository;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.category.domain.repository.CategoryRepository;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.techstack.domain.repository.TechStackRepository;
import moaon.backend.member.application.MemberService;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectCommandServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectQueryRepository projectQueryRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private TechStackRepository techStackRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProjectCommandService projectCommandService;

    @DisplayName("조회 상세 응답은 조회 전용 리포지터리를 통해 조합한다.")
    @Test
    void increaseViewsCount() {
        Project project = new ProjectFixtureBuilder().id(1L).build();
        ReflectionTestUtils.setField(project.getAuthor(), "id", 10L);
        List<ProjectTechStack> stacks = List.of();
        List<ProjectCategory> categories = List.of();

        when(projectQueryRepository.findProjectWithMemberJoin(1L)).thenReturn(project);
        when(projectQueryRepository.findProjectTechStacksByProjectId(1L)).thenReturn(stacks);
        when(projectQueryRepository.findProjectCategoriesByProjectId(1L)).thenReturn(categories);

        ProjectDetailResponse actual = projectCommandService.increaseViewsCount(1L);

        verify(projectRepository).increaseViewCountById(1L);
        verify(projectQueryRepository).findProjectWithMemberJoin(1L);
        assertThat(actual.id()).isEqualTo(1L);
        assertThat(actual.authorId()).isEqualTo(10L);
        assertThat(actual.techStacks()).isEmpty();
        assertThat(actual.categories()).isEmpty();
    }
}
