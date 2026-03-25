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
import moaon.backend.article.infrastructure.sort.ArticleSortSpec;
import moaon.backend.article.infrastructure.sort.ArticleSortSpecFactory;
import moaon.backend.article.repository.ArticleSearchResult;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.domain.SearchKeyword;
import moaon.backend.global.query.FilteredIds;
import moaon.backend.project.application.dto.ProjectArticleQueryCondition;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomizedArticleRepositoryImpl implements CustomizedArticleRepository {

    private final ArticleDao articleDao;
    private final ArticleSortSpecFactory articleSortSpecFactory;

    public DBArticleSearchResult findWithSearchConditions(ArticleQueryCondition queryCondition) {
        ArticleSortSpec sortSpec = articleSortSpecFactory.get(queryCondition.sortType());
        CursorToken cursorToken = CursorCodec.decode(queryCondition.cursor());
        return findWithSearchConditions(queryCondition, sortSpec, cursorToken);
    }

    public ArticleSearchResult findByProjectWithCondition(long projectId, ProjectArticleQueryCondition condition) {
        ArticleQueryCondition articleQueryCondition = condition.toArticleCondition();
        ArticleSortSpec sortSpec = articleSortSpecFactory.get(articleQueryCondition.sortType());
        CursorToken cursorToken = CursorCodec.decode(articleQueryCondition.cursor());
        return findByProjectWithCondition(projectId, condition, sortSpec, cursorToken);
    }

    @Override
    public DBArticleSearchResult findWithSearchConditions(
            ArticleQueryCondition queryCondition,
            ArticleSortSpec sortSpec,
            CursorToken cursorToken
    ) {
        FilteredIds filteredIds = FilteredIds.init();
        filteredIds = applyTechStackFilter(filteredIds, queryCondition.techStackNames());
        filteredIds = applyTopicFilter(filteredIds, queryCondition.topics());
        filteredIds = applySearchFilter(filteredIds, queryCondition.search());
        filteredIds = applySectorFilter(filteredIds, queryCondition.sector());

        if (filteredIds.hasEmptyResult()) {
            return DBArticleSearchResult.empty();
        }

        List<Article> articles = articleDao.findAllBy(
                filteredIds.getIds(),
                cursorToken,
                queryCondition.limit(),
                sortSpec,
                queryCondition.search()
        );
        long totalCount = calculateTotalCount(filteredIds);
        return new DBArticleSearchResult(articles, totalCount, queryCondition.limit(), queryCondition.sortType(), sortSpec);
    }

    @Override
    public ArticleSearchResult findByProjectWithCondition(
            long projectId,
            ProjectArticleQueryCondition condition,
            ArticleSortSpec sortSpec,
            CursorToken cursorToken
    ) {
        List<Article> articles = findAllByProjectIdAndCondition(projectId, condition);
        return new DBArticleSearchResult(
                articles,
                articles.size(),
                articles.size(),
                condition.toArticleCondition().sortType(),
                sortSpec
        );
    }

    private List<Article> findAllByProjectIdAndCondition(long id, ProjectArticleQueryCondition condition) {
        return articleDao.findAllBy(
                id,
                condition.sector(),
                condition.search()
        );
    }

    private FilteredIds applyTechStackFilter(FilteredIds filteredIds, List<String> techStackNames) {
        if (filteredIds.hasEmptyResult() || CollectionUtils.isEmpty(techStackNames)) {
            return filteredIds;
        }

        Set<Long> filterByTechstacks = articleDao.findIdsByTechStackNames(techStackNames);
        return filteredIds.addFilterResult(filterByTechstacks);
    }

    private FilteredIds applyTopicFilter(FilteredIds filteredIds, List<Topic> topics) {
        if (filteredIds.hasEmptyResult() || CollectionUtils.isEmpty(topics)) {
            return filteredIds;
        }

        Set<Long> filterByTopics = articleDao.findIdsByTopics(topics);
        return filteredIds.addFilterResult(filterByTopics);
    }

    private FilteredIds applySearchFilter(FilteredIds filteredIds, SearchKeyword search) {
        if (filteredIds.hasEmptyResult() || search == null || !search.hasValue()) {
            return filteredIds;
        }

        Set<Long> filterBySearch = articleDao.findIdsBySearchKeyword(search);
        return filteredIds.addFilterResult(filterBySearch);
    }

    private FilteredIds applySectorFilter(FilteredIds filteredIds, Sector sector) {
        if (filteredIds.hasEmptyResult() || sector == null) {
            return filteredIds;
        }

        Set<Long> filterBySector = articleDao.findIdsBySectorAndIds(sector, filteredIds.getIds());
        return FilteredIds.of(filterBySector);
    }

    private long calculateTotalCount(FilteredIds filteredIds) {
        if (filteredIds.isEmpty()) {
            return articleDao.count();
        }

        return filteredIds.size();
    }
}
