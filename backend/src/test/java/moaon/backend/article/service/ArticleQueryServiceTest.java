package moaon.backend.article.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import moaon.backend.article.application.ArticleQueryService;
import moaon.backend.article.repository.ArticleRepositoryFacade;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.domain.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class ArticleQueryServiceTest {

    private final ArticleRepositoryFacade articleRepositoryFacade = Mockito.mock(ArticleRepositoryFacade.class);
    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);

    private final ArticleQueryService articleQueryService = new ArticleQueryService(
            articleRepositoryFacade,
            projectRepository
    );

    @DisplayName("존재하지 않는 프로젝트 ID로 검색하면 예외가 발생한다.")
    @Test
    void getByProjectId_notFound() {
        when(projectRepository.findById(123L)).thenReturn(Optional.empty());
        ProjectArticleQueryCondition condition = mock(ProjectArticleQueryCondition.class);

        assertThatThrownBy(() -> articleQueryService.getByProjectId(123L, condition))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_NOT_FOUND);
    }
}
