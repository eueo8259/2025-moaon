package moaon.backend.article.repository;

import java.util.List;
import moaon.backend.article.domain.Article;

public interface ArticleSearchResult {

    List<Article> getArticles();

    long getTotalCount();

    boolean hasNext();

    String getNextCursor();
}
