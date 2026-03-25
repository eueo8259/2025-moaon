package moaon.backend.article.infrastructure.sort;

import static moaon.backend.article.domain.QArticle.article;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.article.repository.db.ArticleFullTextSearchHQLFunction;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for article full-text relevance based pagination.
 */
@Component
public class RelevanceArticleSortSpec implements ArticleSortSpec {

    @Override
    public ArticleSortType getType() {
        return ArticleSortType.RELEVANCE;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers(SearchKeyword searchKeyword) {
        NumberTemplate<Double> score = score(searchKeyword);
        return new OrderSpecifier<?>[]{score.desc(), article.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token, SearchKeyword searchKeyword) {
        if (token == null) {
            return null;
        }

        Double score = parseSortValue(token);
        NumberTemplate<Double> scoreReference = score(searchKeyword);
        return scoreReference.lt(score)
                .or(scoreReference.eq(score).and(article.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Article article) {
        return CursorCodec.encode(new CursorToken(String.valueOf(article.getScore()), article.getId()));
    }

    private Double parseSortValue(CursorToken token) {
        try {
            return Double.parseDouble(token.sortValue());
        } catch (NumberFormatException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }

    private NumberTemplate<Double> score(SearchKeyword searchKeyword) {
        if (searchKeyword == null || !searchKeyword.hasValue()) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
        return ArticleFullTextSearchHQLFunction.scoreReference(searchKeyword);
    }
}
