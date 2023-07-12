package com.example.clubsite.service.clubmember;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.clubmember.ClubMemberWithRankDTO;
import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.dto.request.LoginRequest;
import com.example.clubsite.dto.request.ReissueRequest;
import com.example.clubsite.dto.request.test.TestLoginRequest;
import com.example.clubsite.dto.request.UpdateMemberProfileRequest;
import com.example.clubsite.dto.response.LoginResponse;
import com.example.clubsite.dto.response.MemberProfileResponse;
import com.example.clubsite.dto.response.UploadMemberImageResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.enumType.ClubMemberRole;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClubMemberService {
    LoginResponse loginOrSignUpGoogle(LoginRequest loginRequest);
    LoginResponse testLoginOrSignUpById(TestLoginRequest testLoginRequest);

    void save(ClubMember clubMember);

    void createStepIfNotExist(ClubMember clubMember);
    void removeByEmail(String email);

    ClubMember saveByDto(ClubMemberDTO clubMemberDTO);

    void memberProfileUpdate(MemberAuthDTO memberAuthDTO, UpdateMemberProfileRequest updateMemberProfileRequest);

    void updateRefreshToken(ClubMember clubMember, String newRefreshToken);

    Optional<ClubMember> getByEmail(String email);

    @NotNull
    UploadMemberImageResponse uploadProfileImage(MemberAuthDTO memberAuthDTO, MultipartFile profileImageFile);

    ClubMember getClubMemberWithStepAndUserTokenByEmail(String email);

    ClubMember getClubMemberWithStepById(Long id);

    ClubMember getByIdWithUserToken(Long id);

    ClubMember getById(Long id);

    List<ClubMember> getMembersEntitiesByIds(List<Long> memberIds);

    void updateFcmToken(Long id, String FcmToken);

    Token memberReissue(ReissueRequest reissueRequest);

    void updateStep(Long id, Long stepCount);

    List<ClubMemberDTO> clubMembersToClubMemberDtoS(List<ClubMember> result);

    List<ClubMemberWithRankDTO> clubMembersToRankDtoS(List<ClubMember> result);

    MemberProfileResponse getMemberProfileResponse(Long id);

    ClubMemberDTO getDtoById(Long id);

    void remove(Long id);

    void modify(ClubMemberDTO clubMemberDTO);

    ClubMemberDTO getdtoByEmail(String email);

    default ClubMember dtoToEntity(ClubMemberDTO clubMemberDTO) {//profileName은 제외..!
        if (clubMemberDTO == null) return null;
        ClubMember clubMember = ClubMember.builder()
                .email(clubMemberDTO.getEmail())
                .name(clubMemberDTO.getName())
                // .profileFileName(DEFAULT_IMAGE)//clubMemberDTO.getProfileImageFileUrl()
                .build();
        clubMember.addMemberRole(ClubMemberRole.USER);// 회원가입한 멤버는 기본 유저로 역할 설정
        return clubMember;
    }
}
