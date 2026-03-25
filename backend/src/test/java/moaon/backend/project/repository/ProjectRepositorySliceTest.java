package moaon.backend.project.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import moaon.backend.article.infrastructure.dao.ArticleDao;
import moaon.backend.article.repository.db.CustomizedArticleRepositoryImpl;
import moaon.backend.fixture.RepositoryHelper;
import moaon.backend.global.config.QueryDslConfig;
import moaon.backend.category.infrastructure.CategoryRepositoryImpl;
import moaon.backend.project.infrastructure.ProjectRepositoryImpl;
import moaon.backend.project.infrastructure.dao.ProjectDao;
import moaon.backend.project.infrastructure.sort.ArticleCountProjectSortSpec;
import moaon.backend.project.infrastructure.sort.CreatedAtProjectSortSpec;
import moaon.backend.project.infrastructure.sort.LovesProjectSortSpec;
import moaon.backend.project.infrastructure.sort.ProjectSortSpecFactory;
import moaon.backend.project.infrastructure.sort.ViewsProjectSortSpec;
import moaon.backend.techstack.infrastructure.TechStackRepositoryImpl;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * Shared slice-test configuration for project persistence tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@Import({
        RepositoryHelper.class,
        QueryDslConfig.class,
        ArticleDao.class,
        CustomizedArticleRepositoryImpl.class,
        ProjectDao.class,
        ProjectRepositoryImpl.class,
        CategoryRepositoryImpl.class,
        TechStackRepositoryImpl.class,
        ProjectSortSpecFactory.class,
        CreatedAtProjectSortSpec.class,
        ViewsProjectSortSpec.class,
        LovesProjectSortSpec.class,
        ArticleCountProjectSortSpec.class
})
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never"
})
public @interface ProjectRepositorySliceTest {
}
