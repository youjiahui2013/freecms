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
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: ChannelPathDirective.java</p>
 * 
 * <p>Description: 按上下级顺序提取指定栏目的所属栏目路径
 * 
 * 参数
 * id		栏目id
 * siteid	站点id
 * pagemark	栏目页面标识
 * 
 * 返回值
 * channel	栏目对象
 * index		索引
 * 
 * 使用示例
 * 
  <@channelPath siteid="${site.id}" ;channel> 
      <td class="index_menu index_menu_jg">|</td>
      <td class="index_menu index_menu1"><a href="#">${channel.name}</a></td>
  </@channelPath>
 * </p>
 * 
 * <p>Date: May 13, 2012</p>
 * 
 * <p>Time: 10:21:58 PM</p>
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
public class ChannelPathDirective extends BaseDirective implements TemplateDirectiveModel{

	private ChannelService channelService;
	private SiteService siteService;
	
	public ChannelPathDirective(){
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
					//查询栏目
					List<Channel> channelList=channelService.findPath(channel.getId());
					if (channelList!=null && channelList.size()>0) {
						Site site=siteService.findById(siteid);
						for (int i = 0; i < channelList.size(); i++) {
							//设置sitepath
							if (site!=null) {
								channelList.get(i).setSitepath(env.getDataModel().get("contextPath").toString()
										+"site/"+site.getSourcepath()+"/");
							}
							loopVars[0]=new BeanModel(channelList.get(i),new BeansWrapper());  
							if(loopVars.length>1){
								loopVars[1]=new SimpleNumber(i);
							}
							body.render(env.getOut());  
						}
					}
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
