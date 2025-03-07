package com.sprint.mission.discodeit.entity.user.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserUpdateRequest(

    @NotEmpty
    String newUsername,

    @NotEmpty
    String newEmail,

    @NotEmpty
    String newPassword
) {

}
