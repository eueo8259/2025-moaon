package moaon.backend.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProjectSortTypeTest {

    @ParameterizedTest
    @CsvSource({"createdAt,CREATED_AT", "views,VIEWS", "loves,LOVES", "articleCount,ARTICLE_COUNT", "createdat,CREATED_AT", ",CREATED_AT"})
    void from(String sortType, ProjectSortType expected) {
        ProjectSortType actual = ProjectSortType.from(sortType);

        assertThat(actual).isEqualTo(expected);
    }
}
