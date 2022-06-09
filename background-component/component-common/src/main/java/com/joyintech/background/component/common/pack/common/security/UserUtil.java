package com.joyintech.background.component.common.pack.common.security;

/**
 * @author 何阳阳
 */
public final class UserUtil {
    
    public static String DEFAULT_USER = "system";

    public static String DEFAULT_DEPT = "000000";

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static String getUserName() {
        if(null != USER_THREAD_LOCAL.get()) {
            return USER_THREAD_LOCAL.get().getUserName();
        }
        return DEFAULT_USER;
    }

    public static String getDeptCode() {
        if(null != USER_THREAD_LOCAL.get()) {
            return USER_THREAD_LOCAL.get().getDeptCode();
        }
        return DEFAULT_DEPT;
    }

    public static void bindUser(User user) {
        if(user != null) {
            USER_THREAD_LOCAL.set(user);
        }
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    public void unload() {
        USER_THREAD_LOCAL.remove(); // Compliant
    }
}
