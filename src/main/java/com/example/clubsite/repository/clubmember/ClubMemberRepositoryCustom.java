package com.example.clubsite.repository.clubmember;

import com.example.clubsite.entity.ClubMember;

public interface ClubMemberRepositoryCustom {

    ClubMember findByEmailWithStepAndUserToken(String email);

    ClubMember findByIdWithUserToken(Long id);

    ClubMember findByIdWithStep(Long id);
}
