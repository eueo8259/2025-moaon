package moaon.backend.fixture;

import java.util.Arrays;
import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.domain.Topic;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;

public class ArticleQueryConditionBuilder {

    private SearchKeyword search;
    private Sector sector;
    private List<Topic> topics;
    private List<String> techStackNames;
    private ArticleSortType sortBy;
    private int limit;
    private String articleCursor;

    public ArticleQueryConditionBuilder() {
        this.search = new SearchKeyword(null);
        this.sector = null;
        this.topics = null;
        this.techStackNames = null;
        this.sortBy = null;
        this.limit = 20;
        this.articleCursor = null;
    }

    public ArticleQueryConditionBuilder search(String search) {
        this.search = new SearchKeyword(search);
        return this;
    }

    public ArticleQueryConditionBuilder sector(Sector sector) {
        this.sector = sector;
        return this;
    }

    public ArticleQueryConditionBuilder topics(Topic... topics) {
        this.topics = Arrays.asList(topics);
        return this;
    }

    public ArticleQueryConditionBuilder techStackNames(List<String> techStackNames) {
        this.techStackNames = techStackNames;
        return this;
    }

    public ArticleQueryConditionBuilder techStackNames(String... techStackNames) {
        this.techStackNames = Arrays.asList(techStackNames);
        return this;
    }

    public ArticleQueryConditionBuilder sortBy(ArticleSortType articleSortType) {
        this.sortBy = articleSortType;
        return this;
    }

    public ArticleQueryConditionBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public ArticleQueryConditionBuilder cursor(String cursor) {
        this.articleCursor = cursor;
        return this;
    }

    public ArticleQueryConditionBuilder cursor(CursorToken cursorToken) {
        this.articleCursor = CursorCodec.encode(cursorToken);
        return this;
    }

    public ArticleQueryCondition build() {
        return new ArticleQueryCondition(
                search,
                sector,
                topics,
                techStackNames,
                sortBy,
                limit,
                articleCursor
        );
    }
}
