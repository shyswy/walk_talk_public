package com.example.clubsite.repository.clubmember;

import com.example.clubsite.entity.ClubMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long>, ClubMemberRepositoryCustom {
    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<ClubMember> findFirstByEmail(String email);

    List<ClubMember> findAllByIdIn(List<Long> userIds);
}
