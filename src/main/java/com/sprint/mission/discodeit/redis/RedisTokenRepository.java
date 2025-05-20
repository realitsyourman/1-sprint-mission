package com.sprint.mission.discodeit.redis;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@RequiredArgsConstructor
public class RedisTokenRepository implements PersistentTokenRepository {

  private final RedisTemplate<String, PersistentRememberMeToken> redis;
  private final RedisTemplate<String, String> userRedis;
  private static final int EXPIRE_TOKEN_TIME = 21;

  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    String key = getKey(token);
    setToken(token, key);

    String userKey = getUserKey(token);
    userRedis.opsForSet().add(userKey, token.getSeries());
    userRedis.expire(userKey, EXPIRE_TOKEN_TIME, TimeUnit.DAYS);
  }

  @Override
  public void updateToken(String series, String tokenValue, Date lastUsed) {
    String key = getKey(series);
    PersistentRememberMeToken persistentRememberMeToken = redis.opsForValue().get(key);
    if (persistentRememberMeToken != null) {

      setToken(persistentRememberMeToken, key);

      String userKey = getUserKey(persistentRememberMeToken);
      userRedis.expire(userKey, EXPIRE_TOKEN_TIME, TimeUnit.DAYS);
    }

  }

  @Override
  public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    return redis.opsForValue().get("remember-me:" + seriesId);
  }

  @Override
  public void removeUserTokens(String username) {
    String userKey = getUserKey(username);

    Set<String> seriesSet = userRedis.opsForSet().members(userKey);
    if (seriesSet != null) {
      seriesSet.forEach(series -> redis.delete(getKey(series)));

      userRedis.delete(userKey);
    }
  }


  private String getUserKey(String username) {
    return "remember-me-user:" + username;
  }

  private String getKey(PersistentRememberMeToken token) {
    return "remember-me:" + token.getSeries();
  }

  private String getKey(String series) {
    return "remember-me:" + series;
  }

  private void setToken(PersistentRememberMeToken token, String key) {
    redis.opsForValue().set(key, token, EXPIRE_TOKEN_TIME, TimeUnit.DAYS);
  }

  private String getUserKey(PersistentRememberMeToken token) {
    return "remember-me-user:" + token.getUsername();
  }

}