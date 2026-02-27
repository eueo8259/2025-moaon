package moaon.backend.article.repository.es;

import static moaon.backend.article.domain.ArticleSortType.CREATED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.fixture.ArticleFixtureBuilder;
import moaon.backend.fixture.ArticleQueryConditionBuilder;
import moaon.backend.project.domain.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHits;

class ArticleDocumentRepositoryTest {

    private final ArticleDocumentOperations documentOperations = Mockito.mock(ArticleDocumentOperations.class);
    private final ArticleDocumentRepository repository = new ArticleDocumentRepository(documentOperations);

    private final ArticleQueryCondition queryCondition = new ArticleQueryConditionBuilder().sortBy(CREATED_AT).build();

    @DisplayName("검색 요청을 DocumentOperations에 위임하고 SearchHits를 반환한다.")
    @Test
    void search() {
        // given
        SearchHits<ArticleDocument> mockHits = mock(SearchHits.class);
        when(documentOperations.search(queryCondition)).thenReturn(mockHits);

        // when
        SearchHits<ArticleDocument> result = repository.search(queryCondition);

        // then
        assertThat(result).isEqualTo(mockHits);
        verify(documentOperations).search(queryCondition);
    }

    @DisplayName("프로젝트 검색 요청 시 프로젝트의 아티클 ID 목록을 DocumentOperations에 전달한다.")
    @Test
    void searchInProject() {
        // given
        Project project = Project.builder().id(99L)
                .articles(List.of(
                        new ArticleFixtureBuilder().id(1L).build(),
                        new ArticleFixtureBuilder().id(2L).build())
                ).build();

        SearchHits<ArticleDocument> mockHits = mock(SearchHits.class);
        when(documentOperations.searchInIds(List.of(1L, 2L), queryCondition)).thenReturn(mockHits);

        // when
        SearchHits<ArticleDocument> result = repository.searchInProject(project, queryCondition);

        // then
        assertThat(result).isEqualTo(mockHits);
        verify(documentOperations).searchInIds(List.of(1L, 2L), queryCondition);
    }

    @DisplayName("ArticleDocument 저장 요청을 위임한다.")
    @Test
    void save() {
        // given
        ArticleDocument document = new ArticleDocument(new ArticleFixtureBuilder().build());

        // when
        repository.save(document);

        // then
        verify(documentOperations).save(document);
    }
}
