package moaon.backend.article.event;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.event.domain.EventStatus;
import moaon.backend.article.event.repository.EventOutboxRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutboxCleaner {

    private final EventOutboxRepository outboxRepository;


    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @SchedulerLock(
            name = "article_outbox_scheduler",
            lockAtMostFor = "10s",
            lockAtLeastFor = "2s"
    )
    @Transactional
    public void cleanupOldEvents() {
        int retentionDays = 7;

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);

        int deletedCount = outboxRepository.deleteByStatusAndProcessedAtBefore(
                EventStatus.PROCESSED,
                cutoffTime
        );

        if (deletedCount > 0) {
            log.info("오래된 Outbox 이벤트 {}건 삭제 완료 (기준: {}일 이전)",
                    deletedCount, retentionDays);
        }
    }
}
