package project.gymnawa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymnawa.domain.Gym;
import project.gymnawa.domain.GymMembership;
import project.gymnawa.domain.NorMember;

import java.util.List;

public interface GymMembershipRepository extends JpaRepository<GymMembership, Long> {

    /**
     * 헬스장 등록 정보 저장
     */

    /**
     * 헬스장 별 등록 정보 조회
     */
    List<GymMembership> findByGym(Gym gym);

    /**
     * 회원 별 등록 정보 조회
     */
    List<GymMembership> findByNorMember(NorMember norMember);
}
