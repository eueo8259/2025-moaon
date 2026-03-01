package moaon.backend.article.application;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.application.dto.ArticleResponse;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.repository.ArticleRepositoryFacade;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.application.dto.ProjectArticleResponse;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleQueryService {

    private final ArticleRepositoryFacade articleRepositoryFacade;
    private final ProjectRepository projectRepository;

    public ArticleResponse getPagedArticles(ArticleQueryCondition queryCondition) {
        ArticleSearchResult result = articleRepositoryFacade.search(queryCondition);
        return ArticleResponse.from(result);
    }

    public ProjectArticleResponse getByProjectId(long id, ProjectArticleQueryCondition condition) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        ArticleSearchResult filteredArticles = articleRepositoryFacade.searchInProject(project, condition);
        Map<Sector, Long> articleCountBySector = project.countArticlesGroupBySector();
        return ProjectArticleResponse.of(filteredArticles.getArticles(), articleCountBySector);
    }
}