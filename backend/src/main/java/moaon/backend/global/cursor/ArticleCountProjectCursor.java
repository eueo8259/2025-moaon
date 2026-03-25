package moaon.backend.global.cursor;

import static moaon.backend.article.domain.QArticle.article;
import static moaon.backend.project.domain.QProject.project;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArticleCountProjectCursor implements Cursor<Integer> {

    private final int count;
    private final Long id;

    @Override
    public Integer getSortValue() {
        return count;
    }

    @Override
    public Long getLastId() {
        return id;
    }

    @Override
    public String getNextCursor() {
        return count + "_" + id;
    }

    @Override
    public BooleanExpression getCursorExpression() {
        return articleCount().lt((long) getSortValue())
                .or(
                        articleCount().eq((long) getSortValue())
                                .and(project.id.lt(getLastId()))
                );
    }

    private NumberExpression<Long> articleCount() {
        return Expressions.asNumber(
                JPAExpressions.select(article.count())
                        .from(article)
                        .where(article.project.id.eq(project.id))
        );
    }
}
