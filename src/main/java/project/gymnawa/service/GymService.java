package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Gym;
import project.gymnawa.repository.GymRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymService {

    private GymRepository gymRepository;

    @Transactional
    public Long join(Gym gym) {
        // 이미 존재하는 헬스장인지 확인
        validateDuplicateGym(gym);
        gymRepository.save(gym);
        return gym.getId();
    }

    /**
     * 동일한 이름의 헬스장 존재하면 중복
     * 보통 동일한 이름의 프랜차이즈 헬스장도 존재하기 때문에
     * 나중에 이름과 주소까지 고려해서 동일성 체크할 예정
     * 지금은 우선 이름으로만
     */
    private void validateDuplicateGym(Gym gym) {
        List<Gym> result = gymRepository.findByName(gym.getName());
        if (!result.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 헬스장입니다.");
        }
    }

    /**
     * 이름으로 검색
     */
    public List<Gym> findByName(String name) {
        return gymRepository.findByName(name);
    }
}
