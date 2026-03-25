package moaon.backend.article.application.dto;

import java.util.List;
import moaon.backend.article.repository.ArticleSearchResult;

public record ArticleResponse(
        List<ArticleData> contents,
        int totalCount,
        boolean hasNext,
        String nextCursor
) {

    public static ArticleResponse from(ArticleSearchResult searchResult) {
        return new ArticleResponse(
                ArticleData.from(searchResult.getArticles()),
                (int) searchResult.getTotalCount(),
                searchResult.hasNext(),
                searchResult.getNextCursor()
        );
    }
}
