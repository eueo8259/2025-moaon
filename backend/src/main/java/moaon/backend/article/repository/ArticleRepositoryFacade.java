package moaon.backend.article.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.article.repository.es.ArticleDocumentRepository;
import moaon.backend.article.repository.es.ESArticleSearchResult;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryFacade {

    private final ArticleDBRepository database;
    private final ArticleDocumentRepository elasticSearch;

    public ArticleSearchResult search(ArticleQueryCondition condition) {
        try {
            SearchHits<ArticleDocument> hits = elasticSearch.search(condition);
            return wrapSearchHits(hits, condition);
        } catch (Exception e) {
            log.error("검색 엔진이 실패했습니다. 데이터베이스로 검색을 시도합니다.", e);
            return database.findWithSearchConditions(condition);
        }
    }

    public ArticleSearchResult searchInProject(long projectId, ProjectArticleQueryCondition condition) {
        try {
            SearchHits<ArticleDocument> hits = elasticSearch.searchInProject(projectId, condition.toArticleCondition());
            return wrapSearchHits(hits, condition.toArticleCondition());
        } catch (Exception e) {
            log.error("검색 엔진이 실패했습니다. 데이터베이스로 검색을 시도합니다.", e);
            return database.findByProjectWithCondition(projectId, condition);
        }
    }

    public Optional<Article> findById(Long id) {
        return database.findById(id);
    }

    public void incrementClickCount(Long id) {
        database.incrementClickCount(id);
    }

    public Article save(Article article) {
        return database.save(article);
    }

    private ArticleSearchResult wrapSearchHits(SearchHits<ArticleDocument> hits, ArticleQueryCondition condition) {
        List<Long> ids = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ArticleDocument::getId)
                .toList();

        List<Article> articles = database.findAllById(ids)
                .stream()
                .sorted(Comparator.comparingInt(a -> ids.indexOf(a.getId())))
                .toList();

        return new ESArticleSearchResult(hits, articles, condition.limit());
    }
}
