package project.gymnawa.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.auth.oauth.domain.CustomOAuth2UserDetails;
import project.gymnawa.domain.member.dto.MemberHomeInfoDto;
import project.gymnawa.domain.member.dto.MemberOauthInfoDto;
import project.gymnawa.domain.member.entity.Member;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;
import project.gymnawa.domain.normember.dto.MemberSaveDto;
import project.gymnawa.domain.normember.service.NorMemberService;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.trainer.service.TrainerService;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final NorMemberService norMemberService;
    private final TrainerService trainerService;

    /**
     * 홈 화면용 DTO 반환
     */
    public MemberHomeInfoDto getMemberInfo(Long id) {
        Member member = memberRepository.findById(id) // 그냥 같은 서비스 내 함수 호출로 변경
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        String name = member.getName();
        boolean isTrainer = member instanceof Trainer;

        return MemberHomeInfoDto.builder()
                .id(id)
                .name(name)
                .trainer(isTrainer)
                .build();
    }

    /**
     * 게스트 회원 일반 회원 or 트레이너 회원으로 승격
     */
    public Long convertGuestToMember(Long id, MemberOauthInfoDto memberOauthInfoDto) {
        Member guestMember = findOne(id);
        deleteOne(id);

        if (memberOauthInfoDto.getIsTrainer()) {
            TrainerSaveDto trainerSaveDto = toTrainerSaveDto(guestMember, memberOauthInfoDto);
            return trainerService.join(trainerSaveDto);
        } else {
            MemberSaveDto memberSaveDto = toMemberSaveDto(guestMember, memberOauthInfoDto);
            return norMemberService.join(memberSaveDto);
        }
    }

    /**
     * 회원 조회
     */
    public Member findOne(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return member;
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return member;
    }

    /**
     * 임시 회원 정보 삭제
     * @Transactional 어노테이션이 붙지 않아도 삭제가 되었던 이유
     * deleteById()는 Spring Data JPA의 메서드를 사용하는 건데, 해당 메서드를 가보면 @Transactional 어노테이션이 붙어있다.
     * 그치만 명시적으로 붙여주자.
     */
    @Transactional
    public void deleteOne(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * 비밀번호 검증
     */
    public boolean verifyPassword(String rawPw, String encodedPw) {
        return bCryptPasswordEncoder.matches(rawPw, encodedPw);
    }

    /**
     * 회원 탈퇴 처리
     */
    @Transactional
    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        member.deactivate();
    }

    private MemberSaveDto toMemberSaveDto(Member guestMember, MemberOauthInfoDto memberOauthInfoDto) {
        return MemberSaveDto.builder()
                .name(guestMember.getName())
                .email(guestMember.getEmail())
                .loginType(guestMember.getLoginType())
                .provider(guestMember.getProvider())
                .providerId(guestMember.getProviderId())
                .gender(memberOauthInfoDto.getGender())
                .zoneCode(memberOauthInfoDto.getZoneCode())
                .address(memberOauthInfoDto.getAddress())
                .detailAddress(memberOauthInfoDto.getDetailAddress())
                .buildingName(memberOauthInfoDto.getBuildingName())
                .build();
    }

    private TrainerSaveDto toTrainerSaveDto(Member guestMember, MemberOauthInfoDto memberOauthInfoDto) {
        return TrainerSaveDto.builder()
                .name(guestMember.getName())
                .email(guestMember.getEmail())
                .loginType(guestMember.getLoginType())
                .provider(guestMember.getProvider())
                .providerId(guestMember.getProviderId())
                .gender(memberOauthInfoDto.getGender())
                .zoneCode(memberOauthInfoDto.getZoneCode())
                .address(memberOauthInfoDto.getAddress())
                .detailAddress(memberOauthInfoDto.getDetailAddress())
                .buildingName(memberOauthInfoDto.getBuildingName())
                .build();
    }
}
