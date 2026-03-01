package moaon.backend.project.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import java.util.List;
import moaon.backend.article.application.ArticleQueryService;
import moaon.backend.global.cookie.AccessHistory;
import moaon.backend.global.cookie.TrackingCookieManager;
import moaon.backend.project.application.ProjectService;
import moaon.backend.project.application.dto.PagedProjectResponse;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.application.dto.ProjectArticleResponse;
import moaon.backend.project.application.dto.ProjectCreateRequest;
import moaon.backend.project.application.dto.ProjectCreateResponse;
import moaon.backend.project.application.dto.ProjectDetailResponse;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final TrackingCookieManager cookieManager;
    private final ProjectService projectService;
    private final ArticleQueryService articleQueryService;

    public ProjectController(
            @Qualifier("projectViewCookieManager") TrackingCookieManager cookieManager,
            ProjectService projectService,
            ArticleQueryService articleQueryService
    ) {
        this.cookieManager = cookieManager;
        this.projectService = projectService;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity<ProjectCreateResponse> saveProject(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody @Valid ProjectCreateRequest projectCreateRequest
    ) {
        Long savedId = projectService.save(token, projectCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectCreateResponse.from(savedId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable("id") long id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AccessHistory accessHistory = cookieManager.extractViewedMap(request);
        if (cookieManager.isCountIncreasable(id, accessHistory)) {
            ProjectDetailResponse projectDetailResponse = projectService.increaseViewsCount(id);
            cookieManager.createOrUpdateCookie(id, accessHistory, response);
            return ResponseEntity.ok(projectDetailResponse);
        }
        return ResponseEntity.ok(projectService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PagedProjectResponse> getPagedProjects(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categories", required = false) List<String> categories,
            @RequestParam(value = "techStacks", required = false) List<String> techStacks,
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "limit") @Validated @Max(100) int limit,
            @RequestParam(value = "cursor", required = false) String cursor
    ) {
        ProjectQueryCondition condition = ProjectQueryCondition.of(search, categories, techStacks, sortType, limit,
                cursor);
        return ResponseEntity.ok(projectService.getPagedProjects(condition));
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<ProjectArticleResponse> getArticlesByProjectId(
            @PathVariable("id") long id,
            @RequestParam(value = "sector", required = false) String sector,
            @RequestParam(value = "search", required = false) String search
    ) {
        ProjectArticleResponse response = articleQueryService.getByProjectId(
                id,
                ProjectArticleQueryCondition.from(sector, search)
        );
        return ResponseEntity.ok(response);
    }
}
