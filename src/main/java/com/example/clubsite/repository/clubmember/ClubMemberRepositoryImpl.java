package com.example.clubsite.repository.clubmember;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.entity.ClubMember;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.clubsite.entity.QClubMember.clubMember;
import static com.example.clubsite.entity.QStep.step;
import static com.example.clubsite.entity.QUserToken.userToken;

@Repository
@RequiredArgsConstructor
@Log4j2
public class ClubMemberRepositoryImpl implements ClubMemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ClubMember findByEmailWithStepAndUserToken(String email) {
        return queryFactory.select(clubMember)
                .from(clubMember)
                .leftJoin(clubMember.userToken, userToken).fetchJoin()
                .leftJoin(clubMember.step, step).fetchJoin()
                .where(clubMember.email.eq(email))
                .fetchOne();
    }

    public ClubMember findByIdWithUserToken(Long id) {
        return queryFactory.select(clubMember)
                .from(clubMember)
                .leftJoin(clubMember.userToken, userToken).fetchJoin()
                .where(clubMember.id.eq(id))
                .fetchOne();
    }

    public ClubMember findByIdWithStep(Long id) {
        return queryFactory.select(clubMember)
                .from(clubMember)
                .leftJoin(clubMember.step, step).fetchJoin()
                .where(clubMember.id.eq(id))
                .fetchOne();
    }

    @NotNull
    private static ConstructorExpression<ClubMemberDTO> constructEntityWithDTO() {
        return Projections.constructor(
                ClubMemberDTO.class,
                clubMember.id,
                clubMember.email,
                clubMember.name,
                userToken.refreshToken,
                step.stepCount
        );
    }
}
