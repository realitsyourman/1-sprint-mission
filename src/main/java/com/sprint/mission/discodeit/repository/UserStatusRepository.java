package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.entity.user.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  UserStatus user(User user);
}
