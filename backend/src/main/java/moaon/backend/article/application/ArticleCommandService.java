package moaon.backend.article.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleCreateRequest;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleContent;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.domain.Topic;
import moaon.backend.article.event.ArticleEventPublisher;
import moaon.backend.article.repository.ArticleRepositoryFacade;
import moaon.backend.article.repository.db.ArticleContentRepository;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.member.domain.Member;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.techstack.domain.repository.TechStackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArticleCommandService {

    private final ArticleRepositoryFacade articleRepositoryFacade;
    private final ArticleContentRepository articleContentRepository;
    private final ProjectRepository projectRepository;
    private final TechStackRepository techStackRepository;
    private final ArticleEventPublisher articleEventPublisher;

    public void increaseClicksCount(long id) {
        articleRepositoryFacade.incrementClickCount(id);
    }

    public void save(List<ArticleCreateRequest> requests, Member member) {
        for (ArticleCreateRequest request : requests) {
            Project project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

            project.validateAuthor(member);

            Article article = new Article(
                    request.title(),
                    request.summary(),
                    articleContentRepository.findByUrl(request.url().toString())
                            .map(ArticleContent::getContent)
                            .orElse(""),
                    request.url().toString(),
                    LocalDateTime.now(),
                    project,
                    Sector.of(request.sector()),
                    request.topics().stream().map(Topic::of).toList(),
                    request.techStacks().stream()
                            .map(techStack -> techStackRepository.findByName(techStack)
                                    .orElseThrow(() -> new CustomException(ErrorCode.TECHSTACK_NOT_FOUND)))
                            .toList()
            );

            Article saved = articleRepositoryFacade.save(article);
            articleEventPublisher.publishInsert(saved);
        }
    }
}
