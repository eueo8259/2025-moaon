package moaon.backend.article.repository.db;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaon.backend.article.application.dto.ArticleQueryCondition;
import moaon.backend.article.domain.Article;
import moaon.backend.article.domain.Sector;
import moaon.backend.article.domain.Topic;
import moaon.backend.article.infrastructure.dao.ArticleDao;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import moaon.backend.project.domain.Project;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomizedArticleRepositoryImpl implements CustomizedArticleRepository {

    private final ArticleDao articleDao;

    @Override
    public DBArticleSearchResult findWithSearchConditions(ArticleQueryCondition queryCondition) {
        FilteringIds filteringIds = FilteringIds.init();
        filteringIds = applyTechStackFilter(filteringIds, queryCondition.techStackNames());
        filteringIds = applyTopicFilter(filteringIds, queryCondition.topics());
        filteringIds = applySearchFilter(filteringIds, queryCondition.search());
        filteringIds = applySectorFilter(filteringIds, queryCondition.sector());

        if (filteringIds.hasEmptyResult()) {
            return DBArticleSearchResult.empty();
        }

        List<Article> articles = articleDao.findAllBy(
                filteringIds.getIds(),
                queryCondition.cursor(),
                queryCondition.limit(),
                queryCondition.sortType(),
                queryCondition.search()
        );
        long totalCount = calculateTotalCount(filteringIds);
        return new DBArticleSearchResult(articles, totalCount, queryCondition.limit(), queryCondition.sortType());
    }

    @Override
    public ArticleSearchResult findByProjectWithCondition(Project project, ProjectArticleQueryCondition condition) {
        List<Article> articles = findAllByProjectIdAndCondition(project.getId(), condition);
        return new DBArticleSearchResult(
                articles,
                articles.size(),
                articles.size(),
                condition.toArticleCondition().sortType()
        );
    }

    @Override
    public List<Article> findAllByProjectIdAndCondition(long id, ProjectArticleQueryCondition condition) {
        return articleDao.findAllBy(
                id,
                condition.sector(),
                condition.search()
        );
    }

    private FilteringIds applyTechStackFilter(FilteringIds filteringIds, List<String> techStackNames) {
        if (filteringIds.hasEmptyResult() || CollectionUtils.isEmpty(techStackNames)) {
            return filteringIds;
        }

        Set<Long> filterByTechstacks = articleDao.findIdsByTechStackNames(techStackNames);
        return filteringIds.addFilterResult(filterByTechstacks);
    }

    private FilteringIds applyTopicFilter(FilteringIds filteringIds, List<Topic> topics) {
        if (filteringIds.hasEmptyResult() || CollectionUtils.isEmpty(topics)) {
            return filteringIds;
        }

        Set<Long> filterByTopics = articleDao.findIdsByTopics(topics);
        return filteringIds.addFilterResult(filterByTopics);
    }

    private FilteringIds applySearchFilter(FilteringIds filteringIds, SearchKeyword search) {
        if (filteringIds.hasEmptyResult() || search == null || !search.hasValue()) {
            return filteringIds;
        }

        Set<Long> filterBySearch = articleDao.findIdsBySearchKeyword(search);
        return filteringIds.addFilterResult(filterBySearch);
    }

    private FilteringIds applySectorFilter(FilteringIds filteringIds, Sector sector) {
        if (filteringIds.hasEmptyResult() || sector == null) {
            return filteringIds;
        }

        Set<Long> filterBySector = articleDao.findIdsBySectorAndIds(sector, filteringIds.getIds());
        return FilteringIds.of(filterBySector);
    }

    private long calculateTotalCount(FilteringIds filteringIds) {
        if (filteringIds.isEmpty()) {
            return articleDao.count();
        }

        return filteringIds.size();
    }
}
