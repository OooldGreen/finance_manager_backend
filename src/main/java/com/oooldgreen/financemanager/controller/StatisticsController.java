package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.HeatmapProjection;
import com.oooldgreen.financemanager.dto.StatisticsAllDTO;
import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.dto.StatisticsKpiDTO;
import com.oooldgreen.financemanager.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticService statisticService;

    @GetMapping
    private ResponseEntity<List<StatisticsAllDTO>> getData(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticService.getData(startDate, endDate));
    }

    @GetMapping("/category")
    private ResponseEntity<List<StatisticsDTO>> getDataByCatAndType(
            @RequestParam String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ){
        return ResponseEntity.ok(statisticService.getDataByCatAndType(type, startDate, endDate));
    }

    @GetMapping("/kpi")
    private ResponseEntity<StatisticsKpiDTO> getKpiData() {
        return ResponseEntity.ok(statisticService.getKpiData());
    }

    @GetMapping("/heatmap")
    private ResponseEntity<List<HeatmapProjection>> getHeatmapData(int year) {
        return ResponseEntity.ok((statisticService.getHeatmapData(year)));
    }
}
