package cn.freeteam.cms.action.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.CreditruleService;
import cn.freeteam.cms.service.GuestbookService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import freemarker.template.TemplateModelException;

/**
 * 
 * <p>Title: GuestbookAction.java</p>
 * 
 * <p>Description:留言相关操作 </p>
 * 
 * <p>Date: Mar 17, 2013</p>
 * 
 * <p>Time: 7:58:07 PM</p>
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
public class GuestbookAction extends BaseAction{

	private GuestbookService guestbookService;
	private SiteService siteService;
	private Guestbook guestbook;
	private String validatecode;
	private String msg;
	private String siteid;
	private String templetPath;
	private CreditruleService creditruleService;
	
	public GuestbookAction() {
		init("guestbookService","siteService");
	}
	

	public String save() throws TemplateModelException, IOException{
		if (guestbook!=null) {
			Site site=siteService.findById(siteid);
			if (site!=null && site.getIndextemplet()!=null 
					&& site.getIndextemplet().trim().length()>0) {
			    HttpSession session =getHttpSession();
				if (validatecode==null || !validatecode.equals(session.getAttribute("rand"))) {
					msg="验证码错误!";
				}else {
					guestbook.setAddtime(new Date());
					guestbook.setIp(getHttpRequest().getRemoteAddr());
					guestbook.setState("0");
					if (getLoginMember()!=null) {
						guestbook.setMemberid(getLoginMember().getId());
						guestbook.setMembername(getLoginMemberName());
					}
					guestbookService.add(guestbook);
					msg="感谢您的留言，我们会尽快回复并联系您！";
					//处理积分
					init("creditruleService");
					creditruleService.credit(getLoginMember(), "guestbook_pub");
				}
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				setData(data, site);
				data.put("msg", msg);
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+templetPath;
				getHttpResponse().setCharacterEncoding("UTF-8");
				FreeMarkerUtil.createWriter(getServletContext(), data, templetPath, getHttpResponse().getWriter());
			}
		}
		return null;
	}

	public GuestbookService getGuestbookService() {
		return guestbookService;
	}

	public void setGuestbookService(GuestbookService guestbookService) {
		this.guestbookService = guestbookService;
	}



	public Guestbook getGuestbook() {
		return guestbook;
	}



	public void setGuestbook(Guestbook guestbook) {
		this.guestbook = guestbook;
	}



	public String getValidatecode() {
		return validatecode;
	}



	public void setValidatecode(String validatecode) {
		this.validatecode = validatecode;
	}



	public String getMsg() {
		return msg;
	}



	public void setMsg(String msg) {
		this.msg = msg;
	}



	public String getSiteid() {
		return siteid;
	}



	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}



	public String getTempletPath() {
		return templetPath;
	}



	public void setTempletPath(String templetPath) {
		this.templetPath = templetPath;
	}



	public SiteService getSiteService() {
		return siteService;
	}



	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}


	public CreditruleService getCreditruleService() {
		return creditruleService;
	}


	public void setCreditruleService(CreditruleService creditruleService) {
		this.creditruleService = creditruleService;
	}
}
