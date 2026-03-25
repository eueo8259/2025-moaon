package moaon.backend.article.repository.es;

import static moaon.backend.article.domain.ArticleSortType.CREATED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.fixture.ArticleFixtureBuilder;
import moaon.backend.fixture.ArticleQueryConditionBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHits;

class ArticleDocumentRepositoryTest {

    private final ArticleDocumentOperations documentOperations = Mockito.mock(ArticleDocumentOperations.class);
    private final ArticleDBRepository articleDBRepository = Mockito.mock(ArticleDBRepository.class);
    private final ArticleDocumentRepository repository = new ArticleDocumentRepository(documentOperations, articleDBRepository);

    private final ArticleQueryCondition queryCondition = new ArticleQueryConditionBuilder().sortBy(CREATED_AT).build();

    @DisplayName("검색 요청을 DocumentOperations에 위임하고 SearchHits를 반환한다")
    @Test
    void search() {
        SearchHits<ArticleDocument> mockHits = mock(SearchHits.class);
        when(documentOperations.search(queryCondition)).thenReturn(mockHits);

        SearchHits<ArticleDocument> result = repository.search(queryCondition);

        assertThat(result).isEqualTo(mockHits);
        verify(documentOperations).search(queryCondition);
    }

    @DisplayName("프로젝트 검색은 프로젝트 ID로 article ID 목록을 조회해서 ES에 전달한다")
    @Test
    void searchInProject() {
        SearchHits<ArticleDocument> mockHits = mock(SearchHits.class);
        when(articleDBRepository.findIdsByProjectId(99L)).thenReturn(List.of(1L, 2L));
        when(documentOperations.searchInIds(List.of(1L, 2L), queryCondition)).thenReturn(mockHits);

        SearchHits<ArticleDocument> result = repository.searchInProject(99L, queryCondition);

        assertThat(result).isEqualTo(mockHits);
        verify(articleDBRepository).findIdsByProjectId(99L);
        verify(documentOperations).searchInIds(List.of(1L, 2L), queryCondition);
    }

    @DisplayName("ArticleDocument 저장 요청을 위임한다")
    @Test
    void save() {
        ArticleDocument document = new ArticleDocument(new ArticleFixtureBuilder().build());

        repository.save(document);

        verify(documentOperations).save(document);
    }
}
