package com.cms.repository;

import com.cms.entity.Project;
import org.springframework.data.jpa.repository.*;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @EntityGraph(attributePaths = {"projectItems", "projectItems.item", "projectItems.item.category", "payments", "deliveries"})
    java.util.Optional<Project> findDetailedById(Long id);
}
