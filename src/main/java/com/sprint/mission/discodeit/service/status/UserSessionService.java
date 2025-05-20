package com.sprint.mission.discodeit.service.status;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionService {

  private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

  public boolean isOnline(String username) {

    Map<String, ? extends Session> sessions = sessionRepository.findByIndexNameAndIndexValue(
        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, username);

    return sessions != null && !sessions.isEmpty();
  }
}
