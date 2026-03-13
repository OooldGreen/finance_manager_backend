package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.GoalDTO;
import com.oooldgreen.financemanager.dto.GoalSummaryDTO;
import com.oooldgreen.financemanager.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getAllGoals() {
        return ResponseEntity.ok(goalService.getAllGoals());
    }

    @GetMapping("/summary")
    public ResponseEntity<GoalSummaryDTO> getSummary() {
        return ResponseEntity.ok(goalService.getSummary());
    }

    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@RequestBody GoalDTO goal) {
        return ResponseEntity.ok(goalService.createGoal(goal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable Long id, @RequestBody GoalDTO goal) throws AccessDeniedException {
        return ResponseEntity.ok(goalService.updateGoal(id, goal));
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
