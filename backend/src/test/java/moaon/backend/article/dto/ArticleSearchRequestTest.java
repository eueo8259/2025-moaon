package moaon.backend.article.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.application.dto.ArticleSearchRequest;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.domain.Topic;
import moaon.backend.global.domain.SearchKeyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArticleSearchRequestTest {

    @DisplayName("Request DTO를 Query Condition으로 변환한다")
    @Test
    void toCondition() {
        ArticleSearchRequest request = new ArticleSearchRequest();
        request.setSearch("검색어");
        request.setSector("be");
        request.setTopics(List.of("db"));
        request.setTechStacks(List.of("mysql"));
        request.setSort("relevance");
        request.setLimit(10);
        request.setCursor("2025-09-25T04:35:00.764_35867");

        ArticleQueryCondition condition = request.toCondition();

        assertThat(condition).isEqualTo(new ArticleQueryCondition(
                new SearchKeyword("검색어"),
                Sector.BE,
                List.of(Topic.DATABASE),
                List.of("mysql"),
                ArticleSortType.RELEVANCE,
                10,
                "2025-09-25T04:35:00.764_35867"
        ));
    }

    @DisplayName("초기화하지 않은 리스트는 빈 리스트로 유지된다")
    @Test
    void toConditionNotInitializedList() {
        ArticleSearchRequest request = new ArticleSearchRequest();
        request.setSearch("검색어");
        request.setSector("be");
        request.setSort("relevance");
        request.setLimit(10);
        request.setCursor("2025-09-25T04:35:00.764_35867");

        ArticleQueryCondition condition = request.toCondition();

        assertThat(condition).isEqualTo(new ArticleQueryCondition(
                new SearchKeyword("검색어"),
                Sector.BE,
                List.of(),
                List.of(),
                ArticleSortType.RELEVANCE,
                10,
                "2025-09-25T04:35:00.764_35867"
        ));
    }
}
