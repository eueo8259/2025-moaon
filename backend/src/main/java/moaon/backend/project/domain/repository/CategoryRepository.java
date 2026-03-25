package moaon.backend.project.domain.repository;

import java.util.List;
import java.util.Optional;
import moaon.backend.project.domain.Category;

public interface CategoryRepository {

    Optional<Category> findByName(String name);

    Category save(Category category);

    List<Category> saveAll(List<Category> categories);
}
