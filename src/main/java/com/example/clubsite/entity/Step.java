package com.example.clubsite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@Getter
public class Step extends CreateDateEntity {
    public Step() {
        this.stepCount = 0L;
    }

    @Id
    @Column(name = "step_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    private Long stepCount = 0L;

    public void changeStepCount(Long stepCount) {
        this.stepCount = stepCount;
    }
}


