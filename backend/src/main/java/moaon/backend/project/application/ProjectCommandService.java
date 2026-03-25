package moaon.backend.project.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.member.application.MemberService;
import moaon.backend.member.domain.Member;
import moaon.backend.project.application.dto.ProjectCreateRequest;
import moaon.backend.project.application.dto.ProjectDetailResponse;
import moaon.backend.project.application.repository.ProjectQueryRepository;
import moaon.backend.project.domain.Images;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectCategory;
import moaon.backend.project.domain.ProjectTechStack;
import moaon.backend.category.domain.repository.CategoryRepository;
import moaon.backend.project.domain.repository.ProjectRepository;
import moaon.backend.techstack.domain.repository.TechStackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProjectCommandService {

    private final ProjectRepository projectRepository;
    private final ProjectQueryRepository projectQueryRepository;
    private final MemberService memberService;
    private final TechStackRepository techStackRepository;
    private final CategoryRepository categoryRepository;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.bucket}")
    private String bucket;

    public ProjectDetailResponse increaseViewsCount(long id) {
        projectRepository.increaseViewCountById(id);
        Project project = projectQueryRepository.findProjectWithMemberJoin(id);
        List<ProjectTechStack> stacks = projectQueryRepository.findProjectTechStacksByProjectId(id);
        List<ProjectCategory> categories = projectQueryRepository.findProjectCategoriesByProjectId(id);

        return ProjectDetailResponse.from(project, stacks, categories);
    }

    public Long save(String token, ProjectCreateRequest request) {
        Member member = memberService.getUserByToken(token);
        Project project = new Project(
                request.title(),
                request.summary(),
                request.description(),
                request.githubUrl(),
                request.productionUrl(),
                imagesFrom(request.imageKeys()),
                member,
                request.techStacks().stream()
                        .map(techStack -> techStackRepository.findByName(techStack)
                                .orElseThrow(() -> new CustomException(ErrorCode.TECHSTACK_NOT_FOUND)))
                        .toList(),
                request.categories().stream()
                        .map(category -> categoryRepository.findByName(category)
                                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)))
                        .toList(),
                LocalDateTime.now()
        );

        Project saved = projectRepository.save(project);
        return saved.getId();
    }

    private Images imagesFrom(List<String> imageKeys) {
        List<String> urls = imageKeys.stream()
                .map(k -> String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, k))
                .toList();
        return new Images(urls);
    }
}
