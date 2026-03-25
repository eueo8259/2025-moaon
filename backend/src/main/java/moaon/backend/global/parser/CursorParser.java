package moaon.backend.global.parser;

import moaon.backend.article.domain.ArticleCursor;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;

public class CursorParser {

    private static final String COUNT_BASED_CURSOR_REGEX = "[0-9]+_[0-9]+";
    private static final String CREATED_AT_CURSOR_REGEX = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?_[0-9]+$";
    private static final LongParser LONG_PARSER = new LongParser();

    private CursorParser() {
    }

    public static ArticleCursor toArticleCursor(String cursor) {
        if (isCursorEmpty(cursor)) {
            return null;
        }

        String[] valueAndId = splitAndValidateFormat(cursor);

        Object sortValue = valueAndId[0];
        Long lastId = LONG_PARSER.parse(valueAndId[1]);

        return new ArticleCursor(sortValue, lastId);
    }

    private static String[] splitAndValidateFormat(String cursor) {
        if (!cursor.matches(COUNT_BASED_CURSOR_REGEX) && !cursor.matches(CREATED_AT_CURSOR_REGEX)) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }

        return cursor.split("_");
    }

    private static boolean isCursorEmpty(String cursor) {
        return cursor == null || cursor.isEmpty();
    }
}
