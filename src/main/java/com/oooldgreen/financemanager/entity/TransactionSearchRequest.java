package com.oooldgreen.financemanager.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class TransactionSearchRequest {
    private String keyword;
    private String startDate;
    private String endDate;
    private String type;
    private List<String> status = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> selections = new ArrayList<>();
    private int page = 0;
    private int size = 10;
    private String sortKey = "ticketCompletionDate";
    private String sortDirection = "DESC";

    public LocalDateTime getStartDateTime() {
        if (startDate == null || status.isEmpty()) return null;
        return LocalDate.parse(startDate).atStartOfDay();
    }

    public LocalDateTime getEndDateTime() {
        if (endDate == null || status.isEmpty()) return null;
        return LocalDate.parse(endDate).atTime(LocalTime.MAX);
    }

    public TransactionType getEnumType() {
        if (type == null) return null;
        return TransactionType.valueOf(type);
    }

    public List<TransactionStatus> getEnumStatus() {
        if (status == null || status.isEmpty()) return null;
        return status.stream().map(TransactionStatus::valueOf).toList();
    }

    public List<TransactionCategory> getEnumCategories() {
        if (categories == null || categories.isEmpty()) return null;
        return categories.stream().map(TransactionCategory::valueOf).toList();
    }

    public List<Long> getAccountIds() {
        if (selections == null || selections.isEmpty()) return Collections.emptyList();
        return selections.stream()
                .filter(selection -> selection.startsWith("acc_"))
                .map(selection -> Long.parseLong(selection.replace("acc_", "")))
                .toList();
    }

    public List<Long> getTagIds() {
        if (selections == null || selections.isEmpty()) return Collections.emptyList();
        return selections.stream()
                .filter(selection -> selection.startsWith("tag_"))
                .map(selection -> Long.parseLong(selection.replace("tag_", "")))
                .toList();
    }
}
