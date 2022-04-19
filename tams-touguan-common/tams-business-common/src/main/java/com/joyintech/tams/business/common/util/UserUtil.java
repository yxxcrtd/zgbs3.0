package com.joyintech.tams.business.common.util;

import com.joyintech.framework.security.User;
import com.joyintech.framework.util.DateUtil;

import java.util.Date;

public class UserUtil {

//    /**
//     * 获取用户信息
//     * @return
//     */
//    public static User getUserInfo(){
//    	com.joyintech.system.user.dto.User u = new com.joyintech.system.user.dto.User();
////        return SecurityUtil.getLoginUser();
//    }
    
	public static User getUser(){
		return new User() {
			public void setUserName(String userName) {
			}
			public void setUid(String uid) {
			}
			public void setSalt(String salt) {
			}
			public void setPassword(String password) {
			}
			public void setMailAddr(String mailAddr) {
			}
			public void setLastPassDate(Date lastPassDate) {
			}
			public void setIbuTrader(String ibuTrader) {
			}
			
			@Override
			public boolean isLocked() {
				return false;
			}
			@Override
			public boolean isExpired() {
				return false;
			}
			
			@Override
			public boolean isAdmin() {
				return false;
			}
			
			@Override
			public String getUserName() {
				return "admin";
			}
			
			@Override
			public String getUid() {
				return "admin";
			}
			
			@Override
			public String getSalt() {
				return null;
			}
			
			@Override
			public String getRealName() {
				return "admin";
			}
			
			@Override
			public String getPassword() {
				return "888888";
			}
			
			@Override
			public String getOrgCode() {
				return null;
			}
			
			@Override
			public String getMailAddr() {
				return null;
			}
			
			@Override
			public Date getLastPassDate() {
				return DateUtil.platDateFromCache();
			}
			
			@Override
			public String getIbuTrader() {
				return null;
			}
			
			@Override
			public String getDeptCode() {
				return null;
			}
		};
//        return SecurityUtil.getLoginUser();
    }

}
