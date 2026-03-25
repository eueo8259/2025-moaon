package moaon.backend.article.sort;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import moaon.backend.article.infrastructure.sort.ClicksArticleSortSpec;
import moaon.backend.article.infrastructure.sort.CreatedAtArticleSortSpec;
import moaon.backend.article.infrastructure.sort.RelevanceArticleSortSpec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArticleSortSpecTest {

    @DisplayName("createdAt 정렬은 생성일과 ID 기준 커서 조건을 만든다")
    @Test
    void createdAtPredicate() {
        CreatedAtArticleSortSpec sortSpec = new CreatedAtArticleSortSpec();

        BooleanExpression predicate = sortSpec.getCursorPredicate(
                new CursorToken(LocalDateTime.of(2025, 10, 26, 12, 0).toString(), 10L),
                null
        );

        assertThat(predicate.toString())
                .containsSubsequence("article.createdAt <", "||", "article.createdAt =", "&&", "article.id <");
    }

    @DisplayName("clicks 정렬은 클릭수와 ID 기준 커서 조건을 만든다")
    @Test
    void clicksPredicate() {
        ClicksArticleSortSpec sortSpec = new ClicksArticleSortSpec();

        BooleanExpression predicate = sortSpec.getCursorPredicate(new CursorToken("123", 9L), null);

        assertThat(predicate.toString())
                .containsSubsequence("article.clicks <", "||", "article.clicks =", "&&", "article.id <");
    }

    @DisplayName("relevance 정렬은 score와 ID 기준 커서 조건을 만든다")
    @Test
    void relevancePredicate() {
        RelevanceArticleSortSpec sortSpec = new RelevanceArticleSortSpec();

        BooleanExpression predicate = sortSpec.getCursorPredicate(
                new CursorToken("0.85", 5L),
                new SearchKeyword("백엔드")
        );

        assertThat(predicate.toString()).contains("article.id <");
    }
}
