package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.Goal;
import com.oooldgreen.financemanager.entity.GoalStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUserIdOrderByIsPriorityDescIsActiveDescUpdatedAtDesc(Long userId);

    // get summary
    Integer countByUserId(Long userId);
    Integer countByUserIdAndStatus(Long userId, GoalStatus status);

}
