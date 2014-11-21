package cn.freeteam.tag;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import cn.freeteam.model.Users;




/**
 * 标签公用
 * 2011-4-15
 * 
 * @author freeteam
 */
public class BaseTag extends TagSupport{

	public HttpServletRequest getRequest(){
		return (HttpServletRequest)pageContext.getRequest();
	}
	public HttpSession getSession(){
		return getRequest().getSession();
	}
	/**
	 * 获取session中的当有登录用户
	 * @return
	 */
	public Users getLoginAdmin(){
		if (getSession().getAttribute("loginAdmin")!=null) {
			return (Users)getSession().getAttribute("loginAdmin");
		}
		return null;
	}
	/**
	 * 获取session中的当有登录用户名
	 * @return
	 */
	public String getLoginName(){
		if (getLoginAdmin()!=null) {
			return getLoginAdmin().getLoginname();
		}
		return "";
	}
	/**
	 * 获取session中的当有登录企业
	 * @return
	 */
	public String getLoginCompany(){
		if (getSession().getAttribute("loginCompany")!=null) {
			return (String)getSession().getAttribute("loginCompany");
		}
		return null;
	}
}