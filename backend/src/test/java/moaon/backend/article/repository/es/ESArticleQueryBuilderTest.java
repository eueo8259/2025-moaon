package moaon.backend.article.repository.es;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.domain.Topic;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

class ESArticleQueryBuilderTest {

    @DisplayName("Article ID 목록으로 filter query를 만든다")
    @Test
    void withIdsAddsTermsFilter() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withIds(List.of(1L, 2L, 3L))
                .build();

        assertThat(query.getQuery().toString()).containsSubsequence("filter", "terms", "id", "[1,2,3]");
    }

    @DisplayName("검색어가 있으면 multi_match query를 추가한다")
    @Test
    void withTextSearchAddsMustQuery() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withTextSearch(new SearchKeyword("백엔드"))
                .build();

        assertThat(query.getQuery().toString()).containsSubsequence("multi_match", "query", "백엔드");
    }

    @DisplayName("비어 있는 검색어는 multi_match query를 만들지 않는다")
    @Test
    void withEmptyTextSkipsQuery() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withTextSearch(new SearchKeyword(""))
                .build();

        assertThat(query.getQuery().toString()).doesNotContain("multi_match", "query");
    }

    @DisplayName("sector filter를 추가한다")
    @Test
    void withSectorAddsFilter() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withSector(Sector.FE)
                .build();

        assertThat(query.getQuery().toString()).containsSubsequence("filter", "term", "sector", "FE");
    }

    @DisplayName("topics AND filter를 추가한다")
    @Test
    void withTopicsAndMatchAddsFilter() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withTopicsAndMatch(List.of(Topic.DATABASE, Topic.API_DESIGN))
                .build();

        assertThat(query.getQuery().toString())
                .containsSubsequence("filter", "term", "topics", "DATABASE", "term", "topics", "API_DESIGN");
    }

    @DisplayName("topics OR filter를 추가한다")
    @Test
    void withTopicsOrMatchAddsFilter() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withTopicsOrMatch(List.of(Topic.DATABASE, Topic.API_DESIGN))
                .build();

        assertThat(query.getQuery().toString())
                .containsSubsequence("filter", "should", "term", "topics", "DATABASE", "term", "topics", "API_DESIGN");
    }

    @DisplayName("pagination과 정렬을 함께 설정한다")
    @Test
    void withPaginationAndSort() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withPagination(20, new CursorToken("100", 10L), ArticleSortType.CLICKS)
                .withSort(ArticleSortType.CLICKS)
                .build();

        Pageable pageable = query.getPageable();
        Sort sort = query.getSort();

        assertAll(
                () -> assertThat(pageable.getPageSize()).isEqualTo(20),
                () -> assertThat(sort.getOrderFor("clicks")).isNotNull(),
                () -> assertThat(query.getSearchAfter()).contains(100L, 10L)
        );
    }

    @DisplayName("relevance 정렬이면 score 추적을 활성화한다")
    @Test
    void relevanceSortEnablesTrackScores() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withSort(ArticleSortType.RELEVANCE)
                .build();

        assertAll(
                () -> assertThat(query.getSort().getOrderFor("_score")).isNotNull(),
                () -> assertThat(query.getTrackScores()).isTrue()
        );
    }

    @DisplayName("ArticleQueryCondition 전체를 조합해 query를 만든다")
    @Test
    void withQueryConditionCombinesAll() {
        NativeQuery query = new ESArticleQueryBuilder()
                .withQueryCondition(new ArticleQueryCondition(
                        new SearchKeyword("테스트"),
                        Sector.BE,
                        List.of(Topic.DATABASE),
                        List.of("mysql"),
                        ArticleSortType.CLICKS,
                        10,
                        "10_5"
                ))
                .build();

        String queryString = query.getQuery().toString();
        assertAll(
                () -> assertThat(queryString).containsSubsequence("filter", "term", "sector", "BE"),
                () -> assertThat(queryString).contains("filter", "term", "topics", "DATABASE"),
                () -> assertThat(queryString).contains("filter", "term", "techStacks", "mysql"),
                () -> assertThat(query.getSort().getOrderFor("clicks")).isNotNull(),
                () -> assertThat(query.getSearchAfter()).contains(10L, 5L)
        );
    }
}
