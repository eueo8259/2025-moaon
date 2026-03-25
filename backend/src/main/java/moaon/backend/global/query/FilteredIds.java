package moaon.backend.global.query;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the intermediate id set produced while combining multiple query filters.
 */
public class FilteredIds {

    private final Set<Long> ids;
    private final boolean hasResult;

    private FilteredIds(Set<Long> ids, boolean hasResult) {
        this.ids = new HashSet<>(ids);
        this.hasResult = hasResult;
    }

    public static FilteredIds init() {
        return new FilteredIds(Set.of(), false);
    }

    public static FilteredIds of(Set<Long> ids) {
        return new FilteredIds(ids, true);
    }

    public FilteredIds addFilterResult(Set<Long> newIds) {
        if (!hasResult) {
            return FilteredIds.of(newIds);
        }

        return intersect(newIds);
    }

    public Set<Long> getIds() {
        return new HashSet<>(ids);
    }

    public boolean hasResult() {
        return hasResult;
    }

    public boolean hasEmptyResult() {
        return ids.isEmpty() && hasResult;
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }

    public long size() {
        return ids.size();
    }

    private FilteredIds intersect(Set<Long> newIds) {
        if (hasEmptyResult() || newIds.isEmpty()) {
            return FilteredIds.of(Set.of());
        }

        Set<Long> intersection = new HashSet<>(ids);
        intersection.retainAll(newIds);
        return FilteredIds.of(intersection);
    }
}
