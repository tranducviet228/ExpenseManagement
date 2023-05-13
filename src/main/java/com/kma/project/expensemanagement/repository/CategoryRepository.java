package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Page<CategoryEntity> findAllByNameLikeIgnoreCaseAndParentIdAndCreatedBy(Pageable pageable, String search, Long parentId,
                                                                            Long createdBy);

    List<CategoryEntity> findAllByNameLikeIgnoreCaseAndCreatedByAndCategoryType(String search, Long createdBy, CategoryType type);

    @Query(value = " select c.id from CategoryEntity c where c.categoryType = :categoryType and c.createdBy = :userId ")
    List<Long> getAllCategoryId(@Param("categoryType") CategoryType categoryType,
                                @Param("userId") Long userId);

    @Query(value = " select * from categories c " +
            " join users u on c.created_by = u.id " +
            " join user_role_map ur on u.id = ur.user_id " +
            " join roles ro on ur.role_id = ro.id " +
            " where ro.name = :role_name and c.parent_id = 0 ", nativeQuery = true)
    List<CategoryEntity> getAllCategoryCreateByAdmin(@Param("role_name") String roleName);

    List<CategoryEntity> findAllByParentIdIn(@Param("parentIds") Collection<Long> parentIds);
}
