package moaon.backend.project.infrastructure.sort;

import static moaon.backend.article.domain.QArticle.article;
import static moaon.backend.project.domain.QProject.project;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectSortType;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for project article count based pagination.
 */
@Component
public class ArticleCountProjectSortSpec implements ProjectSortSpec {

    @Override
    public ProjectSortType getType() {
        return ProjectSortType.ARTICLE_COUNT;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers() {
        return new OrderSpecifier<?>[]{articleCount().desc(), project.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token) {
        if (token == null) {
            return null;
        }

        Integer articleCount = parseSortValue(token);
        return articleCount().lt(articleCount.longValue())
                .or(articleCount().eq(articleCount.longValue()).and(project.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Project project) {
        return CursorCodec.encode(new CursorToken(String.valueOf(project.getArticleCount()), project.getId()));
    }

    private Integer parseSortValue(CursorToken token) {
        try {
            return Integer.parseInt(token.sortValue());
        } catch (NumberFormatException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }

    private NumberExpression<Long> articleCount() {
        return Expressions.asNumber(
                JPAExpressions.select(article.count())
                        .from(article)
                        .where(article.project.id.eq(project.id))
        );
    }
}
