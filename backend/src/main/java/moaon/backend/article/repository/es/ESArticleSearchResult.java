package moaon.backend.article.repository.es;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import org.springframework.data.elasticsearch.core.SearchHits;

@RequiredArgsConstructor
public class ESArticleSearchResult implements ArticleSearchResult {

    private final SearchHits<ArticleDocument> searchHits;
    private final List<Article> originArticles;
    private final int limit;

    @Override
    public List<Article> getArticles() {
        return Collections.unmodifiableList(originArticles);
    }

    @Override
    public String getNextCursor() {
        if (!hasNext()) {
            return null;
        }

        List<Object> sortValues = searchHits.getSearchHits().getLast().getSortValues();
        return CursorCodec.encode(new CursorToken(sortValues.get(0).toString(), Long.parseLong(sortValues.get(1).toString())));
    }

    @Override
    public long getTotalCount() {
        return searchHits.getTotalHits();
    }

    @Override
    public boolean hasNext() {
        return getArticles().size() == limit;
    }
}
