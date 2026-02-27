package moaon.backend.article.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.Article;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.article.repository.es.ArticleDocumentRepository;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.domain.Project;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryFacade {

    private final ArticleDBRepository database;
    private final ArticleDocumentRepository elasticSearch;

    public ArticleSearchResult search(ArticleQueryCondition condition) {
        try {
            return elasticSearch.search(condition);

        } catch (Exception e) {
            log.error("검색엔진이 실패하였습니다. 데이터베이스로 검색을 시도합니다.", e);
            return database.findWithSearchConditions(condition);
        }
    }

    public ArticleSearchResult searchInProject(Project project, ProjectArticleQueryCondition condition) {
        return elasticSearch.searchInProject(project, condition.toArticleCondition());
    }

    public Optional<Article> findById(Long id) {
        return database.findById(id);
    }

    public void updateClicksCount(Article article) {
        //TODO 원자적 업데이트로 수정
    }

    public Article save(Article article) {
        return database.save(article); //TODO 이벤트 발행으로 수정 예정
    }
}
