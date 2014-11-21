package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;


import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.NumberModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: InfoNextListDirective.java</p>
 * 
 * <p>Description: 下几条 信息列表标签
 * 参数
 * siteid		站点id
 * channelid	栏目id
 * channelParid	栏目parid
 * infoid		信息id
 * num			显示数量
 * order		排序类型  
 * 				1 固顶有效并降序,发布时间降序(默认)
 * 				2 固顶有效并降序,发布时间升序
 * 				3 发布时间降序
 * 				4 发布时间升序
 * titleLen		标题显示长度
 * hot			是否按点击热度倒序，1是
 * dateFormat	日期格式
 * channelPagemark	栏目页面标识
 * channelParPagemark	父栏目页面标识
 * img			是否只提取带图片的新闻	1是
 * checkOpenendtime	检查公开时限 默认不检查，1检查
 * 
 * 返回值
 * info			信息对象
 * index		索引
 * 
 * 
 * 使用示例
 * </p>
 * 
 * <p>Date: May 14, 2012</p>
 * 
 * <p>Time: 1:45:03 PM</p>
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
public class InfoNextListDirective extends BaseDirective implements TemplateDirectiveModel{

	private InfoService infoService;
	private SiteService siteService;
	private ChannelService channelService;
	
	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public InfoNextListDirective(){
		init("infoService","siteService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//站点id
		String siteid=getParam(params, "siteid");
		//栏目id
		String channelid=getParam(params, "channelid");
		String channelParid=getParam(params, "channelParid");
		String infoid=getParam(params, "infoid");
		//显示数量
		int num=getParamInt(params, "num", 1);
		//排序
		String order=getParam(params, "order","1");
		//标题长度
		int titleLen=getParamInt(params, "titleLen",10);
		//是否按点击热度查询
		String hot=getParam(params, "hot");
		//日期格式
		String dateFormat=getParam(params, "dateFormat");
		//栏目页面标识
		String channelPagemark=getParam(params, "channelPagemark");
		String channelParPagemark=getParam(params, "channelParPagemark");
		//是否只提取带图片的信息
		String img=getParam(params, "img");
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				Info currInfo=null;
				if (StringUtils.isNotEmpty(infoid)) {
					currInfo=infoService.findById(infoid);
					if (StringUtils.isEmpty(siteid)) {
						siteid=currInfo.getSite();
					}
				}
				//查询信息
				Info info=new Info();
				if (siteid.trim().length()>0) {
					info.setSite(siteid);
				}
				if (channelid.trim().length()>0) {
					info.setChannel(channelid);
				}
				if (channelPagemark.trim().length()>0) {
					info.setChannelPagemark(channelPagemark);
				}
				if (channelParid.trim().length()>0) {
					List<String> channelids=new ArrayList<String>();
					channelids.add(channelParid);
					init("channelService");
					List<Channel> sonList=channelService.findSon(siteid, channelParid, "1", "");
					if (sonList!=null && sonList.size()>0) {
						for (int i = 0; i < sonList.size(); i++) {
							channelids.add(sonList.get(i).getId());
						}
					}
					info.setChannelids(channelids);
				}
				if (channelParPagemark.trim().length()>0) {
					List<String> channelids=new ArrayList<String>();
					init("channelService");
					Channel channel=channelService.findBySitePagemark(siteid, channelParPagemark);
					if (channel!=null) {
						channelids.add(channel.getId());
						List<Channel> sonList=channelService.findSon(siteid, channel.getId(), "1", "");
						if (sonList!=null && sonList.size()>0) {
							for (int i = 0; i < sonList.size(); i++) {
								channelids.add(sonList.get(i).getId());
							}
						}
						info.setChannelids(channelids);
					}
				}
				if (img.trim().length()>0) {
					info.setImg(img);
				}
				info.setCheckOpenendtime(getParam(params, "checkOpenendtime"));
				String orderSql="";
				if ("1".equals(hot)) {
					orderSql=" clickNum desc,addtime desc ";
					if (currInfo!=null) {
						info.setInfoEndClicknum(currInfo.getClicknum());
						info.setInfoendtimeNoeq(currInfo.getAddtime());
					}
				}else {
					if ("1".equals(order)) {
						//固顶有效并降序,发布时间降序(默认)
						orderSql=" isTop desc,addtime desc";
						if (currInfo!=null) {
							info.setInfoendtimeNoeq(currInfo.getAddtime());
						}
					}
					else if ("2".equals(order)) {
						//固顶有效并降序,发布时间升序
						orderSql=" isTop desc,addtime";
						if (currInfo!=null) {
							info.setInfostarttimeNoeq(currInfo.getAddtime());
						}
					}
					else if ("3".equals(order)) {
						//发布时间倒序
						orderSql=" addtime desc";
						if (currInfo!=null) {
							info.setInfoendtimeNoeq(currInfo.getAddtime());
						}
					}
					else if ("4".equals(order)) {
						//发布时间升序
						orderSql=" addtime";
						if (currInfo!=null) {
							info.setInfostarttimeNoeq(currInfo.getAddtime());
						}
					}
				}
				List<Info> infoList=infoService.find(info, orderSql, 1, num);
				Site site=siteService.findById(siteid);
				if (infoList!=null && infoList.size()>0) {
					for (int i = 0; i < infoList.size(); i++) {
						if (titleLen>0) {
							infoList.get(i).setShowtitleLen(titleLen);
						}
						if (dateFormat.trim().length()>0) {
							infoList.get(i).setDateFormat(dateFormat);
						}
						//设置sitepath
						if (site!=null) {
							infoList.get(i).setSitepath(env.getDataModel().get("contextPath").toString()
									+"site/"+site.getSourcepath()+"/");
						}
						loopVars[0]=new BeanModel(infoList.get(i),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
			}
		}
	}
	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
}
