package com.oooldgreen.financemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "financial_goals")
@Data
public class Goal extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "currency")
    private String currency = "EUR";

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "current_amount")
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Formula("current_amount >= target_amount")
    private Boolean isReached;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GoalCategory category = GoalCategory.OTHERS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Column(name = "is_priority")
    private Boolean isPriority;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public double getPercent () {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount
                .divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue();
    }

    public void setCurrentAmount(BigDecimal amount) {
        this.currentAmount = amount;
        if (this.targetAmount != null) {
            if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
                this.status = GoalStatus.ACHIEVED;
            } else if (this.currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                this.status = GoalStatus.IN_PROGRESS;
            } else {
                this.status = GoalStatus.NOT_STARTED;
            }
        }
    }
}

