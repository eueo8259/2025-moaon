package moaon.backend.article.repository;

import static moaon.backend.article.domain.ArticleSortType.CREATED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.infrastructure.sort.CreatedAtArticleSortSpec;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.article.repository.es.ArticleDocumentRepository;
import moaon.backend.fixture.ArticleFixtureBuilder;
import moaon.backend.fixture.ArticleQueryConditionBuilder;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

class ArticleRepositoryFacadeTest {

    private final ArticleDocumentRepository articleDocumentRepository = mock(ArticleDocumentRepository.class);
    private final ArticleDBRepository articleDBRepository = mock(ArticleDBRepository.class);

    private final ArticleRepositoryFacade articleRepositoryFacade = new ArticleRepositoryFacade(
            articleDBRepository,
            articleDocumentRepository
    );

    private final ArticleQueryCondition queryCondition = new ArticleQueryConditionBuilder().sortBy(CREATED_AT).build();

    @DisplayName("ElasticSearch Repository를 먼저 조회한다")
    @Test
    void getPagedArticlesElasticSearchFirst() {
        SearchHits<ArticleDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of());
        when(articleDocumentRepository.search(queryCondition)).thenReturn(searchHits);
        when(articleDBRepository.findAllById(List.of())).thenReturn(List.of());

        ArticleSearchResult result = articleRepositoryFacade.search(queryCondition, new CreatedAtArticleSortSpec(), null);

        assertThat(result.getArticles()).isEmpty();
        verify(articleDocumentRepository).search(queryCondition);
    }

    @DisplayName("프로젝트 조회는 project id 기반으로 ES를 호출한다")
    @Test
    void getByProjectIdSuccess() {
        long projectId = 1L;
        ProjectArticleQueryCondition pac = new ProjectArticleQueryCondition(
                moaon.backend.article.domain.Sector.BE,
                new SearchKeyword("검색어")
        );
        Article article = new ArticleFixtureBuilder().id(1L).build();

        @SuppressWarnings("unchecked")
        SearchHit<ArticleDocument> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(new ArticleDocument(article));

        SearchHits<ArticleDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(hit));
        when(articleDocumentRepository.searchInProject(eq(projectId), eq(pac.toArticleCondition()))).thenReturn(searchHits);
        when(articleDBRepository.findAllById(List.of(1L))).thenReturn(List.of(article));

        ArticleSearchResult result = articleRepositoryFacade.searchInProject(projectId, pac, new CreatedAtArticleSortSpec(), null);

        assertThat(result.getArticles()).extracting(Article::getId).containsExactly(1L);
        verify(articleDocumentRepository).searchInProject(eq(projectId), eq(pac.toArticleCondition()));
    }
}
