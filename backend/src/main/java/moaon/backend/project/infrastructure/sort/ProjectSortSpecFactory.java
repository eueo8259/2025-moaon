package moaon.backend.project.infrastructure.sort;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import moaon.backend.global.exception.custom.CustomException;
import moaon.backend.global.exception.custom.ErrorCode;
import moaon.backend.project.domain.ProjectSortType;
import org.springframework.stereotype.Component;

@Component
public class ProjectSortSpecFactory {

    private final Map<ProjectSortType, ProjectSortSpec> sortSpecs;

    public ProjectSortSpecFactory(List<ProjectSortSpec> sortSpecs) {
        this.sortSpecs = new EnumMap<>(ProjectSortType.class);
        for (ProjectSortSpec sortSpec : sortSpecs) {
            this.sortSpecs.put(sortSpec.getType(), sortSpec);
        }
    }

    public ProjectSortSpec get(ProjectSortType sortType) {
        ProjectSortSpec sortSpec = sortSpecs.get(sortType);
        if (sortSpec == null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return sortSpec;
    }
}
