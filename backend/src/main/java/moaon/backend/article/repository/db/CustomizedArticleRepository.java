package moaon.backend.article.repository.db;

import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.infrastructure.sort.ArticleSortSpec;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;

public interface CustomizedArticleRepository {

    ArticleSearchResult findWithSearchConditions(
            ArticleQueryCondition queryCondition,
            ArticleSortSpec sortSpec,
            CursorToken cursorToken
    );

    ArticleSearchResult findByProjectWithCondition(
            long projectId,
            ProjectArticleQueryCondition condition,
            ArticleSortSpec sortSpec,
            CursorToken cursorToken
    );
}
