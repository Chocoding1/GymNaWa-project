package project.gymnawa.gymtrainer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.etcfield.ContractStatus;
import project.gymnawa.gymtrainer.entity.GymTrainer;
import project.gymnawa.trainer.entity.Trainer;
import project.gymnawa.errors.exception.CustomException;
import project.gymnawa.gymtrainer.repository.GymTrainerRepository;

import java.time.LocalDate;
import java.util.List;

import static project.gymnawa.errors.dto.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymTrainerService {

    private final GymTrainerRepository gymTrainerRepository;

    /**
     * 트레이너 계약 정보 저장
     */
    @Transactional
    public Long save(GymTrainer gymTrainer) {
        gymTrainerRepository.save(gymTrainer);
        return gymTrainer.getId();
    }

    /**
     * 특정 트레이너의 계약 정보 조회
     */
    public List<GymTrainer> findByTrainer(Trainer trainer) {
        return gymTrainerRepository.findByTrainer(trainer);
    }

    /**
     * 헬스장과 계약 상태 별 계약 정보 조회
     * 특정 헬스장에 근무하는 트레이너 조회용 메서드
     */
    public List<GymTrainer> findByGymAndContractStatus(String gymId, ContractStatus contractStatus) {
        return gymTrainerRepository.findByGymIdAndContractStatus(gymId, contractStatus);
    }

    /**
     * 특정 헬스장에 특정 트레이너가 계약 상태인지 조회
     */
    public List<GymTrainer> findByGymIdAndTrainerAndContractStatus(String gymId, Trainer trainer, ContractStatus contractStatus) {
        return gymTrainerRepository.findByGymIdAndTrainerAndContractStatus(gymId, trainer, contractStatus);
    }

    /**
     * 계약 만료
     */
    @Transactional
    public void expireContract(Long gymTrainerId) {
        GymTrainer gymTrainer = gymTrainerRepository.findById(gymTrainerId)
                .orElseThrow(() -> new CustomException(CONTRACT_NOT_FOUND));

        gymTrainer.expireContract(LocalDate.now());
    }
}
