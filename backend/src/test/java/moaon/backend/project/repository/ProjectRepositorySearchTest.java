package moaon.backend.project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import moaon.backend.fixture.ProjectFixtureBuilder;
import moaon.backend.fixture.ProjectQueryConditionFixtureBuilder;
import moaon.backend.fixture.RepositoryHelper;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.config.QueryDslConfig;
import moaon.backend.project.application.repository.ProjectQueryRepository;
import moaon.backend.project.application.dto.ProjectQueryCondition;
import moaon.backend.project.domain.Project;
import moaon.backend.project.infrastructure.dao.ProjectDao;
import moaon.backend.project.infrastructure.sort.ProjectSortSpecFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Import({RepositoryHelper.class, QueryDslConfig.class, ProjectDao.class})
public class ProjectRepositorySearchTest {

    @Autowired
    private ProjectQueryRepository repository;

    @Autowired
    private RepositoryHelper repositoryHelper;

    @Autowired
    private ProjectSortSpecFactory projectSortSpecFactory;

    @DisplayName("search keyword returns matching projects")
    @Test
    void findWithSearch() {
        Project moaon = repositoryHelper.save(new ProjectFixtureBuilder()
                .title("모아온")
                .summary("여러 플랫폼에 흩어진 포트폴리오를 한 곳에 정리할 수 있는 서비스입니다.")
                .description("GitHub, Notion, Velog 등 다양한 출처의 링크를 카드 형태로 시각화해 보여줍니다.")
                .build()
        );
        Project share = repositoryHelper.save(new ProjectFixtureBuilder()
                .title("공유공유")
                .summary("개발자와 디자이너의 작업물을 쉽게 공유하고 검색할 수 있도록 돕습니다.")
                .description("사용자 맞춤 검색 기능과 태그 필터링을 통해 필요한 작업물을 빠르게 찾을 수 있습니다.")
                .build()
        );
        Project hub = repositoryHelper.save(new ProjectFixtureBuilder()
                .title("허브허브")
                .summary("링크 하나로 모든 프로젝트를 소개할 수 있는 포트폴리오 허브 플랫폼입니다.")
                .description("SNS 공유 최적화 및 모바일 반응형 UI를 제공하여 접근성과 확장성을 높였습니다.")
                .build()
        );

        List<Project> aboutMoaon = search(aboutSearchKeyword("모아"));
        List<Project> aboutShare = search(aboutSearchKeyword("공유"));
        List<Project> aboutHub = search(aboutSearchKeyword("허브"));

        assertAll(
                () -> assertThat(aboutMoaon).containsOnlyOnce(moaon),
                () -> assertThat(aboutShare).containsOnlyOnce(share),
                () -> assertThat(aboutHub).containsOnlyOnce(hub)
        );
    }

    @Test
    @DisplayName("search also matches words contained in content")
    void findWithSearch2() {
        Project cocktailPick = repositoryHelper.save(new ProjectFixtureBuilder()
                .title("칵테일픽")
                .summary("좋아하는 술을, 고민 없이")
                .description("칵테일 및 칵테일 바 정보를 제공하고 추천하는 서비스입니다. 좋아하는 술을, 고민 없이 라는 모토를 가지고 개발하고 있습니다.")
                .build());

        List<Project> aboutCocktail = search(aboutSearchKeyword("칵테일"));
        List<Project> aboutSooleul = search(aboutSearchKeyword("술을"));
        List<Project> aboutGomin = search(aboutSearchKeyword("고민"));

        assertAll(
                () -> assertThat(aboutCocktail).containsOnlyOnce(cocktailPick),
                () -> assertThat(aboutSooleul).containsOnlyOnce(cocktailPick),
                () -> assertThat(aboutGomin).containsOnlyOnce(cocktailPick)
        );
    }

    private ProjectQueryCondition aboutSearchKeyword(String keyword) {
        return new ProjectQueryConditionFixtureBuilder()
                .search(keyword)
                .build();
    }

    private List<Project> search(ProjectQueryCondition queryCondition) {
        return repository.findWithSearchConditions(
                queryCondition,
                projectSortSpecFactory.get(queryCondition.projectSortType()),
                CursorCodec.decode(queryCondition.cursor())
        ).getProjects();
    }
}
