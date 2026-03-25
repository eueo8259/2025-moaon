package moaon.backend.global.cursor;

/**
 * Immutable cursor payload shared across no-offset pagination flows.
 */
public record CursorToken(
        String sortValue,
        Long lastId
) {
}
