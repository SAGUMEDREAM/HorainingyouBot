package cc.thonly.horainingyoubot.repository;

import cc.thonly.horainingyoubot.data.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
