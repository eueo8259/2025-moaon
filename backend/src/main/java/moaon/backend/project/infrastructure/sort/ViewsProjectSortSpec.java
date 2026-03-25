package moaon.backend.project.infrastructure.sort;

import static moaon.backend.project.domain.QProject.project;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectSortType;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for project view count based pagination.
 */
@Component
public class ViewsProjectSortSpec implements ProjectSortSpec {

    @Override
    public ProjectSortType getType() {
        return ProjectSortType.VIEWS;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers() {
        return new OrderSpecifier<?>[]{project.views.desc(), project.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token) {
        if (token == null) {
            return null;
        }

        Integer views = parseSortValue(token);
        return project.views.lt(views)
                .or(project.views.eq(views).and(project.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Project project) {
        return CursorCodec.encode(new CursorToken(String.valueOf(project.getViews()), project.getId()));
    }

    private Integer parseSortValue(CursorToken token) {
        try {
            return Integer.parseInt(token.sortValue());
        } catch (NumberFormatException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }
}
