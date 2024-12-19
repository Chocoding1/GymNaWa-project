package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Trainer;
import project.gymnawa.repository.TrainerRepository;

import java.util.List;

@Service
// 서비스에서 DB에 여러 번 접근할 수 있기 때문에 서비스 단에서 한 번에 쿼리를 처리하기 위해 서비스 단에 트랜잭션을 달아준다.
// 디비에 여러번 접근하면서 하나의 작업을 수행하는 서비스 계층 메서드에 주로 사용 (출처: https://swmobenz.tistory.com/34 [DevYGwan:티스토리])
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Trainer trainer) {
        validateDuplicateTrainer(trainer); // 중복 회원가입 방지
        trainerRepository.save(trainer);
        return trainer.getId();
    }

    private void validateDuplicateTrainer(Trainer trainer) {
        List<Trainer> result = trainerRepository.findByLoginId(trainer.getLoginId());
        if (!result.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
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
     * 특정 헬스장 소속 트레이너 목록(이 기능을 GymService에 넣어야 할지 고민 중)
     *  +) GymService에는 Gym 검색 기능이 들어가기 때문에 트레이너 검색은 TrainerService에 들어가는 게 맞다고 판단
     */
    public List<Trainer> findByGym(Long gymId) {
        return trainerRepository.findByGym(gymId);
    }
}
