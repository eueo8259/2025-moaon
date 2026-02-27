package moaon.backend.article.repository.db;

import java.util.List;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.Article;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.domain.Project;

public interface CustomizedArticleRepository {

    ArticleSearchResult findWithSearchConditions(ArticleQueryCondition queryCondition);

    ArticleSearchResult findByProjectWithCondition(Project project, ProjectArticleQueryCondition condition);

    List<Article> findAllByProjectIdAndCondition(long id, ProjectArticleQueryCondition condition);
}
