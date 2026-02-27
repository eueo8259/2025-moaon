package moaon.backend.article.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleDocument;
import moaon.backend.article.event.domain.EventAction;
import moaon.backend.article.event.domain.EventOutbox;
import moaon.backend.article.event.repository.EventOutboxRepository;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleEventPublisher {

    private final EventOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void publishInsert(Article article) {
        publish(article, EventAction.INSERT);
    }

    private void publish(Article article, EventAction action) {
        ArticleDocument document = new ArticleDocument(article);
        String payload = convertToJson(document);  // 변환 책임을 Publisher가 가짐

        EventOutbox outbox = EventOutbox.builder()
                .entityId(article.getId())
                .eventType("articles")
                .action(action)
                .payload(payload)
                .build();

        outboxRepository.save(outbox);
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ARTICLE_PROCESSING_FAILED);
        }
    }
}
