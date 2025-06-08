package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
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

    @Query("SELECT w FROM CategoryEntity w " +
            "LEFT JOIN GroupEntity g ON w.groupId = g.id " +
            "WHERE " +
            "(:parentId IS NULL OR w.parentId = :parentId) AND " +
            "(:search IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "((:groupId IS NOT NULL AND w.groupId = :groupId) OR " +
            "(:groupId IS NULL AND (w.createdBy = :createdBy OR w.groupId IS NOT NULL))) " +
            "ORDER BY w.createdAt")
    Page<CategoryEntity> findAllCombined(@Param("pageable") Pageable pageable,
                                         @Param("search") String search,
                                         @Param("parentId") Long parentId,
                                         @Param("createdBy") Long createdBy,
                                         @Param("groupId") Long groupId);

    @Query("SELECT w FROM CategoryEntity w " +
            "WHERE " +
            "(:type IS NULL OR w.categoryType = :type) AND " +
            "(:search IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "((:groupId IS NOT NULL AND w.groupId = :groupId) OR " +
            "(:groupId IS NULL AND (w.createdBy = :createdBy OR w.groupId IS NOT NULL)))")
    List<CategoryEntity> findAllFiltered(@Param("search") String search,
                                         @Param("createdBy") Long createdBy,
                                         @Param("type") CategoryType type,
                                         @Param("groupId") Long groupId);

    @Query(value = " select c.id from CategoryEntity c where c.categoryType = :categoryType")
    List<Long> getAllCategoryId(@Param("categoryType") CategoryType categoryType);

    @Query(value = " select * from categories c " +
            " join users u on c.created_by = u.id " +
            " join user_role_map ur on u.id = ur.user_id " +
            " join roles ro on ur.role_id = ro.id " +
            " where ro.name = :role_name and c.parent_id = 0 ", nativeQuery = true)
    List<CategoryEntity> getAllCategoryCreateByAdmin(@Param("role_name") String roleName);

    List<CategoryEntity> findAllByParentIdIn(@Param("parentIds") Collection<Long> parentIds);

    List<CategoryEntity> findAllByParentId(@Param("parentId") Long parentId);

}
