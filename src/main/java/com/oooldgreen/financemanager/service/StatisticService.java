package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.StatisticsAllDTO;
import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.dto.StatisticsKpiDTO;
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
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Transactional
    public StatisticsKpiDTO getKpiData() {
        Long userId = userService.getCurrentAuthUser().getId();
        // get date range of current month
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime startOfCur = current.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfCur = current.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // get date range of previous month
        LocalDateTime startOfPre = startOfCur.minusMonths(1);
        LocalDateTime endOfPre = startOfPre.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Object[]> currentRes = statisticsRepository.getMonthlyStats(userId, TransactionStatus.COMPLETED, startOfCur, endOfCur);
        List<Object[]> previousRes = statisticsRepository.getMonthlyStats(userId, TransactionStatus.COMPLETED, startOfPre, endOfPre);
        Object[] currentStats = currentRes.isEmpty() ? new Object[]{0.0, 0.0, 0.0} : currentRes.get(0);
        Object[] previousStats = previousRes.isEmpty() ? new Object[]{0.0, 0.0, 0.0} : previousRes.get(0);

        System.out.println("---- debug --- ");
        System.out.println("Warning: Array length is only " + (previousStats == null ? 0 : previousStats.length));
        System.out.println(previousStats[0]);
        System.out.println("current expense" + previousStats[1]);
        System.out.println("current top" + previousStats[2]);

        if (currentStats.length < 3 || currentStats[0] == null) {
            currentStats = new Object[]{0.0, 0.0, 0.0};
        }
        if(previousStats.length < 3 || previousStats[0] == null) {
            previousStats = new Object[]{0.0, 0.0, 0.0};
        }

        return new StatisticsKpiDTO(
                currentStats[0],  // current income
                currentStats[1],  // current expense
                previousStats[0], // previous income
                previousStats[1], // previous expense
                currentStats[2],  // current top expense
                previousStats[2]  // previous top expense
        );
    }
}
