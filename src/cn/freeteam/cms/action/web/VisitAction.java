package cn.freeteam.cms.action.web;

import java.io.IOException;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;

/** 
 * <p>Title: VisitAction.java</p>
 * 
 * <p>Description: 访问指定数据</p>
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
public class VisitAction extends BaseAction{

	private String siteid;
	private String channelid;
	private String infoid;
	
	private SiteService siteService;
	private ChannelService channelService;
	private InfoService infoService;
	
	/**
	 * 访问指定站点
	 * @return
	 * @throws IOException
	 */
	public String site() throws IOException{
		if (siteid!=null && siteid.trim().length()>0) {
			init("siteService");
			Site site=siteService.findById(siteid);
			if (site!=null && site.getSourcepath()!=null) {
				getHttpResponse().sendRedirect("site/"+site.getSourcepath()+"/index.html");
			}
		}
		return null;
	}

	/**
	 * 访问指定栏目
	 * @return
	 * @throws IOException 
	 * @throws IOException
	 */
	public String channel() throws IOException{
		if (channelid!=null && channelid.trim().length()>0) {
			init("channelService");
			Channel channel=channelService.findById(channelid);
			if (channel!=null) {
				if (channel.getUrl()!=null && channel.getUrl().trim().length()>0) {
					getHttpResponse().sendRedirect(channel.getUrl());
				}else {
					init("siteService");
					Site site=siteService.findById(channel.getSite());
					if (site!=null && site.getSourcepath()!=null) {
						channel.setSitepath(site.getSourcepath()+"/");
						getHttpResponse().sendRedirect("site/"+channel.getPageurl());
					}
				}
			}
		}
		return null;
	}

	/**
	 * 访问指定信息
	 * @return
	 * @throws IOException 
	 * @throws IOException
	 */
	public String info() throws IOException{
		if (infoid!=null && infoid.trim().length()>0) {
			init("infoService");
			Info info=infoService.findById(infoid);
			if (info!=null) {
				if (info.getUrl()!=null && info.getUrl().trim().length()>0) {
					getHttpResponse().sendRedirect(info.getUrl());
				}else {
					init("siteService");
					Site site=siteService.findById(info.getSite());
					if (site!=null && site.getSourcepath()!=null) {
						info.setSitepath(site.getSourcepath()+"/");
						getHttpResponse().sendRedirect("site/"+info.getPageurl());
					}
				}
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
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getInfoid() {
		return infoid;
	}
	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
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
