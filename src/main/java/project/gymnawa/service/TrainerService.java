package project.gymnawa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gymnawa.domain.*;
import project.gymnawa.repository.MemberRepository;
import project.gymnawa.repository.TrainerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
// 서비스에서 DB에 여러 번 접근할 수 있기 때문에 서비스 단에서 한 번에 쿼리를 처리하기 위해 서비스 단에 트랜잭션을 달아준다.
// 디비에 여러번 접근하면서 하나의 작업을 수행하는 서비스 계층 메서드에 주로 사용 (출처: https://swmobenz.tistory.com/34 [DevYGwan:티스토리])
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;

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
        Optional<Member> result = memberRepository.findByLoginId(trainer.getLoginId());
        if (result.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
    }

    /**
     * 트레이너 단건 조회
     */
    public Trainer findOne(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 트레이너입니다."));
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
    public void updateTrainer(Long id, String loginId, String password, String name, Address address) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 트레이너입니다."));

        //중복 아이디 체크
        Optional<Member> dupliTrainer = memberRepository.findByLoginId(loginId);
        if (dupliTrainer.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }

        trainer.updateInfo(loginId, password, name, address);
    }

    /**
     * 트레이너 탈퇴
     */
    @Transactional
    public void deleteOne(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 트레이너입니다."));

        trainerRepository.delete(trainer);
    }
}
