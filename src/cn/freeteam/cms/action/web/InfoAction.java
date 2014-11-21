package cn.freeteam.cms.action.web;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.InfoSign;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.InfoSignService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
import cn.freeteam.util.EscapeUnescape;


/** 
 * <p>Title: InfoAction.java</p>
 * 
 * <p>Description: InfoAction</p>
 * 
 * <p>Date: May 22, 2012</p>
 * 
 * <p>Time: 8:46:48 PM</p>
 * 
 * <p>Copyright: 2011</p>
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
public class InfoAction extends BaseAction{

	private InfoService infoService;
	private InfoSignService infoSignService;
	private UserService userService;
	private SiteService siteService;
	
	private Info info;
	private Site site;
	private Users user;
	private String cansign;
	private List<InfoSign> infosignList;
	private List<Info> infoList;
	
	public InfoAction(){
		init("infoService");
	}

	/**
	 * ajax点击
	 * @return
	 */
	public String ajaxClick(){
		int clicknum=0;
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			info=infoService.findClickById(info.getId());
			if (info!=null) {
				info.setClicknum((info.getClicknum()!=null?info.getClicknum():0)+1);
				if (!"1".equals(info.getIshot()) && StringUtils.isNotEmpty(getConfigVal("infoHotNum")) ) {
					try {
						//如果点击大于信息热点配置则设置为热点
						if (info.getClicknum()>Integer.parseInt(getConfigVal("infoHotNum").trim())) {
							info.setIshot("1");
						}
					} catch (Exception e) {
					}
				}
				infoService.click(info);
				clicknum=info.getClicknum();
			}
		}
		write(""+clicknum, "UTF-8");
		return null;
	}
	/**
	 * 信息签收
	 * @return
	 */
	public String sign(){
		init("infoSignService");
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			info=infoService.findById(info.getId());
			if (info!=null) {
				infosignList=infoSignService.findSignByInfo(info.getId());
			}
		}
		return "sign";
	}
	/**
	 * 签收处理
	 * @return
	 */
	public String signDo(){
		//记住用户名
		Cookie cookie=new Cookie("FreeCMS_infosignLoginName",EscapeUnescape.escape(user.getLoginname()));
		cookie.setMaxAge(1000*60*60*24*365);//有效时间为一年
		getHttpResponse().addCookie(cookie);
		//记住密码
		Cookie cookiePwd=new Cookie("FreeCMS_infosignPwd",EscapeUnescape.escape(user.getPwd()));
		cookiePwd.setMaxAge(1000*60*60*24*365);//有效时间为一年
		getHttpResponse().addCookie(cookiePwd);
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			info=infoService.findById(info.getId());
			if (info!=null) {
				//判断用户信息是否正确
				init("userService");
				String msg=userService.checkLogin(getHttpSession(), user);
				if (msg!=null && msg.trim().length()>0) {
					write(msg, "UTF-8");
					return null;
				}
			    HttpSession session =getHttpSession();
			    user=(Users)session.getAttribute("loginAdmin");
				//判断是否需要此用户签收
				init("infoSignService");
				InfoSign infoSign=infoSignService.findByUserInfo(user.getId(), info.getId());
				if (infoSign!=null) {
					if (infoSign.getSigntime()!=null) {
						write("您已签收", "UTF-8");
					}else {
						infoSign.setIp(getHttpRequest().getRemoteAddr());
						infoSign.setSigntime(new Date());
						infoSignService.update(infoSign);
						write("操作成功"+user.getId(), "UTF-8");
					}
				}else {
					write("您不能指定的签收用户", "UTF-8");
				}
			}
		}
		return null;
	}
	/**
	 * 根据id访问信息 
	 * @return
	 * @throws IOException 
	 */
	public String visit() throws IOException{
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			infoList=infoService.find(info, "", 1, 1);
			if (infoList!=null && infoList.size()>0) {
				info=infoList.get(0);
				if (info!=null) {
					init("siteService");
					site=siteService.findById(info.getSite());
					if (site!=null) {
						info.setSitepath(getHttpRequest().getContextPath()+"/site/"+site.getSourcepath()+"/");
						getHttpResponse().sendRedirect(info.getPageurl());
					}
				}
			}
		}
		return null;
	}
	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public InfoSignService getInfoSignService() {
		return infoSignService;
	}

	public void setInfoSignService(InfoSignService infoSignService) {
		this.infoSignService = infoSignService;
	}

	public List<InfoSign> getInfosignList() {
		return infosignList;
	}

	public void setInfosignList(List<InfoSign> infosignList) {
		this.infosignList = infosignList;
	}


	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getCansign() {
		return cansign;
	}

	public void setCansign(String cansign) {
		this.cansign = cansign;
	}

	public List<Info> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<Info> infoList) {
		this.infoList = infoList;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
}
