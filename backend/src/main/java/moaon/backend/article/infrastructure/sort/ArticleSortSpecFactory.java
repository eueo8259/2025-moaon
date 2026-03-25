package moaon.backend.article.infrastructure.sort;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import moaon.backend.article.domain.ArticleSortType;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ArticleSortSpecFactory {

    private final Map<ArticleSortType, ArticleSortSpec> sortSpecs;

    public ArticleSortSpecFactory(List<ArticleSortSpec> sortSpecs) {
        this.sortSpecs = new EnumMap<>(ArticleSortType.class);
        for (ArticleSortSpec sortSpec : sortSpecs) {
            this.sortSpecs.put(sortSpec.getType(), sortSpec);
        }
    }

    public ArticleSortSpec get(ArticleSortType sortType) {
        ArticleSortSpec sortSpec = sortSpecs.get(sortType);
        if (sortSpec == null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return sortSpec;
    }
}
