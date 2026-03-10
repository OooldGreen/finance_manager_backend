package com.oooldgreen.financemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SoftDelete;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "budget", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "budget_date"})
})
@Data
public class Budget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_budget", nullable = false)
    private BigDecimal totalBudget = BigDecimal.ZERO;

    @Column(name = "currency")
    private String currency = "EUR";

    @Column(name = "budget_date", nullable = false)
    private LocalDate budgetDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ElementCollection
    @CollectionTable(name = "budget_categories", joinColumns = @JoinColumn(name = "budget_id"))
    private List<BudgetCategory> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}

