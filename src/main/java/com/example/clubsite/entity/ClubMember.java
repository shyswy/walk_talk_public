package com.example.clubsite.entity;

import com.example.clubsite.enumType.ClubMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClubMember extends BaseEntity {
    public ClubMember(UserToken userToken, Step step) {
        this.userToken = userToken;
        this.step = step;
        this.step.changeStepCount(0L);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_member_id")
    Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private UserToken userToken;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clubMember")
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();
    @Column(unique = true, name = "email")
    private String email;
    @Column(nullable = true)
    private String password;
    private String name;
    private boolean fromSocial;
    private String profileFileName;
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ClubMemberRole> roleSet = new HashSet<>();

    public void addMemberRole(ClubMemberRole clubMemberRole) {
        roleSet.clear();
        roleSet.add(clubMemberRole);
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public void setStep(Step stepCoint) {
        this.step = stepCoint;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeProfileFileName(String profileFileName) {
        this.profileFileName = profileFileName;
    }
    public void setId(Long id){this.id=id;}
}

