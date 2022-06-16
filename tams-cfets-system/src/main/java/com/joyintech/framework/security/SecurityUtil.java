/*
 * 文件名：SecurityUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改时间：2018年1月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.framework.security;

import com.joyintech.framework.config.ShiroProperties;
import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jwt.JWTToken;
import com.joyintech.framework.jwt.JWTUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.tams.business.common.util.UserUtil;

import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 安全性工具类
 * 
 * @version 1.0 2018年1月17日
 * @see SecurityUtil
 * @since 1.0
 */
@Lazy(false)
@Component
public final class SecurityUtil implements SecurityService {

    /**
     * SessionDAO
     */
    private static SessionDAO sessionDao;

    private static SessionManager sessionManager;

    private static ShiroProperties shiroProperties;

    /**
     * @param sessionDao SessionDAO
     */
    @Autowired(required = false)
    protected void setSessionDao(SessionDAO sessionDao) {
        SecurityUtil.sessionDao = sessionDao;
    }

    /**
     * @param sessionManager SessionManager
     */
    @Autowired(required = false)
    protected void setSecurityManager(SessionManager sessionManager) {
        SecurityUtil.sessionManager = sessionManager;
    }

    /**
     * @param shiroProperties ShiroProperties
     */
    @Autowired(required = false)
    protected void setShiroProperties(ShiroProperties shiroProperties) {
        SecurityUtil.shiroProperties = shiroProperties;
    }

    /**
     * 判断当前用户是否已通过认证
     *
     * @return 是否已通过认证
     */
    public static boolean hasAuthenticated() {
        try {
            boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
            if(!authenticated) {
                Cookie[] cookie = RequestUtil.getCurrentRequest().getCookies();
                if(null != cookie) {
                    // Sonar Fix modify by huwei 220609
                    Cookie c = cookie[0];
                    SessionDAO sessionDao = SpringContextUtil.getBean(SessionDAO.class);
                    Session session = sessionDao.readSession(c.getValue());
                    authenticated = null != session.getAttribute("__" + c.getValue());
                }
            }
            return authenticated;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取登录用户
     *
     * @return 用户
     */
    public static User getLoginUser() {
    	return UserUtil.getUser();
//        return (User) getFromSession(GlobalConstant.CURRENT_USER);
    }

    /**
     * 获取登录用户
     *
     * @return 用户
     */
    @Override
    public User getCurrentUser() {
        return getLoginUser();
    }

    /**
     * @param sessionId sessionId
     */
    public static Session getSession(Serializable sessionId) {
        try {
            return sessionDao.readSession(sessionId);
        } catch (UnknownSessionException e) {
            return null;
        }
    }

    /**
     * 获取登录用户名
     *
     * @param authcToken AuthenticationToken
     * @return 登录的用户名
     */
    public static String getLoginUsername(AuthenticationToken authcToken) {
        if (authcToken instanceof Pac4jToken) {
            final Pac4jToken token = (Pac4jToken)authcToken;
            final Pac4jPrincipal principal = new Pac4jPrincipal(token.getProfiles());
            return principal.getProfile().getId();
        }
        else if (authcToken instanceof JWTToken) {
            String token = (String)authcToken.getCredentials();
            // 解密获得username，用于和数据库进行对比
            return JWTUtil.getUsername(token);
        }
        return (String)authcToken.getPrincipal();
    }

    /**
     * 把用户存放到session中
     *
     * @param user 登录用户
     */
    public static void addUserToSession(User user) {
        addSession(GlobalConstant.CURRENT_USER, user);
    }

    /**
     * 把数据存放到session中
     *
     * @param sessionKey session key
     * @param sessionValue session value
     */
    public static void addSession(String sessionKey, Object sessionValue) {
        SecurityUtils.getSubject().getSession().setAttribute(sessionKey, sessionValue);
    }

    /**
     * 获取session存储的数据
     *
     * @param sessionKey session key
     * @return 数据
     */
    public static Object getFromSession(String sessionKey) throws UnknownSessionException {
        try {
            Session session = SecurityUtils.getSubject().getSession(false);
            if(null != session) {
                return session.getAttribute(sessionKey);
            } else {
                if (null == RequestUtil.getCurrentRequest()) {
                    return null;
                }
                Cookie[] cookie = RequestUtil.getCurrentRequest().getCookies();
                if(null != cookie) {
                    // Sonar Fix modify by huwei 220609
                    Cookie c = cookie[0];
                    SessionDAO sessionDao = SpringContextUtil.getBean(SessionDAO.class);
                    session = sessionDao.readSession(c.getValue());
                    if ("__sk_user_orgType_".equals(sessionKey)) {
                        return session.getAttribute("__sk_user_orgType_");
                    } else if (GlobalConstant.SESSION_INSTITUTION_CODE.equals(sessionKey)) {
                        return session.getAttribute(GlobalConstant.SESSION_INSTITUTION_CODE);
                    }
                    return session.getAttribute("__" + c.getValue());
                }
            }
        } catch (Exception e) {
            // TODO
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否有权限
     * 
     * @param permit 权限字符串
     * @return 是否有权限
     */
    public static boolean isPermitted(String permit) {
        if (StringUtils.isEmpty(permit)) {
            return true;
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject.hasRole(GlobalConstant.SUPER_ADMIN_ROLE_NAME) || getLoginUser().isAdmin() || subject.isPermitted(permit);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 将用户踢出登录
     * 
     * @param user 用户
     */
    public static void kickOutUser(User user) {
        if(null != user && StringUtils.isNotEmpty(user.getSessionId())) {
            try {
                Session userSession = sessionDao.readSession(user.getSessionId());
                if(null != userSession) {
                    userSession.removeAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
                    userSession.removeAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                    ((DefaultWebSessionManager)sessionManager).stop(
                            new DefaultSessionKey(userSession.getId()));
                    sessionDao.delete(userSession);
                }
            } catch (UnknownSessionException | ExpiredSessionException e) {
            }
        }
    }

    /**
     * 通过用户名将用户踢出登录
     *
     * @param userName 用户名
     */
    public static void kickOutUser(String userName) {
        if (sessionManager instanceof DefaultWebSessionManager) {
            // 获取所有登录列表
            Collection<Session> sessions = sessionDao.getActiveSessions();
            // 获取当前登录session
            Session currentSession = SecurityUtils.getSubject().getSession(false);
            // 同一个用户上次登录的session
            for (Session session : sessions) {
                if (null != session && StringUtils.equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)), userName)) {
                    // 踢出同一用户上次登录的用户session
                    if (null != currentSession
                            && !StringUtils.equals(String.valueOf(session.getId()),
                            currentSession.getId().toString())) {
                        session.removeAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
                        session.removeAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                        ((DefaultWebSessionManager)sessionManager).stop(
                                new DefaultSessionKey(session.getId()));
                        sessionDao.delete(session);
                    }
                }
            }
        }
    }

    /**
     * 是否允许重复登录
     * @param user 用户
     * @return 重复登录
     */
    public static boolean isAllowDuplicateLogin(User user) {
        boolean focusLogin = false;
        Subject subject = SecurityUtils.getSubject();
        if(subject instanceof WebDelegatingSubject) {
            focusLogin = StringUtils.equals(((WebDelegatingSubject) subject).getServletRequest().getParameter("forceLogin"), DictCodeList.是否_是.getValue());
        }
        return shiroProperties.isAllowDuplicateLogin()/*允许重复登录*/
                || focusLogin/*非强制登录*/
                || StringUtils.equals(CacheUtil.getSysParam(GlobalConstant.DUPLICATE_LOGON_TYPE), DictCodeList.是否_是.getValue())/*“重复登录处理模式”为不提示*/
                || (StringUtils.isEmpty(user.getSessionId()) || getSession(user.getSessionId()) == null)/*用户已经登录*/;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAuthenticated() {
		return hasAuthenticated();
	}

	@Override
    public String getLoginUserName() {
        User user = getLoginUser();
        if(null != user) {
            return user.getUserName();
        }
        return null;
    }

    @Override
    public String getLoginUserDeptCode() {
        User user = getLoginUser();
        if(null != user) {
            return user.getDeptCode();
        }
        return null;
    }

    @Override
    public String getUserOrgType() {
//        User user = getLoginUser();
        String orgType = (String)getFromSession("__sk_user_orgType_");
        if(null != orgType) {
            return orgType;
        }
        return null;
    }

    @Override
    public Object findBySession(String key) {
        return getFromSession(key);
    }
    
    @Override
    public List<String> getCommonSelectInstitutionCode(String key) {
    	try {
	    	Session session = getSession(LoginUserUtil.getUserName());
	        if(null != session) {
	        	Object obj = session.getAttribute(key);
	            if (null!=obj) {
	            	List<String> selectInstitutionCode =  (List<String>) obj;
	            	if (null!=selectInstitutionCode && selectInstitutionCode.size()>0) {
	            		return selectInstitutionCode;
	            	}
				}
	            //原代码2020-11-09
	            Serializable sessionId = session.getId();
	            String userName = sessionId + LoginUserUtil.getUserName();
	            List<String> selectInstitutionCode = CacheUtil.getAppCache(userName + key, List.class);
	            return selectInstitutionCode;
	        }
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
        return new ArrayList<String>();
    }
}
