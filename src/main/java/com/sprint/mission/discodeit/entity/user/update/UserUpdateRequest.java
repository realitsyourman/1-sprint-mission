package com.sprint.mission.discodeit.entity.user.update;

public record UserUpdateRequest(
    String username,
    String email,
    String password
) {

}
