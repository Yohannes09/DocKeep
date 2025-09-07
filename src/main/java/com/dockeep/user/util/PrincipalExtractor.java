package com.dockeep.user.util;

import com.dockeep.user.model.UserPrincipal;

@FunctionalInterface
public interface PrincipalExtractor {
    UserPrincipal extract();
}
