package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.service.StatisticService;
import com.oooldgreen.financemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticService statisticService;

    @GetMapping
    private ResponseEntity<List<StatisticsDTO>> getData(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticService.getData(startDate, endDate));
    }

    @GetMapping("/category")
    private ResponseEntity<List<StatisticsDTO>> getDataByCat(
            @RequestParam String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ){
        LocalDate end = (endDate == null) ? startDate : endDate;
        return ResponseEntity.ok(statisticService.getDataByCat(type, startDate, end));
    }

}
