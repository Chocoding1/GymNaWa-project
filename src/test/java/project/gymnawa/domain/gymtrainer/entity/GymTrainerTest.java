package project.gymnawa.domain.gymtrainer.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.gymnawa.domain.common.etcfield.ContractStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GymTrainerTest {

    @Test
    @DisplayName("트레이너 계약 만료")
    void expireContract() {
        //given
        GymTrainer gymTrainer = GymTrainer.builder()
                .contractStatus(ContractStatus.ACTIVE)
                .build();

        LocalDate now = LocalDate.now();

        //when
        gymTrainer.expireContract(now);

        //then
        assertEquals(ContractStatus.EXPIRED, gymTrainer.getContractStatus());
        assertEquals(now, gymTrainer.getExpireDate());
    }

}