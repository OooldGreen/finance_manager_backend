package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.BalanceMode;
import com.oooldgreen.financemanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceModeRepository extends JpaRepository<BalanceMode, Long> {
}
