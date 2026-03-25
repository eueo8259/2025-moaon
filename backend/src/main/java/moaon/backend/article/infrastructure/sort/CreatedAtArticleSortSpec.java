package moaon.backend.article.infrastructure.sort;

import static moaon.backend.article.domain.QArticle.article;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for article creation time based pagination.
 */
@Component
public class CreatedAtArticleSortSpec implements ArticleSortSpec {

    @Override
    public ArticleSortType getType() {
        return ArticleSortType.CREATED_AT;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers(SearchKeyword searchKeyword) {
        return new OrderSpecifier<?>[]{article.createdAt.desc(), article.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token, SearchKeyword searchKeyword) {
        if (token == null) {
            return null;
        }

        LocalDateTime createdAt = parseSortValue(token);
        return article.createdAt.lt(createdAt)
                .or(article.createdAt.eq(createdAt).and(article.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Article article) {
        return CursorCodec.encode(new CursorToken(article.getCreatedAt().toString(), article.getId()));
    }

    private LocalDateTime parseSortValue(CursorToken token) {
        try {
            return LocalDateTime.parse(token.sortValue());
        } catch (RuntimeException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }
}
