package project.gymnawa.auth.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.gymnawa.auth.jwt.dto.RefreshToken;

import java.util.Optional;

@Repository
public interface JwtRepository extends CrudRepository<RefreshToken, Long> {

    // refresh token 조회
    Optional<RefreshToken> findById(Long id);

    // refresh token 삭제
    void deleteById(Long id);
}
