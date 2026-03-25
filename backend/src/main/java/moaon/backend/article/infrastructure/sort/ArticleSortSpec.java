package moaon.backend.article.infrastructure.sort;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.Nullable;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;

/**
 * Querydsl-based pagination policy for article sorting.
 */
public interface ArticleSortSpec {

    ArticleSortType getType();

    OrderSpecifier<?>[] getOrderSpecifiers(@Nullable SearchKeyword searchKeyword);

    BooleanExpression getCursorPredicate(@Nullable CursorToken token, @Nullable SearchKeyword searchKeyword);

    String createNextCursor(Article article);
}
