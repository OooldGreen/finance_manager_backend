package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.GoalDTO;
import com.oooldgreen.financemanager.dto.GoalSummaryDTO;
import com.oooldgreen.financemanager.entity.GoalCategory;
import com.oooldgreen.financemanager.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<Page<GoalDTO>> getAllGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(goalService.getAllGoals(page, size));
    }

    @GetMapping("/summary")
    public ResponseEntity<GoalSummaryDTO> getSummary() {
        return ResponseEntity.ok(goalService.getSummary());
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return Arrays.stream(GoalCategory.values()).map(Enum::name).toList();
    }

    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@RequestBody GoalDTO goal) {
        return ResponseEntity.ok(goalService.createGoal(goal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable Long id, @RequestBody GoalDTO goal) throws AccessDeniedException {
        return ResponseEntity.ok(goalService.updateGoal(id, goal));
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<GoalDTO> updateAmount(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) throws AccessDeniedException, IllegalAccessException {
        return ResponseEntity.ok(goalService.updateAmount(id, payload.get("amount")));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<GoalDTO> updatePriority(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(goalService.updatePriority(id));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<GoalDTO> updateActive(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(goalService.updateActive(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) throws AccessDeniedException {
        goalService.deleteGoalById(id);
        return ResponseEntity.noContent().build();
    }
}
