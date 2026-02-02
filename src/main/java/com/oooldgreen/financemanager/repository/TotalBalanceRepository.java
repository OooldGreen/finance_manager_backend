package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.TotalBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalBalanceRepository extends JpaRepository<TotalBalance, Long> {
    Optional<TotalBalance> findByUserId(Long id);

    @Modifying
    @Query("DELETE FROM TotalBalance t WHERE t.user.id=:uid")
    void deleteByUserId(@Param("uid") Long userId);
}
