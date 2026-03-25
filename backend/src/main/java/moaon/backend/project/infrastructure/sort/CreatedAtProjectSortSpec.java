package moaon.backend.project.infrastructure.sort;

import static moaon.backend.project.domain.QProject.project;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import moaon.backend.global.cursor.CursorCodec;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectSortType;
import org.springframework.stereotype.Component;

/**
 * Sorting policy for project creation time based pagination.
 */
@Component
public class CreatedAtProjectSortSpec implements ProjectSortSpec {

    @Override
    public ProjectSortType getType() {
        return ProjectSortType.CREATED_AT;
    }

    @Override
    public OrderSpecifier<?>[] getOrderSpecifiers() {
        return new OrderSpecifier<?>[]{project.createdAt.desc(), project.id.desc()};
    }

    @Override
    public BooleanExpression getCursorPredicate(CursorToken token) {
        if (token == null) {
            return null;
        }

        LocalDateTime createdAt = parseSortValue(token);
        return project.createdAt.lt(createdAt)
                .or(project.createdAt.eq(createdAt).and(project.id.lt(token.lastId())));
    }

    @Override
    public String createNextCursor(Project project) {
        return CursorCodec.encode(new CursorToken(project.getCreatedAt().toString(), project.getId()));
    }

    private LocalDateTime parseSortValue(CursorToken token) {
        try {
            return LocalDateTime.parse(token.sortValue());
        } catch (RuntimeException exception) {
            throw new CustomException(ErrorCode.INVALID_CURSOR_FORMAT);
        }
    }
}
