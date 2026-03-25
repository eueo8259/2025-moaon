package moaon.backend.article.repository.db;

import java.util.List;
import java.util.stream.Stream;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.ArticleClicks;
import moaon.backend.article.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleDBRepository extends JpaRepository<Article, Long>, CustomizedArticleRepository {

    Long countByProjectIdAndSector(long id, Sector sector);

    @Query("select a.id from Article a where a.project.id = :projectId")
    List<Long> findIdsByProjectId(@Param("projectId") long projectId);

    @Query("select a from Article a")
    Stream<Article> streamAll();

    @Modifying
    @Query("UPDATE Article a SET a.clicks = a.clicks + 1 WHERE a.id = :id")
    void incrementClickCount(@Param("id") Long id);

    @Query("SELECT new moaon.backend.article.domain.ArticleClicks(a.id, a.clicks) FROM Article a")
    List<ArticleClicks> findAllClicks();
}
