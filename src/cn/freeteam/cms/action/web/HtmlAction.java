package cn.freeteam.cms.action.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.SiteService;
import freemarker.template.TemplateException;

/**
 * 
 * <p>Title: HtmlAction.java</p>
 * 
 * <p>Description: 静态化处理</p>
 * 
 * <p>Date: May 12, 2013</p>
 * 
 * <p>Time: 8:09:38 PM</p>
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
public class HtmlAction extends BaseAction{

	private SiteService siteService;
	private ChannelService channelService;
	private String siteid;
	private String channelid;
	
	/**
	 * 检测htmlQuartaKey是否正确
	 * @param request
	 * @return
	 */
	public boolean checkKey(HttpServletRequest request){
		if(request.getParameter("htmlQuartaKey")!=null 
				&& request.getParameter("htmlQuartaKey").trim().length()>0
				&& request.getParameter("htmlQuartaKey").equals(getServletContext().getAttribute("htmlQuartaKey"))){
			return true;
		}
		return false;
	}
	/**
	 * 静态化首页
	 * @return
	 */
	public String site(){
		if (checkKey(getHttpRequest())
				&& siteid!=null && siteid.trim().length()>0) {
			try {
				init("siteService");
				siteService.html(siteid, getServletContext());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 静态化栏目页
	 * @return
	 */
	public String channel(){
		if (checkKey(getHttpRequest())
				&& channelid!=null && channelid.trim().length()>0) {
			try {
				init("channelService");
				channelService.html(siteid, channelid, getServletContext());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public String getSiteid() {
		return siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public ChannelService getChannelService() {
		return channelService;
	}
	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
}
