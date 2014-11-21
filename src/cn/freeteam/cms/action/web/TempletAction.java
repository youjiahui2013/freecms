package cn.freeteam.cms.action.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.util.OperLogUtil;


import freemarker.template.TemplateModelException;

/** 
 * <p>Title: TempletAction.java</p>
 * 
 * <p>Description: TempletAction</p>
 * 
 * <p>Date: May 24, 2012</p>
 * 
 * <p>Time: 12:16:44 PM</p>
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
public class TempletAction extends BaseAction{

	private SiteService siteService;
	private TempletService templetService;
	private ChannelService channelService;
	private InfoService infoService;
	
	private String siteid;
	private String templetPath;
	
	public TempletAction(){
		init("siteService","templetService");
	}
	
	/**
	 * 获取数据处理模板并装处理结果以页面形式显示为用户
	 * @return
	 * @throws IOException 
	 * @throws TemplateModelException 
	 */
	public String pro() throws TemplateModelException, IOException{
		if (siteid!=null && siteid.trim().length()>0
				&& templetPath!=null && templetPath.trim().length()>0) {
			//查询站点
			Site site=siteService.findById(siteid);
			if (site!=null && site.getIndextemplet()!=null 
					&& site.getIndextemplet().trim().length()>0) {
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				setData(data, site);
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+templetPath;
				getHttpResponse().setCharacterEncoding("UTF-8");
				FreeMarkerUtil.createWriter(getServletContext(), data, templetPath, getHttpResponse().getWriter());
			}
		}
		return null;
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

	public TempletService getTempletService() {
		return templetService;
	}

	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
}
