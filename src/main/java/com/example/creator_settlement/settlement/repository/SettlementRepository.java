package com.example.creator_settlement.settlement.repository;

import com.example.creator_settlement.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, String> {
    Optional<Settlement> findByCreatorIdAndMonth(String creatorId, String month);

    // 동일 기간 중복 정산 방지
    boolean existsByCreatorIdAndMonth(String creatorId, String month);
}
