package project.gymnawa.domain.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.gymnawa.domain.dto.gymtrainer.GymTrainerViewDto;
import project.gymnawa.domain.dto.normember.MemberViewDto;

@Data
@AllArgsConstructor
@Builder
public class MemberMyPageDto {

    // 회원 정보
    private MemberViewDto memberViewDto;

    // PT 트레이너 정보
    private GymTrainerViewDto trainerViewDto;

    // 리뷰 정보
    private
}
