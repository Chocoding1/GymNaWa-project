package project.gymnawa.auth.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.gymnawa.auth.jwt.domain.RefreshToken;

import java.util.Optional;

@Repository
public interface JwtRepository extends CrudRepository<RefreshToken, Long> {

    @Override
    Optional<RefreshToken> findById(Long userId);
}
