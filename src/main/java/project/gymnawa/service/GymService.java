package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.Gym;
import project.gymnawa.repository.GymRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    /**
     * 헬스장 등록
     * 추후 진행 예정
     */

    /**
     * 이름으로 검색
     */
    public List<Gym> findByName(String name) {
        return gymRepository.findByStoreName(name);
    }

    /**
     * 헬스장 단 건 조회
     */
    public Gym findGym(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 헬스장입니다."));
    }

    /**
     * 헬스장 목록
     */
    public List<Gym> findGyms() {
        return gymRepository.findAll();
    }
}
