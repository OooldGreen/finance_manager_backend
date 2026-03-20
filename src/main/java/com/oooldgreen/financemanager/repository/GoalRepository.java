package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.Goal;
import com.oooldgreen.financemanager.entity.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    Page<Goal> findAllByUserId(Long userId, Pageable pageable);

    // get summary
    Integer countByUserId(Long userId);
    Integer countByUserIdAndStatus(Long userId, GoalStatus status);

}
