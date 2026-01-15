package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.TotalBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalBalanceRepository extends JpaRepository<TotalBalance, Long> {
    Optional<TotalBalance> findByUserId(Long id);
}
