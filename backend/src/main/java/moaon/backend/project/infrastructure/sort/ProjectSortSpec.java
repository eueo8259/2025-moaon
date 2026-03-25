package moaon.backend.project.infrastructure.sort;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import moaon.backend.global.cursor.CursorToken;
import moaon.backend.project.domain.Project;
import moaon.backend.project.domain.ProjectSortType;

/**
 * Defines Querydsl sorting and cursor behavior for a single project sort type.
 */
public interface ProjectSortSpec {

    ProjectSortType getType();

    OrderSpecifier<?>[] getOrderSpecifiers();

    BooleanExpression getCursorPredicate(CursorToken token);

    String createNextCursor(Project project);
}
