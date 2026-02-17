package moaon.backend.article.event.repository;

import java.util.List;
import moaon.backend.article.event.domain.EventOutbox;
import moaon.backend.article.event.domain.EventStatus;

public interface EventOutboxRepositoryCustom {

    List<EventOutbox> findEventsByStatus(EventStatus status, int batchSize);

    void markAsProcessed(List<Long> ids);

    void incrementFailCount(List<Long> ids);

    void markAsFailed(List<Long> ids);
}
