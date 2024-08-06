package backend.Loveline_backend.repository;

import backend.Loveline_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    public Optional<User> findByLoverId(int loverId);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByUsername(String username);

}
