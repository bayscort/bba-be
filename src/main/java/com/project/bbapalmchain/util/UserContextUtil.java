package com.project.bbapalmchain.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserContextUtil {

    private static UserContext userContext;

    @Autowired
    public void setUserContext(UserContext userContext) {
        UserContextUtil.userContext = userContext;
    }

    public static String getCurrentUsername() {
        try {
            return userContext != null ? userContext.getUsername() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}