package cn.freeteam.cms.action.web;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Store;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.StoreService;

/** 
 * <p>Title: StoreAction.java</p>
 * 
 * <p>Description: StoreAction</p>
 * 
 * <p>Date: May 22, 2013</p>
 * 
 * <p>Time: 8:46:48 PM</p>
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
public class StoreAction extends BaseAction {

	private StoreService storeService;
	private InfoService infoService;
	private SiteService siteService;
	private ChannelService channelService;
	private Store store;
	private Info info;
	private Site site;
	private Channel channel;
	private String msg;
	
	public StoreAction() {
		init("storeService");
	}
	/**
	 * 收藏 
	 * @return
	 */
	public String ajaxStore(){
		if (getLoginMember()!=null) {
			if (store!=null && StringUtils.isNotEmpty(store.getObjid()) && StringUtils.isNotEmpty(store.getObjtype())) {
				try {
					//查询是否已收藏
					store.setMemberid(getLoginMember().getId());
					if (storeService.count(store)>0) {
						msg="您已收藏";
					}else {
						//查询收藏对象
						boolean haveObj=false;
						if ("info".equals(store.getObjtype())) {
							init("infoService");
							info=infoService.findById(store.getObjid());
							if (info!=null) {
								haveObj=true;
								store.setObjtitle(info.getTitle());
							}
						}
						if (haveObj) {
							//保存收藏数据
							if (StringUtils.isNotEmpty(store.getSiteid())) {
								init("siteService");
								site=siteService.findById(store.getSiteid());
								if (site!=null) {
									store.setSitename(site.getName());
								}
							}
							if (StringUtils.isNotEmpty(store.getChannelid())) {
								init("channelService");
								channel=channelService.findById(store.getChannelid());
								if (channel!=null) {
									store.setChannelname(channel.getName());
								}
							}
							store.setStoretime(new Date());
							storeService.insert(store);
							msg="收藏成功";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg="收藏失败";
				}
			}else {
				msg="请传递objid和objtype参数";
			}
		}else {
			msg="请先登陆会员";
		}
		write(msg, "UTF-8");
		return null;
	}

	public StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(StoreService storeService) {
		this.storeService = storeService;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public InfoService getInfoService() {
		return infoService;
	}
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
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
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
