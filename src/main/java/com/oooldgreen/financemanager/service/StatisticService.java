package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.StatisticsAllDTO;
import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.entity.TransactionStatus;
import com.oooldgreen.financemanager.entity.TransactionType;
import com.oooldgreen.financemanager.repository.StatisticsRepository;
import com.oooldgreen.financemanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UserService userService;
    private final StatisticsRepository statisticsRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<StatisticsAllDTO> getData(LocalDate startDate, LocalDate endDate) {
        Long userId = userService.getCurrentAuthUser().getId();
        long monthsRange = ChronoUnit.MONTHS.between(startDate, endDate);

        String dateFormat;
        if (monthsRange <= 12) {
            dateFormat = "YYYY-MM";
        } else {
            dateFormat = "YYYY";
            if (ChronoUnit.YEARS.between(startDate, endDate) > 12) {
                startDate = endDate.minusYears(12);
            }
        }
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : transactionRepository.findFirstTransactionDate(userId).orElse(LocalDateTime.now().minusYears(1));
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        List<StatisticsAllDTO> data = statisticsRepository.getData(userId, dateFormat, TransactionStatus.COMPLETED, start, end);
        if (monthsRange <= 12) {
            data = getYearlyData(start, end, data);
        }
        return data;
    }

    private List<StatisticsAllDTO> getYearlyData(LocalDateTime start, LocalDateTime end, List<StatisticsAllDTO> data) {
        Map<String, StatisticsAllDTO> dataMap = data.stream().collect(Collectors.toMap(StatisticsAllDTO::getName, d -> d));
        List<StatisticsAllDTO> fullYearData = new ArrayList<>();
        YearMonth from = YearMonth.from(start);
        YearMonth to = YearMonth.from(end);

        while (!from.isAfter(to)) {
            String month = from.toString();
            fullYearData.add(dataMap.getOrDefault(month, new StatisticsAllDTO(month, BigDecimal.ZERO, BigDecimal.ZERO)));
            from = from.plusMonths(1);
        }
        return fullYearData;
    }

    @Transactional
    public List<StatisticsDTO> getDataByCatAndType(String type, LocalDate startDate, LocalDate endDate) {
        Long userId = userService.getCurrentAuthUser().getId();
        TransactionType typeEnum = TransactionType.valueOf(type);
        return statisticsRepository.getDataByCatAndType(userId, typeEnum, TransactionStatus.COMPLETED, startDate, endDate);
    }
}
