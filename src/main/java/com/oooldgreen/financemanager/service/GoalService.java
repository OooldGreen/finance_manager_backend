package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.GoalDTO;
import com.oooldgreen.financemanager.dto.GoalSummaryDTO;
import com.oooldgreen.financemanager.entity.Goal;
import com.oooldgreen.financemanager.entity.GoalCategory;
import com.oooldgreen.financemanager.entity.GoalStatus;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.mapper.GoalMapper;
import com.oooldgreen.financemanager.repository.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private GoalRepository goalRepository;
    private GoalMapper goalMapper;
    private UserService userService;

    @Transactional
    public List<GoalDTO> getAllGoals() {
        Long userId = userService.getCurrentAuthUser().getId();
        return goalRepository.findAllByUserIdOrderByIsPriorityDescIsActiveDescUpdatedAtDesc(userId)
                .stream()
                .map(goal -> goalMapper.toDTO(goal))
                .toList();
    }

    @Transactional
    public GoalSummaryDTO getSummary() {
        Long userId = userService.getCurrentAuthUser().getId();
        int total = goalRepository.countByUserId(userId);
        int achieved = goalRepository.countByUserIdAndStatus(userId, GoalStatus.ACHIEVED);

        GoalSummaryDTO summary = new GoalSummaryDTO();
        summary.setTotalGoals(total);
        summary.setAchievedGoals(achieved);
        return summary;
    }

    @Transactional
    public GoalDTO createGoal(GoalDTO goalDetail) {
        User user = userService.getCurrentAuthUser();
        Goal goal = goalMapper.toEntity(goalDetail, user, true, false);
        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Transactional
    public GoalDTO updateGoal(Long goalId, GoalDTO goalDetail) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Goal goal = findAndVerifyGoal(goalId, user.getId());
        goal.setTitle(goalDetail.getTitle());
        goal.setDescription(goalDetail.getDescription());
        goal.setTargetAmount(goalDetail.getTargetAmount());
        goal.setCurrentAmount(goalDetail.getCurrentAmount());
        goal.setCategory(GoalCategory.valueOf(goalDetail.getCategory().toUpperCase()));
        goal.setIsActive(goalDetail.isActive());
        goal.setIsPriority(goalDetail.isPriority());

        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Transactional
    public GoalDTO updatePriority(Long goalId) throws AccessDeniedException {
        Long userId = userService.getCurrentAuthUser().getId();
        Goal goal = findAndVerifyGoal(goalId, userId);
        goal.setIsPriority(!goal.getIsPriority());
        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Transactional
    public GoalDTO updateActive(Long goalId) throws AccessDeniedException {
        Long userId = userService.getCurrentAuthUser().getId();
        Goal goal = findAndVerifyGoal(goalId, userId);
        goal.setIsActive(!goal.getIsActive());
        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoalById(Long goalId) throws AccessDeniedException {
        Long userId = userService.getCurrentAuthUser().getId();
        Goal goal = findAndVerifyGoal(goalId, userId);
        goalRepository.delete(goal);
    }

    private Goal findAndVerifyGoal(Long goalId, Long userId) throws AccessDeniedException {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new RuntimeException("Goal does not exist."));
        if (!goal.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied.");
        }
        return goal;
    }
}
