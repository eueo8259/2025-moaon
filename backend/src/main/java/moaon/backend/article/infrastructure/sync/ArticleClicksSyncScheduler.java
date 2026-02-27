package moaon.backend.article.infrastructure.sync;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.domain.ArticleClicks;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.article.repository.es.ArticleDocumentRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleClicksSyncScheduler {

    private final ArticleDBRepository articleDBRepository;
    private final ArticleDocumentRepository articleDocumentRepository;

    @Scheduled(cron = "0 0 * * * *")  // 1시간마다
    @SchedulerLock(name = "article_clicks_sync", lockAtMostFor = "50m", lockAtLeastFor = "1m")
    public void syncClicksToEs() {
        log.info("clicks ES 동기화 시작");
        try {
            List<ArticleClicks> clicksList = articleDBRepository.findAllClicks();
            articleDocumentRepository.bulkUpdateClicks(clicksList);
            log.info("clicks ES 동기화 완료 - {}건", clicksList.size());
        } catch (Exception e) {
            log.error("clicks ES 동기화 실패", e);
        }
    }
}