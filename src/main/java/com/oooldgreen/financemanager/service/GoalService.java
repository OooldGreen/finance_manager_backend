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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;

    @Transactional
    public Page<GoalDTO> getAllGoals(int page, int size) {
        Long userId = userService.getCurrentAuthUser().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("isReached"),
                Sort.Order.desc("isActive"),
                Sort.Order.desc("isPriority"),
                Sort.Order.desc("createdAt")
        ));
        Page<Goal> goals = goalRepository.findAllByUserId(userId, pageable);
        return goals.map(goalMapper::toDTO);
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
        goal.setCurrency(goalDetail.getCurrency());
        goal.setTargetAmount(goalDetail.getTargetAmount());
        goal.setCurrentAmount(goalDetail.getCurrentAmount());
        goal.setCategory(GoalCategory.valueOf(goalDetail.getCategory().toUpperCase()));

        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Transactional
    public GoalDTO updateAmount(Long goalId, BigDecimal amount) throws AccessDeniedException, IllegalAccessException {
        Long userId = userService.getCurrentAuthUser().getId();
        Goal goal = findAndVerifyGoal(goalId, userId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalAccessException("Invalid amount. The amount is too small.");
        }

        BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount());

        if (amount.compareTo(remaining) > 0) {
            throw new IllegalAccessException("Invalid amount. The amount is too big.");
        }
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
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
