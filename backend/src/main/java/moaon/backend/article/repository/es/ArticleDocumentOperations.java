package moaon.backend.article.repository.es;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleClicks;
import moaon.backend.article.domain.ArticleDocument;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDocumentOperations {

    private static final IndexCoordinates ARTICLE_ALIAS = IndexCoordinates.of("articles");

    private final ElasticsearchOperations ops;

    public SearchHits<ArticleDocument> search(ArticleQueryCondition condition) {
        NativeQuery esArticleQuery = new ESArticleQueryBuilder()
                .withQueryCondition(condition)
                .build();
        return ops.search(esArticleQuery, ArticleDocument.class, ARTICLE_ALIAS);
    }

    public SearchHits<ArticleDocument> searchInIds(List<Long> articleIds, ArticleQueryCondition condition) {
        NativeQuery esArticleQuery = new ESArticleQueryBuilder()
                .withIds(articleIds)
                .withQueryCondition(condition)
                .build();

        return ops.search(esArticleQuery, ArticleDocument.class, ARTICLE_ALIAS);
    }

    public ArticleDocument save(ArticleDocument articleDocument) {
        return ops
                .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                .save(articleDocument, ARTICLE_ALIAS);
    }

    public void bulkUpdateClicks(List<ArticleClicks> clicksList) {
        List<UpdateQuery> queries = clicksList.stream()
                .map(c -> UpdateQuery.builder(c.id().toString())
                        .withDocument(Document.create().append("clicks", c.clicks()))
                        .build())
                .toList();

        ops.bulkUpdate(queries, ArticleDocument.class);
    }
}
