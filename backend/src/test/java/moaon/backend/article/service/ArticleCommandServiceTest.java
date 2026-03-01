package moaon.backend.article.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import moaon.backend.article.application.ArticleCommandService;
import moaon.backend.article.application.dto.ArticleCreateRequest;
import moaon.backend.article.domain.Article;
import moaon.backend.article.event.ArticleEventPublisher;
import moaon.backend.article.repository.ArticleRepositoryFacade;
import moaon.backend.article.repository.db.ArticleContentRepository;
import moaon.backend.fixture.Fixture;
import moaon.backend.fixture.ProjectFixtureBuilder;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.global.parser.URLParser;
import moaon.backend.member.domain.Member;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.techStack.repository.TechStackRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArticleCommandServiceTest {

    private final ArticleRepositoryFacade articleRepositoryFacade = Mockito.mock(ArticleRepositoryFacade.class);
    private final ArticleContentRepository articleContentRepository = Mockito.mock(ArticleContentRepository.class);
    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private final TechStackRepository techStackRepository = Mockito.mock(TechStackRepository.class);
    private final ArticleEventPublisher articleEventPublisher = Mockito.mock(ArticleEventPublisher.class);

    private final ArticleCommandService articleCommandService = new ArticleCommandService(
            articleRepositoryFacade,
            articleContentRepository,
            projectRepository,
            techStackRepository,
            articleEventPublisher
    );

    @DisplayName("ArticleCreateRequest의 갯수만큼 저장한다.")
    @Test
    void save_createsArticleAndDocument() {
        // given
        Member author = new Member(1L, "socialId", "email", "name", 0);
        when(projectRepository.findById(1L)).thenReturn(
                Optional.of(new ProjectFixtureBuilder().author(author).build())
        );

        List<ArticleCreateRequest> requests = List.of(
                articleCreateRequestWithProjectId(1L),
                articleCreateRequestWithProjectId(1L),
                articleCreateRequestWithProjectId(1L)
        );

        // when
        articleCommandService.save(requests, author);

        // then
        verify(articleRepositoryFacade, times(3)).save(any(Article.class));
    }

    @DisplayName("아티클 저장 시 로그인한 멤버가 프로젝트의 작성자가 아니면 예외 발생")
    @Test
    void save_unauthorizedMember() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(new ProjectFixtureBuilder()
                .author(new Member(1L, "socialId", "email", "name", 0)).build())
        );

        // when & then
        assertThatThrownBy(
                () -> articleCommandService.save(List.of(articleCreateRequestWithProjectId(1L)), Fixture.anyMember()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED_MEMBER);
    }

    @DisplayName("클릭 수 증가를 위임한다.")
    @Test
    void increaseClicksCount() {
        // when
        articleCommandService.increaseClicksCount(123L);

        // then
        verify(articleRepositoryFacade).incrementClickCount(123L);
    }

    private ArticleCreateRequest articleCreateRequestWithProjectId(long projectId) {
        return new ArticleCreateRequest(
                projectId,
                "title",
                "summary",
                List.of(),
                URLParser.parse("http://example.com"),
                "non_tech",
                List.of("design")
        );
    }
}