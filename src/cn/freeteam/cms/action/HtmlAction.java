package cn.freeteam.cms.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.RoleChannelService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;


/**
 * 关于静态化的操作
 * @author freeteam
 *
 */
public class HtmlAction extends BaseAction{

	private SiteService siteService;
	private ChannelService channelService;
	private InfoService infoService;
	private RoleChannelService roleChannelService;
	
	public List<Site> siteList;
	private List<Channel> channelList;
	
	public String[] sites;
	public String msg;
	private Channel channel;
	private Site site;
	private String createType;
	private String[] channels;
	private String starttime;
	private String endtime;
	private Info info;
	private String pagenum;
	
	public HtmlAction(){
		init("siteService","channelService","infoService","roleChannelService");
	}
	/**
	 * 首页静态化页面
	 * @return
	 */
	public String index(){
		if (isAdminLogin()) {
			//提取一级站点
			siteList=siteService.selectByParId( "");
		}else {
			//普通用户只提取有自己有权限的站点
			siteList=siteService.selectByRoles(getLoginRoleIdsSql());
		}
		return "index";
	}
	/**
	 * 首页静态化页面
	 * @return
	 */
	public String indexConfirm(){
		return "indexConfirm";
	}
	/**
	 * 首页静态化处理
	 * @return
	 */
	public String indexDo(){
		try {
			site=getManageSite();
			if (site!=null) {
				//生成首页
				siteService.html(site.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
			}
			/*
			if (sites!=null && sites.length>0) {
				Site site;
				for (int i = 0; i < sites.length; i++) {
					if (sites[i].trim().length()>0) {
						//生成首页
						siteService.html(sites[i], getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
					}
				}
			}*/
			showMessage="首页静态化处理成功!";
		} catch (Exception e) {
			e.printStackTrace();
			showMessage="首页静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
		}
		return showMessage(showMessage, "", 0);
	}
	/**
	 * 栏目页静态化页面
	 * @return
	 */
	public String channel(){
		site=getManageSite();
		if (site!=null) {
			//获取当前管理站点
			if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
				if (!channel.getId().equals("select")) {
					channel=channelService.findById(channel.getId());
				}
				channelList=channelService.findByPar(site.getId(), "par");
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
				return "channelSelect";
			}else {
				//栏目管理页面
				//获取一级栏目 
				channelList=channelService.findByPar(site.getId(), "par");
				//设置是否有子栏目
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
			}
		}
		return "channel";
	}
	/**
	 * 生成栏目页处理
	 * @return
	 */
	public String channelDo(){
		try {
			site=getManageSite();
			int page=0;
			try {
				page=Integer.parseInt(pagenum);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if ("channels".equals(createType)) {
				//生成所选栏目
				if (channels!=null && channels.length>0) {
					for (int i = 0; i < channels.length; i++) {
						Channel channel=channelService.findById(channels[i]);
						if (channel!=null) {
							//生成栏目页
							channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(),page);
						}
					}
				}
			}else if ("all".equals(createType)) {
				//生成所有
				if (!isAdminLogin()) {
					channelList=channelService.findByRolesWithBLOBs(site.getId(),  getLoginRoleIdsSql());
				}else {
					channelList=channelService.findByParWithBLOBs(site.getId(), "");
				}
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						Channel channel=channelList.get(i);
						if (channel!=null) {
							//生成栏目页
							channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(),page);
						}
					}
				}
			}
			showMessage="栏目页静态化处理成功!";
		} catch (Exception e) {
			e.printStackTrace();
			showMessage="栏目页静态化处理失败，原因:"+e.toString().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
		}
		return showMessage(showMessage, "", 0);
	}

	/**
	 * 信息静态化页面
	 * @return
	 */
	public String info(){
		site=getManageSite();
		if (site!=null) {
			//获取当前管理站点
			if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
				if (!channel.getId().equals("select")) {
					channel=channelService.findById(channel.getId());
				}
				channelList=channelService.findByPar(site.getId(), "par");
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
				return "channelSelect";
			}else {
				//栏目管理页面
				//获取一级栏目 
				channelList=channelService.findByPar(site.getId(), "par");
				//设置是否有子栏目
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
			}
		}
		return "info";
	}
	/**
	 * 生成信息页处理
	 * @return
	 */
	public String infoDo(){
		try {
			boolean ishtml=false;
			site=getManageSite();
			List<Info> infoList=null;
			if ("channels".equals(createType)) {
				//生成所选栏目
				if (channels!=null && channels.length>0) {
					info.setChannels(channels);
					ishtml=true;
				}
			}else if ("all".equals(createType)) {
				//生成所有
				if (!isAdminLogin()) {
					channelList=channelService.findByRoles(site.getId(),  getLoginRoleIdsSql());
					String[] channelArr=new String[channelList.size()];
					if (channelList!=null && channelList.size()>0) {
						for (int i = 0; i < channelList.size(); i++) {
							channel=channelList.get(i);
							if (channel!=null) {
								channelArr[i]=channel.getId();
							}
						}
					}
					info.setChannels(channels);
				}
				ishtml=true;
			}
			if (ishtml) {
				info.setSite(site.getId());
				infoList=infoService.findAll(info, "");
				if (infoList!=null && infoList.size()>0) {
					for (int i = 0; i < infoList.size(); i++) {
						if (infoList.get(i)!=null) {
							//生成静态页面
							infoService.html(infoList.get(i).getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
						}
					}
				}
			}
			showMessage="信息页静态化处理成功!";
		} catch (Exception e) {
			showMessage="信息页静态化处理失败，原因:"+e.toString().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
		}
		return showMessage(showMessage, "", 0);
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public List<Site> getSiteList() {
		return siteList;
	}
	public void setSiteList(List<Site> siteList) {
		this.siteList = siteList;
	}

	public String[] getSites() {
		return sites;
	}

	public void setSites(String[] sites) {
		this.sites = sites;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}

	public String getCreateType() {
		return createType;
	}

	public void setCreateType(String createType) {
		this.createType = createType;
	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] channels) {
		this.channels = channels;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
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
	public RoleChannelService getRoleChannelService() {
		return roleChannelService;
	}
	public void setRoleChannelService(RoleChannelService roleChannelService) {
		this.roleChannelService = roleChannelService;
	}
	public String getPagenum() {
		return pagenum;
	}
	public void setPagenum(String pagenum) {
		this.pagenum = pagenum;
	}
}
