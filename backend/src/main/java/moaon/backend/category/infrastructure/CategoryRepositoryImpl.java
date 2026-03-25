package moaon.backend.category.infrastructure;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import moaon.backend.category.domain.Category;
import moaon.backend.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByName(name);
    }

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public List<Category> saveAll(List<Category> categories) {
        return categoryJpaRepository.saveAll(categories);
    }
}
