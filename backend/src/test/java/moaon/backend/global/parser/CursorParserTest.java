package moaon.backend.global.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import moaon.backend.global.cursor.CursorCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CursorParserTest {

    @DisplayName("cursor format can be decoded")
    @Test
    void cursorParse() {
        String cursor1 = "2025-09-25T04:35:00.764_35867";
        String cursor2 = "2025-09-25T04:35:00_35867";
        String cursor3 = "2025-09-25T12:13:36.146232141_35867";

        assertAll(
                () -> assertDoesNotThrow(() -> CursorCodec.decode(cursor1)),
                () -> assertDoesNotThrow(() -> CursorCodec.decode(cursor2)),
                () -> assertDoesNotThrow(() -> CursorCodec.decode(cursor3))
        );
    }
}
