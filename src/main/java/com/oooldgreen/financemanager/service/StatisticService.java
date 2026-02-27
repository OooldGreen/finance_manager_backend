package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.entity.TransactionType;
import com.oooldgreen.financemanager.repository.StatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UserService userService;
    private final StatisticsRepository statisticsRepository;

    @Transactional
    public List<StatisticsDTO> getData(LocalDate startDate, LocalDate endDate) {
        Long userId = userService.getCurrentAuthUser().getId();
        return statisticsRepository.getDataByUserId(userId, startDate, endDate);
    }

    @Transactional
    public List<StatisticsDTO> getDataByCat(String type, LocalDate startDate, LocalDate endDate) {
        Long userId = userService.getCurrentAuthUser().getId();
        TransactionType typeEnum = TransactionType.valueOf(type);
        return statisticsRepository.getDataByCat(userId, typeEnum, startDate, endDate);
    }
}
