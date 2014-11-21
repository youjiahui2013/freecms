package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.SiteService;


import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: ChannelNameDirective.java</p>
 * 
 * <p>Description:根据id提取栏目对象</p>
 * 参数 
 * id		栏目id
 * siteid	站点id
 * pagemark	栏目页面标识
 * checkHasSon 是否检查有子栏目 1是
 * 
 * 返回值
 * channel 栏目对象
 * 
 * 使用示例
   <@channel id="b7a761e6-8308-472a-9758-1d1d5142a609" ;channel>${channel.name}</@channel>
 * 
 * <p>Date: May 14, 2012</p>
 * 
 * <p>Time: 1:08:48 PM</p>
 * 
 * <p>Copyright: 2012</p>
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
public class ChannelDirective extends BaseDirective implements TemplateDirectiveModel{

	private ChannelService channelService;
	private SiteService siteService;
	
	public ChannelDirective(){
		init("channelService","siteService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//栏目id
		String channelid=getParam(params, "id");
		//站点id
		String siteid=getParam(params, "siteid");
		//栏目页面标识
		String pagemark=getParam(params, "pagemark");
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询栏目
				Channel channel=null;
				//有channelid参数则根据channelid参数查询
				if (channelid.trim().length()>0) {
					channel=channelService.findById(channelid);
				}else if (siteid.trim().length()>0 && pagemark.trim().length()>0) {
					channel=channelService.findBySitePagemark(siteid, pagemark);
				}
				if (channel!=null) {
					//设置sitepath
					Site site=siteService.findById(channel.getSite());
					if (site!=null) {
						channel.setSitepath(env.getDataModel().get("contextPath").toString()
								+"site/"+site.getSourcepath()+"/");
					}
					if ("1".equals(getParam(params, "checkHasSon"))) {
						//检查是否有子栏目
						channel.setHasChildren(channelService.hasChildren(channel.getId())?"1":"0");
					}
					loopVars[0]=new BeanModel(channel,new BeansWrapper());  
					body.render(env.getOut());  
				}
			}
		}
	}

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

}
