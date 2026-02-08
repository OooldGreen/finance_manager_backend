package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId AND a.balance > 0 AND a.isActive = true")
    BigDecimal getTotalAssetsByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId AND a.balance < 0 AND a.isActive = true")
    BigDecimal getTotalDebtsByUserId(@Param("userId") Long userId);
}
