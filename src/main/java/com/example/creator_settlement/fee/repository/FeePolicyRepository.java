package com.example.creator_settlement.fee.repository;

import com.example.creator_settlement.fee.entity.FeePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FeePolicyRepository extends JpaRepository<FeePolicy, String> {
//     effective_from <= 날짜
//     AND
//     effective_to >= 날짜
    Optional<FeePolicy> findFirstByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            LocalDate targetDate,
            LocalDate targetDate2
    );
}
