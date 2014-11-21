package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
 * <p>Title: ChannelListDirective.java</p>
 * 
 * <p>Description: 栏目循环标签
 * 
 * 参数
 * siteid	站点id
 * parid    空字符:所有;"par":一级栏目;"parid":此id下栏目;
 * navigation  是否导航 空字符串:所有;"1":是;"0":否;
 * state  是否有效 空字符串:所有;"1":是;"0":否;
 * 
 * 返回值
 * channel	栏目对象
 * index		索引
 * 
 * 使用示例
 * 
  <@channelList siteid="${site.id}" ;channel> 
      <td class="index_menu index_menu_jg">|</td>
      <td class="index_menu index_menu1"><a href="#">${channel.name}</a></td>
  </@channelList>
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
public class ChannelListDirective extends BaseDirective implements TemplateDirectiveModel{

	private ChannelService channelService;
	private SiteService siteService;
	
	public ChannelListDirective(){
		init("channelService","siteService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String siteid=getParam(params, "siteid");
		String parid=getParam(params, "parid");// 空字符:所有;"par":一级栏目;"parid":此id下栏目;
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && siteid.trim().length()>0) {
				//查询栏目
				List<Channel> channelList=channelService.findByPar(
						siteid, parid,getParam(params, "state"),getParam(params, "navigation"));
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
