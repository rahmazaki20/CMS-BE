package com.cms.repository;

import com.cms.entity.ProjectItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, Long> {
    List<ProjectItem> findByProjectId(Long projectId);

    Optional<ProjectItem> findByIdAndProjectId(Long id, Long projectId);
}
