package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.user.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  void removeUserById(UUID id);

  @Query("select u from User u join fetch u.status join fetch u.profile")
  List<User> findUsers();

  User findUserByUsername(String username);
}
