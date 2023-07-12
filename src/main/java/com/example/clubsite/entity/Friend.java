package com.example.clubsite.entity;

import com.example.clubsite.enumType.FriendStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(
        name = "friend",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "follow_uk", //FK,UK 에 대해 자동으로 인덱스 생성되기에 별도로 생성하지 않는다. + Status는 수정 가능성이 높기에, index로 설정하지 않는다.
                        columnNames = {"from_member", "to_member"}  //from-> to 중복되지 않게.
                )
        }
        ,
        indexes = {
                @Index(name = "friend_index", columnList = "from_member, to_member"),
                @Index(name = "from_member", columnList = "from_member"),
                @Index(name = "from_member", columnList = "to_member")
        }
)
@NamedEntityGraph(
        name = "friend-with-members",
        attributeNodes = {
                @NamedAttributeNode("fromMember"),
                @NamedAttributeNode("toMember")
        }
)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member")
    @JsonIgnore
    private ClubMember fromMember;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member")
    @JsonIgnore
    private ClubMember toMember;
    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    public void changeStatus(FriendStatus status) {
        this.status = status;
    }
}
