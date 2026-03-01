package moaon.backend.article.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import moaon.backend.article.application.ArticleCommandService;
import moaon.backend.article.application.ArticleQueryService;
import moaon.backend.article.application.dto.ArticleCreateRequest;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.application.dto.ArticleResponse;
import moaon.backend.article.application.dto.ArticleSearchRequest;
import moaon.backend.global.cookie.AccessHistory;
import moaon.backend.global.cookie.TrackingCookieManager;
import moaon.backend.member.application.MemberService;
import moaon.backend.member.domain.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final TrackingCookieManager cookieManager;
    private final ArticleCommandService articleCommandService;
    private final ArticleQueryService articleQueryService;
    private final MemberService memberService;

    public ArticleController(
            @Qualifier("articleClickCookieManager") TrackingCookieManager cookieManager,
            ArticleCommandService articleCommandService, ArticleQueryService articleQueryService,
            MemberService memberService
    ) {
        this.cookieManager = cookieManager;
        this.articleCommandService = articleCommandService;
        this.articleQueryService = articleQueryService;
        this.memberService = memberService;
    }

    @GetMapping("/articles")
    public ResponseEntity<ArticleResponse> getPagedArticles(@ModelAttribute @Validated ArticleSearchRequest request) {
        ArticleQueryCondition condition = request.toCondition();
        return ResponseEntity.ok(articleQueryService.getPagedArticles(condition));
    }


    @PostMapping
    public ResponseEntity<Void> saveArticles(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody @Valid List<ArticleCreateRequest> requests
    ) {
        Member member = memberService.getUserByToken(token);
        articleCommandService.save(requests, member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/clicks")
    public ResponseEntity<Void> updateArticleClicks(
            @PathVariable("id") long id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AccessHistory accessHistory = cookieManager.extractViewedMap(request);
        if (cookieManager.isCountIncreasable(id, accessHistory)) {
            articleCommandService.increaseClicksCount(id);
            cookieManager.createOrUpdateCookie(id, accessHistory, response);
        }
        return ResponseEntity.ok().build();
    }
}