package moaon.backend.article.repository.es;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.ArticleClicks;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.repository.db.ArticleDBRepository;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleDocumentRepository {

    private final ArticleDocumentOperations documentOperations;
    private final ArticleDBRepository articleDBRepository;

    public SearchHits<ArticleDocument> search(ArticleQueryCondition condition) {
        return documentOperations.search(condition);
    }

    public SearchHits<ArticleDocument> searchInProject(long projectId, ArticleQueryCondition condition) {
        List<Long> articleIds = articleDBRepository.findIdsByProjectId(projectId);
        return documentOperations.searchInIds(articleIds, condition);
    }

    public ArticleDocument save(ArticleDocument articleDocument) {
        return documentOperations.save(articleDocument);
    }

    public void bulkUpdateClicks(List<ArticleClicks> clicksList) {
        documentOperations.bulkUpdateClicks(clicksList);
    }
}
