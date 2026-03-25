package moaon.backend.article.repository.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.article.infrastructure.sort.ClicksArticleSortSpec;
import moaon.backend.article.repository.ArticleSearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DBArticleSearchResultTest {

    @DisplayName("빈 ArticleSearchResult를 만든다")
    @Test
    void empty() {
        ArticleSearchResult articles = DBArticleSearchResult.empty();
        assertAll(
                () -> assertThat(articles.getArticles()).isEmpty(),
                () -> assertThat(articles.getTotalCount()).isEqualTo(0),
                () -> assertThat(articles.getNextCursor()).isNull(),
                () -> assertThat(articles.hasNext()).isFalse()
        );
    }

    @DisplayName("limit보다 1개 더 조회된 경우 limit까지만 반환한다")
    @Test
    void getArticles() {
        List<Article> threeArticles = List.of(anyArticle(), anyArticle(), anyArticle());
        DBArticleSearchResult articles = new DBArticleSearchResult(
                threeArticles,
                4,
                2,
                ArticleSortType.CLICKS,
                new ClicksArticleSortSpec()
        );

        assertThat(articles.getArticles()).hasSize(2);
    }

    @DisplayName("다음 커서는 마지막 반환 아티클의 ID를 포함한다")
    @Test
    void getNextCursor() {
        List<Article> threeArticles = List.of(anyArticle(), anyArticle(), anyArticle());
        DBArticleSearchResult articles = new DBArticleSearchResult(
                threeArticles,
                4,
                2,
                ArticleSortType.CLICKS,
                new ClicksArticleSortSpec()
        );

        Article finallyLastArticle = articles.getArticles().getLast();
        String nextCursor = articles.getNextCursor();

        assertThat(nextCursor).endsWith("_" + finallyLastArticle.getId());
    }

    @DisplayName("다음 페이지가 존재하면 true를 반환한다")
    @Test
    void hasNextTrue() {
        DBArticleSearchResult articles = new DBArticleSearchResult(
                List.of(anyArticle(), anyArticle(), anyArticle()),
                999,
                2,
                ArticleSortType.CLICKS,
                new ClicksArticleSortSpec()
        );

        assertThat(articles.hasNext()).isTrue();
    }

    @DisplayName("다음 페이지가 없으면 false를 반환한다")
    @Test
    void hasNextFalse() {
        DBArticleSearchResult articles = new DBArticleSearchResult(
                List.of(anyArticle(), anyArticle(), anyArticle()),
                999,
                3,
                ArticleSortType.CLICKS,
                new ClicksArticleSortSpec()
        );

        assertThat(articles.hasNext()).isFalse();
    }

    private final AtomicLong seq = new AtomicLong(1);

    private Article anyArticle() {
        return Article.builder()
                .id(seq.getAndIncrement())
                .build();
    }
}
