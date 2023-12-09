package com.nulp.project2.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    USE_BASIC_FUNCTIONS("use_basic_functions"),
    EDIT_USER_DATA("edit_user_data");

    private final String permission;
}
