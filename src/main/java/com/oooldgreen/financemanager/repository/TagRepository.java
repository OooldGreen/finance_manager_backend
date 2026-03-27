package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByUserIdAndName(Long userId, String name);
    List<Tag> findAllByUserId(Long userId);

    @Query("SELECT t FROM Transaction tr " +
            "JOIN tr.tags t " +
            "WHERE tr.user.id = :userId " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(tr) DESC")
    List<Tag> findTopUsedTags(Long userId, Pageable pageable);
}
