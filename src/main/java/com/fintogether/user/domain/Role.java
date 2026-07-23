package com.fintogether.user.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    EARNER("Earner", "Primary income earner in the couple"),
    SAVER("Saver", "Primary saver/investor in the couple");

    private final String displayName;
    private final String description;

}
