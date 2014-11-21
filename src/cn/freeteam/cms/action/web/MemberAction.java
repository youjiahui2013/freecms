package cn.freeteam.cms.action.web;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.model.Membergroup;
import cn.freeteam.cms.service.MemberService;
import cn.freeteam.cms.service.MembergroupService;
import cn.freeteam.model.Users;
import cn.freeteam.util.EscapeUnescape;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.MD5;
import cn.freeteam.util.Mail;
import cn.freeteam.util.MybatisSessionFactory;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;

/**
 * 
 * <p>Title: MemberAction.java</p>
 * 
 * <p>Description:会员相关操作 </p>
 * 
 * <p>Date: Feb 1, 2013</p>
 * 
 * <p>Time: 8:00:46 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class MemberAction extends BaseAction{

	private MemberService memberService;
	private MembergroupService membergroupService;
	private Member member;
	private Membergroup membergroup;
	private String ValidateCode;
	private String RememberMe;
	private String msg;
	public MemberAction() {
		init("memberService");
	}

	/**
	 * 注册处理
	 * @return
	 */
	public String register(){
		try {
			init("membergroupService");

		    HttpSession session =getHttpSession();
			if (ValidateCode!=null && !ValidateCode.equals(session.getAttribute("rand"))) {
				write("<script>alert('验证码错误!');history.back();</script>", "UTF-8");
				return null;
			}
			//判断是否是禁用会员名
			String memberLimitName = getConfigVal("memberLimitName");
			if (memberLimitName.indexOf(member.getLoginname()+",") >-1 
					|| memberLimitName.indexOf(","+member.getLoginname()) >-1 ) {
				write("<script>alert('禁止使用此会员名!');history.back();</script>", "UTF-8");
				return null;
			}
			//判断用户是否存在
			if (memberService.have(member)) {
				write("<script>alert('此会员名已存在!');history.back();</script>", "UTF-8");
				return null;
			}
			member.setPwd(MD5.MD5(member.getPwd()));
			member.setAddtime(new Date());
			member.setIsok("1");
			member.setGrouptype(0);
			member.setExperience(0);
			member.setCredit(0);
			//默认注册会员是经验会员并处理所属会员组
			if (member.getExperience()!=null) {
				membergroup=membergroupService.findByExperience(0);
				if (membergroup!=null) {
					member.setGroupid(membergroup.getId());
				}else {
					member.setGroupid("");
				}
			}else {
				member.setGroupid("");
			}
			memberService.add(member);
			getHttpSession().setAttribute("loginMember", member);
		} catch (Exception e) {
			DBProException(e);
			write(e.toString(), "GBK");
		}
		return showMessage("恭喜您，注册成功了!<br>正在跳转到会员中心!", "member/member_index.do", 3);
	}

	/**
	 * 检查登录名是否重复
	 * @return
	 */
	public String checkLoginname(){
		//判断用户是否存在
		if (memberService.have(member)) {
			write("此会员名已存在!", "UTF-8");
			return null;
		}
		return null;
	}
	/**
	 * 会员登录
	 * @return
	 */
	public String login(){
		try {
			//记住用户名
			if("on".equals(RememberMe)){
				Cookie cookie=new Cookie("FreeCMS_memberLoginName",EscapeUnescape.escape(member.getLoginname()));
				cookie.setMaxAge(1000*60*60*24*365);//有效时间为一年
				getHttpResponse().addCookie(cookie);
			}
		    HttpSession session =getHttpSession();
			if (ValidateCode!=null && ValidateCode.equals(session.getAttribute("rand"))) {
				showMessage=memberService.checkLogin(getHttpSession(), member);
			}else {
				showMessage="验证码错误!";
			}
			if (showMessage==null || "".equals(showMessage)) {
				OperLogUtil.log(member.getLoginname(), "会员登录", getHttpRequest());
				getHttpResponse().sendRedirect("member/member_index.do");
				return null;
			}else {
				return showMessage(showMessage, forwardUrl, forwardSeconds);
			}
		} catch (Exception e) {
			DBProException(e);
			OperLogUtil.log(member.getLoginname(), "会员登录失败:"+e.toString(), getHttpRequest());
			return showMessage("出现错误:"+e.toString()+"", forwardUrl, forwardSeconds);
		}
	}
	//退出
	public String out(){
	    HttpSession session =getHttpSession();
	    session.removeAttribute("loginMember");
	    session.removeAttribute("loginMembergroup");
	    msg="<script>parent.location.href=parent.location.href;</script>";
	    return "msg";
	}
	/**
	 * 找回密码
	 * @return
	 */
	public String findPwd(){
		//判断用户是否存在
		member=memberService.findByLoginname(member);
		if (member!=null) {
			String newPwd=UUID.randomUUID().toString().substring(0, 8);
			member.setPwd(MD5.MD5(newPwd));
			memberService.update(member);
			//发送邮件
			Mail mail=new Mail(getConfig());
			mail.sendMessage(member.getEmail(), "FreeCMS:"+member.getLoginname()+"找回密码邮件", 
					"您的新密码为"+newPwd+",请使用新密码登录，原密码已不可用。<br><a href='"+getBasePath()+"/mlogin.jsp'>"+getBasePath()+"/mlogin.jsp</a>");
		    return showMessage("已发送新密码到您的邮箱，请查收，并使用新密码登录!", forwardUrl, forwardSeconds);
		}else {
		    return showMessage("此会员不存在!", forwardUrl, forwardSeconds);
		}
	}
	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public MembergroupService getMembergroupService() {
		return membergroupService;
	}

	public void setMembergroupService(MembergroupService membergroupService) {
		this.membergroupService = membergroupService;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Membergroup getMembergroup() {
		return membergroup;
	}

	public void setMembergroup(Membergroup membergroup) {
		this.membergroup = membergroup;
	}

	public String getValidateCode() {
		return ValidateCode;
	}

	public void setValidateCode(String validateCode) {
		ValidateCode = validateCode;
	}

	public String getRememberMe() {
		return RememberMe;
	}

	public void setRememberMe(String rememberMe) {
		RememberMe = rememberMe;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
