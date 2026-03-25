package moaon.backend.global.cursor;

import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;

/**
 * Encodes and decodes raw cursor strings into a transport-only cursor token.
 */
public final class CursorCodec {

    private static final String DELIMITER = "_";

    private CursorCodec() {
    }

    public static CursorToken decode(String rawCursor) {
        if (rawCursor == null || rawCursor.isBlank()) {
            return null;
        }

        String[] parts = rawCursor.split(DELIMITER, 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }

        try {
            return new CursorToken(parts[0], Long.parseLong(parts[1]));
        } catch (NumberFormatException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }

    public static String encode(CursorToken token) {
        if (token == null) {
            return null;
        }

        if (token.sortValue() == null || token.sortValue().isBlank() || token.lastId() == null) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }

        return token.sortValue() + DELIMITER + token.lastId();
    }
}
