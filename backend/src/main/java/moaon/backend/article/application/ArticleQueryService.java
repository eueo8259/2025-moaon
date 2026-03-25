package moaon.backend.article.application;

import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.application.dto.ArticleResponse;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.repository.ArticleRepositoryFacade;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.article.repository.db.ArticleDBRepository;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.application.dto.ProjectArticleResponse;
import moaon.backend.project.domain.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleQueryService {

    private final ArticleRepositoryFacade articleRepositoryFacade;
    private final ArticleDBRepository articleDBRepository;
    private final ProjectRepository projectRepository;

    public ArticleResponse getPagedArticles(ArticleQueryCondition queryCondition) {
        ArticleSearchResult result = articleRepositoryFacade.search(queryCondition);
        return ArticleResponse.from(result);
    }

    public ProjectArticleResponse getByProjectId(long id, ProjectArticleQueryCondition condition) {
        if (!projectRepository.existsById(id)) {
            throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
        }

        ArticleSearchResult filteredArticles = articleRepositoryFacade.searchInProject(id, condition);
        Map<Sector, Long> articleCountBySector = countArticlesGroupBySector(id);
        return ProjectArticleResponse.of(filteredArticles.getArticles(), articleCountBySector);
    }

    private Map<Sector, Long> countArticlesGroupBySector(long projectId) {
        Map<Sector, Long> articleCountBySector = new EnumMap<>(Sector.class);
        for (Sector sector : Sector.values()) {
            articleCountBySector.put(sector, articleDBRepository.countByProjectIdAndSector(projectId, sector));
        }
        return articleCountBySector;
    }
}
