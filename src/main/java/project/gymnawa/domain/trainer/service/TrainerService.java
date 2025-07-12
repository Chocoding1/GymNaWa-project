package project.gymnawa.domain.trainer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.member.dto.UpdatePasswordDto;
import project.gymnawa.domain.trainer.dto.TrainerEditDto;
import project.gymnawa.domain.trainer.dto.TrainerSaveDto;
import project.gymnawa.domain.trainer.entity.Trainer;
import project.gymnawa.domain.trainer.repository.TrainerRepository;
import project.gymnawa.domain.common.etcfield.Address;
import project.gymnawa.domain.member.entity.etcfield.Role;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.member.repository.MemberRepository;

import java.util.List;

import static project.gymnawa.domain.common.error.dto.ErrorCode.*;

@Service
// 서비스에서 DB에 여러 번 접근할 수 있기 때문에 서비스 단에서 한 번에 쿼리를 처리하기 위해 서비스 단에 트랜잭션을 달아준다.
// 디비에 여러번 접근하면서 하나의 작업을 수행하는 서비스 계층 메서드에 주로 사용 (출처: https://swmobenz.tistory.com/34 [DevYGwan:티스토리])
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(TrainerSaveDto trainerSaveDto) {
        validateDuplicateTrainer(trainerSaveDto); // 중복 회원가입 방지

        if (trainerSaveDto.getLoginType() == null) {
            // 비밀번호 암호화
            trainerSaveDto.setPassword(bCryptPasswordEncoder.encode(trainerSaveDto.getPassword()));

            trainerSaveDto.setLoginType("normal");
        }
        trainerSaveDto.setRole(Role.USER);

        Trainer joinedTrainer = trainerRepository.save(trainerSaveDto.toEntity());
        return joinedTrainer.getId();
    }

    /**
     * 이메일 중복 체크
     */
    private void validateDuplicateTrainer(TrainerSaveDto trainerSaveDto) {
        if (memberRepository.existsByEmailAndDeletedFalse(trainerSaveDto.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }

    /**
     * 트레이너 단건 조회
     */
    public Trainer findOne(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (trainer.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        return trainer;
    }

    /**
     * 이름으로 검색
     */
    public List<Trainer> findByName(String name) {
        return trainerRepository.findByName(name);
    }

    /**
     * 트레이너 목록
     */
    public List<Trainer> findTrainers() {
        return trainerRepository.findAll();
    }

    /**
     * 트레이너 정보 수정
     */
    @Transactional
    public void updateTrainer(Long id, TrainerEditDto trainerEditDto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (trainer.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        String name = trainerEditDto.getName();
        Address address = new Address(trainerEditDto.getZoneCode(), trainerEditDto.getAddress(), trainerEditDto.getDetailAddress(), trainerEditDto.getBuildingName());

        trainer.updateInfo(name, address);
    }

    /**
     * 비밀번호 수정
     */
    @Transactional
    public void changePassword(Long id, UpdatePasswordDto updatePasswordDto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (trainer.isDeleted()) {
            throw new CustomException(DEACTIVATE_MEMBER);
        }

        // 현재 비밀번호 일치 확인
        if (!bCryptPasswordEncoder.matches(updatePasswordDto.getCurrentPassword(), trainer.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        // 새 비밀번호 일치 확인
        if (!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new CustomException(INVALID_NEW_PASSWORD);
        }

        String newPassword = bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword());
        trainer.changePassword(newPassword);
    }
}
