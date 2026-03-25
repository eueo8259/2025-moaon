package moaon.backend.article.infrastructure.sort;

import static moaon.backend.article.domain.QArticle.article;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for article click count based pagination.
 */
@Component
public class ClicksArticleSortSpec implements ArticleSortSpec {

    @Override
    public ArticleSortType getType() {
        return ArticleSortType.CLICKS;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers(SearchKeyword searchKeyword) {
        return new OrderSpecifier<?>[]{article.clicks.desc(), article.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token, SearchKeyword searchKeyword) {
        if (token == null) {
            return null;
        }

        Integer clicks = parseSortValue(token);
        return article.clicks.lt(clicks)
                .or(article.clicks.eq(clicks).and(article.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Article article) {
        return CursorCodec.encode(new CursorToken(String.valueOf(article.getClicks()), article.getId()));
    }

    private Integer parseSortValue(CursorToken token) {
        try {
            return Integer.parseInt(token.sortValue());
        } catch (NumberFormatException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }
}
