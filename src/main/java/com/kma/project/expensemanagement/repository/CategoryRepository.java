package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Page<CategoryEntity> findAllByNameLikeIgnoreCaseAndParentIdAndCreatedBy(Pageable pageable, String search, Long parentId,
                                                                            Long createdBy);

    List<CategoryEntity> findAllByNameLikeIgnoreCaseAndCreatedByAndCategoryType(String search, Long createdBy, CategoryType type);

    @Query(value = " select c.id from CategoryEntity c where c.categoryType = :categoryType and c.createdBy = :userId ")
    List<Long> getAllCategoryId(@Param("categoryType") CategoryType categoryType,
                                @Param("userId") Long userId);
}
