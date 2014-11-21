package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreemarkerPager;
import cn.freeteam.util.DateUtil;


import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: InfoPageDirective.java</p>
 * 
 * <p>Description: 信息列表分页标签
 * 参数
 * siteid		站点id
 * channelid	栏目id
 * channelparid	栏目父id
 * num			显示数量
 * order		排序类型  
 * 				1 固顶有效并降序,发布时间降序(默认)
 * 				2 固顶有效并降序,发布时间升序
 * 				3 发布时间降序
 * 				4 发布时间升序
 * titleLen		标题长度
 * hot			是否按点击热度倒序，1是
 * dateFormat	日期格式
 * channelPagemark	栏目页面标识
 * channelParPagemark	父栏目页面标识
 * img			是否只提取带图片的新闻	1是
 * page			当前第几页，默认1			
 * pagenum		最多显示页数
 * checkOpenendtime	检查公开时限 默认不检查，1检查
 * newdays		几天内为最新
 * 
 * 返回值
 * infoList		信息对象列表
 * pager		分页对象
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
public class InfoPageDirective extends BaseDirective implements TemplateDirectiveModel{

	private InfoService infoService;
	private SiteService siteService;
	private ChannelService channelService;
	
	public InfoPageDirective(){
		init("infoService","siteService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//站点id
		String siteid=getParam(params, "siteid");
		//栏目id
		String channelid=getParam(params, "channelid");
		//栏目id
		String channelparid=getParam(params, "channelparid");
		//显示数量
		int num=getParamInt(params, "num", 10);
		//排序
		String order=getParam(params, "order","1");
		//标题长度
		int titleLen=getParamInt(params, "titleLen",0);
		//是否按点击热度查询
		String hot=getParam(params, "hot");
		//日期格式
		String dateFormat=getParam(params, "dateFormat");
		//栏目页面标识
		String channelPagemark=getParam(params, "channelPagemark");
		String channelParPagemark=getParam(params, "channelParPagemark");
		//是否只提取带图片的信息
		String img=getParam(params, "img");
		//当前第几页
		int page=getParamInt(params, "page", 1);
		//最多显示页数
		int pagenum=getParamInt(params, "pagenum", 0);
		//几天内为最新
		int newdays=getParamInt(params, "newdays",0);
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询信息
				Info info=new Info();
				if (siteid.trim().length()>0) {
					info.setSite(siteid);
				}
				if (channelid.trim().length()>0) {
					info.setChannel(channelid);
				}
				if (channelparid.trim().length()>0) {
					List<String> channelids=new ArrayList<String>();
					channelids.add(channelparid);
					init("channelService");
					List<Channel> sonList=channelService.findSon(siteid, channelparid, "1", "");
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
				if (channelPagemark.trim().length()>0) {
					info.setChannelPagemark(channelPagemark);
				}
				if (img.trim().length()>0) {
					info.setImg(img);
				}
				info.setCheckOpenendtime(getParam(params, "checkOpenendtime"));
				String orderSql="";
				if ("1".equals(hot)) {
					orderSql=" clickNum desc,addtime desc ";
				}else {
					if ("1".equals(order)) {
						//固顶有效并降序,发布时间降序(默认)
						orderSql=" isTop desc,addtime desc";
					}
					else if ("2".equals(order)) {
						//固顶有效并降序,发布时间升序
						orderSql=" isTop desc,addtime";
					}
					else if ("3".equals(order)) {
						//发布时间倒序
						orderSql=" addtime desc";
					}
					else if ("4".equals(order)) {
						//发布时间升序
						orderSql=" addtime";
					}
				}
				//获取总数
				int count=infoService.count(info);
				FreemarkerPager pager=new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setUrl("index");
				//如果总页数大于最多显示页数，则设置总页数为最多显示页数
				if (pagenum>0 && pagenum<pager.getTotalPage()) {
					pager.setTotalPage(pagenum);
				}
				List<Info> infoList=infoService.find(info, orderSql, page, num);
				Site site=siteService.findById(siteid);
				if (infoList!=null && infoList.size()>0) {
					for (int i = 0; i < infoList.size(); i++) {
						if (titleLen>0) {
							infoList.get(i).setShowtitleLen(titleLen);
						}
						if (dateFormat.trim().length()>0) {
							infoList.get(i).setDateFormat(dateFormat);
						}
						if (newdays>0 && 
								(DateUtil.differ(infoList.get(i).getAddtime(), new Date())/(1000*60*60*24))<newdays) {
							//判断是否为最新新闻
							infoList.get(i).setIsnew("1");
						}
						//设置sitepath
						if (site!=null) {
							infoList.get(i).setSitepath(env.getDataModel().get("contextPath").toString()
									+"site/"+site.getSourcepath()+"/");
						}
					}
				}
				//如果有下一页，则输入下一页标识
				if (pager.getTotalPage()>page ) {
					env.getOut().write(ChannelService.hasNextPage);
				}
				loopVars[0]=new ArrayModel(infoList.toArray(),new BeansWrapper()); 
				if(loopVars.length>1){
					loopVars[1]=new BeanModel(pager,new BeansWrapper()); 
				}
				body.render(env.getOut());  
			}
		}
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
}