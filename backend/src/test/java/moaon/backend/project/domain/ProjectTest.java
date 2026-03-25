package moaon.backend.project.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import moaon.backend.fixture.Fixture;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.member.domain.Member;
import moaon.backend.shared.domain.TechStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectTest {

    private final Member author = Fixture.anyMember();
    private final Images images = new Images(List.of("url"));
    private final List<TechStack> techStacks = List.of(new TechStack("Spring"));
    private final List<Category> categories = List.of(new Category("Web"));
    private final LocalDateTime now = LocalDateTime.now();

    @DisplayName("유효한 값이면 Project가 생성된다")
    @Test
    void validProject_isCreatedSuccessfully() {
        Project project = new Project(
                "모아온",
                "모아온은 개발자 포트폴리오 플랫폼입니다.",
                "개발자들이 프로젝트와 기술 아티클을 공유하고 모아 볼 수 있는 플랫폼입니다. 상세 설명은 충분히 길게 작성되어야 합니다.".repeat(10),
                "https://github.com/moaon",
                "https://moaon.site",
                images,
                author,
                techStacks,
                categories,
                now
        );

        assertThat(project).isNotNull();
        assertThat(project.getTitle()).isEqualTo("모아온");
    }

    @DisplayName("제목이 너무 짧으면 예외가 발생한다")
    @Test
    void invalidTitle_throwsException() {
        assertThatThrownBy(() -> new Project(
                "A",
                "프로젝트 요약입니다.".repeat(2),
                "길이가 충분한 설명입니다.".repeat(10),
                "", "", images, author, techStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_TITLE);
    }

    @DisplayName("요약이 10자 미만이면 예외가 발생한다")
    @Test
    void invalidSummary_short_throwsException() {
        assertThatThrownBy(() -> new Project(
                "모아온",
                "짧음",
                "내용".repeat(100),
                "", "", images, author, techStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_SUMMARY);
    }

    @DisplayName("요약이 50자를 넘기면 예외가 발생한다")
    @Test
    void invalidSummary_long_throwsException() {
        assertThatThrownBy(() -> new Project(
                "모아온",
                "길".repeat(51),
                "내용".repeat(100),
                "", "", images, author, techStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_SUMMARY);
    }

    @DisplayName("설명이 100자 미만이면 예외가 발생한다")
    @Test
    void invalidDescription_short_throwsException() {
        assertThatThrownBy(() -> new Project(
                "모아온",
                "프로젝트 요약입니다.".repeat(2),
                "짧음",
                "", "", images, author, techStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_DESCRIPTION);
    }

    @DisplayName("설명이 8000자를 넘기면 예외가 발생한다")
    @Test
    void invalidDescription_long_throwsException() {
        assertThatThrownBy(() -> new Project(
                "모아온",
                "프로젝트 요약입니다.".repeat(2),
                "길".repeat(8001),
                "", "", images, author, techStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_DESCRIPTION);
    }

    @DisplayName("기술 스택이 비어 있으면 예외가 발생한다")
    @Test
    void invalidTechStacks_throwsException() {
        List<TechStack> emptyTechStacks = new ArrayList<>();
        assertThatThrownBy(() -> new Project(
                "모아온",
                "프로젝트 요약입니다.".repeat(2),
                "내용".repeat(100),
                "", "", images, author, emptyTechStacks, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_TECHSTACK);
    }

    @DisplayName("기술 스택이 중복되면 예외가 발생한다")
    @Test
    void invalidTechStacks_duplicated_throwsException() {
        List<TechStack> duplicated = List.of(new TechStack("spring"), new TechStack("spring"));
        assertThatThrownBy(() -> new Project(
                "모아온",
                "프로젝트 요약입니다.".repeat(2),
                "내용".repeat(100),
                "", "", images, author, duplicated, categories, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_TECHSTACK);
    }

    @DisplayName("카테고리가 비어 있으면 예외가 발생한다")
    @Test
    void invalidCategories_empty_throwsException() {
        assertThatThrownBy(() -> new Project(
                "모아온",
                "요약 충분".repeat(5),
                "내용".repeat(100),
                "", "", images, author, techStacks, List.of(), now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_CATEGORY);
    }

    @DisplayName("카테고리가 중복되면 예외가 발생한다")
    @Test
    void invalidCategories_duplicate_throwsException() {
        List<Category> duplicated = List.of(new Category("web"), new Category("web"));
        assertThatThrownBy(() -> new Project(
                "모아온",
                "요약 충분".repeat(5),
                "내용".repeat(100),
                "", "", images, author, techStacks, duplicated, now
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PROJECT_INVALID_CATEGORY);
    }
}
