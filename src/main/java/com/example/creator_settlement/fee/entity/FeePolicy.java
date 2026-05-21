package com.example.creator_settlement.fee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeePolicy {
    @Id
    private String id;

    private int feeRate;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
