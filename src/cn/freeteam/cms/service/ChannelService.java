package cn.freeteam.cms.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.ChannelMapper;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.ChannelExample;
import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.ChannelExample.Criteria;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.cms.util.HtmlChannelJob;
import cn.freeteam.cms.util.QuartzUtil;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.OperLogUtil;


import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * 
 * <p>Title: ChannelService.java</p>
 * 
 * <p>Description: 栏目相关服务</p>
 * 
 * <p>Date: Jan 23, 2012</p>
 * 
 * <p>Time: 11:49:58 AM</p>
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
public class ChannelService extends BaseService{
	
	public static String hasNextPage="<!--FreeCMS_channel_info_list_hasNextPage-->";

	private ChannelMapper channelMapper;
	private RoleChannelService roleChannelService;
	private HtmlquartzService htmlquartzService;
	private SiteService siteService;
	
	public ChannelService(){
		initMapper("channelMapper");
		init("roleChannelService");
	}
	/**
	 * 查询是否有子数据
	 * @param parId
	 * @return
	 */
	public boolean hasChildren(String parId){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		return channelMapper.countByExample(example)>0;
	}
	/**
	 * 判断是否已存在此页面标识
	 * @param pagemark
	 * @return
	 */
	public boolean hasPagemark(String siteid,String pagemark){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteEqualTo(siteid);
		criteria.andSql(" pagemark='"+pagemark+"' ");
		return channelMapper.countByExample(example)>0;
	}
	/**
	 * 根据站点id,页面标识查询
	 * @param siteid
	 * @param pagemark
	 * @return
	 */
	public Channel findBySitePagemark(String siteid,String pagemark){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteEqualTo(siteid);
		criteria.andSql(" pagemark='"+pagemark+"' ");
		List<Channel> channelList=channelMapper.selectByExampleWithBLOBs(example);
		if (channelList!=null && channelList.size()>0) {
			return channelList.get(0);
		}
		return null;
	}
	/**
	 * 根据站点id,索引号查询
	 * @param siteid
	 * @param pagemark
	 * @return
	 */
	public Channel findBySiteIndexnum(String siteid,Integer indexnum){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteEqualTo(siteid);
		criteria.andSql(" index="+indexnum+" ");
		List<Channel> channelList=channelMapper.selectByExampleWithBLOBs(example);
		if (channelList!=null && channelList.size()>0) {
			return channelList.get(0);
		}
		return null;
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByParWithBLOBs(String siteid,String parid){
		return findByParWithBLOBs(siteid, parid, null,null);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByPar(String siteid,String parid){
		return findByPar(siteid, parid, null,null);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByPar(String siteid,String parid,String state,String navigation){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		if (siteid!=null && siteid.trim().length()>0) {
			criteria.andSiteEqualTo(siteid.trim());
		}
		if (parid!=null && parid.trim().length()>0 && !"par".equals(parid)) {
			criteria.andParidEqualTo(parid.trim());
		}
		if ("par".equals(parid)) {
			criteria.andSql(" (parid is null or parid = '') ");
		}
		if (state!=null && state.trim().length()>0) {
			criteria.andStateEqualTo(state.trim());
		}
		if (navigation!=null && navigation.trim().length()>0) {
			criteria.andNavigationEqualTo(navigation.trim());
		}
		example.setOrderByClause(" orderNum ");
		return channelMapper.selectByExample(example);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByParWithBLOBs(String siteid,String parid,String state,String navigation){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		if (siteid!=null && siteid.trim().length()>0) {
			criteria.andSiteEqualTo(siteid.trim());
		}
		if (parid!=null && parid.trim().length()>0 && !"par".equals(parid)) {
			criteria.andParidEqualTo(parid.trim());
		}
		if ("par".equals(parid)) {
			criteria.andSql(" (parid is null or parid = '') ");
		}
		if (state!=null && state.trim().length()>0) {
			criteria.andStateEqualTo(state.trim());
		}
		if (navigation!=null && navigation.trim().length()>0) {
			criteria.andNavigationEqualTo(navigation.trim());
		}
		example.setOrderByClause(" orderNum ");
		return channelMapper.selectByExampleWithBLOBs(example);
	}
	/**
	 * 根据站点查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findBySite(String siteid,String state,String navigation){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		if (siteid!=null && siteid.trim().length()>0) {
			criteria.andSiteEqualTo(siteid.trim());
		}
		if (state!=null && state.trim().length()>0) {
			criteria.andStateEqualTo(state.trim());
		}
		if (navigation!=null && navigation.trim().length()>0) {
			criteria.andNavigationEqualTo(navigation.trim());
		}
		example.setOrderByClause(" orderNum ");
		return channelMapper.selectByExample(example);
	}
	/**
	 * 根据站点和角色查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByRoles(String siteid,String roles){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		if (siteid!=null && siteid.trim().length()>0) {
			criteria.andSiteEqualTo(siteid);
		}
		criteria.andSql(" id in (select channelid from freecms_role_channel where roleid in ("+roles+" ))");
		return channelMapper.selectByExample(example);
	}
	/**
	 * 根据站点和角色查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<Channel> findByRolesWithBLOBs(String siteid,String roles){
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		if (siteid!=null && siteid.trim().length()>0) {
			criteria.andSiteEqualTo(siteid);
		}
		criteria.andSql(" id in (select channelid from freecms_role_channel where roleid in ("+roles+" ))");
		return channelMapper.selectByExampleWithBLOBs(example);
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Channel findById(String id){
		return channelMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param channel
	 */
	public void update(Channel channel){
		channelMapper.updateByPrimaryKeyWithBLOBs(channel);
		DBCommit();
	}
	/**
	 * 添加
	 * @param channel
	 */
	public String insert(Channel channel){
		String id=UUID.randomUUID().toString();
		channel.setId(id);
		channelMapper.insert(channel);
		DBCommit();
		return id;
	}

	/**
	 * 删除
	 * @param siteId
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void del(String id,HttpServletRequest request) throws IOException, TemplateException{
		init("htmlquartzService");
		Channel channel=findById(id);
		if (channel!=null) {
			delhtml(channel, request);
			delPar(id,request);
			channelMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	/**
	 * 递归删除
	 * @param parId
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void delPar(String parId,HttpServletRequest request) throws IOException, TemplateException{
		ChannelExample example=new ChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		List<Channel> channelList=channelMapper.selectByExample(example);
		if (channelList!=null && channelList.size()>0) {
			for (int i = 0; i < channelList.size(); i++) {
				delhtml(channelList.get(i), request);
				delPar(channelList.get(i).getId(),request);
			}
		}
		try {
			delHtmlChannelJob(parId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		htmlquartzService.delByChannelid(parId);
		channelMapper.deleteByPrimaryKey(parId);
	}

	/**
	 * 删除栏目静态页
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void delhtml(Channel channel,HttpServletRequest request) throws IOException, TemplateException{
		if (channel!=null) {
			init("siteService");
			Site site=siteService.findById(channel.getSite());
			if (site!=null) {
				String rootPath=request.getRealPath("/")+"/site/"+site.getSourcepath()+"/"+channel.getFolder()+"/";
				//判断栏目文件夹是否存在
				File channelFolder=new File(rootPath);
				if (channelFolder.exists()) {
					FileUtil.deleteFile(channelFolder);
				}
			}
		}
	}
	/**
	 * 栏目页静态化
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void html(Site site,Channel channel,ServletContext context,HttpServletRequest request,String operuser,int pagenum) throws IOException, TemplateException{
		if (site!=null && channel!=null
				&& site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
			//生成模板位置
			String templetPath="templet/"+site.getIndextemplet().trim()+"/channel.html";
			if (channel.getTemplet()!=null && channel.getTemplet().trim().length()>0) {
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+channel.getTemplet().trim();
			}
			//判断模板文件是否存在
			File templetFile=new File(request.getRealPath("/")+templetPath);
			channel.setSitepath(request.getContextPath()+"/site/"+site.getSourcepath()+"/");
			if (templetFile.exists()) {
				//先生成第一页
				htmlPage(site, channel, context, request, templetPath, 1, operuser,pagenum);
			}
		}
	}
	/**
	 * 栏目页静态化每一页
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void htmlPage(Site site,Channel channel,ServletContext context,HttpServletRequest request,String templetPath,int page,String operuser,int pagenum) throws IOException, TemplateException{
		if (site!=null && channel!=null
				&& site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
			//如果设置了最大生成页数
			if (channel.getMaxpage()>0) {
				pagenum=channel.getMaxpage();
			}
			if (pagenum==0 || (pagenum>0 && pagenum>=page)) {
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				//传递site参数
				data.put("site", site);
				data.put("currChannel", channel);
				data.put("page", page);
				data.put("pagenum", pagenum);
				data.put("contextPath", request.getContextPath()+"/");
				data.put("contextPathNo", request.getContextPath());
				String rootPath=request.getRealPath("/")+"/site/"+site.getSourcepath()+"/"+channel.getFolder()+"/";
				//判断栏目文件夹是否存在
				File channelFolder=new File(rootPath);
				if (!channelFolder.exists()) {
					channelFolder.mkdirs();
				}
				FreeMarkerUtil.createHTML(context, data, 
						templetPath, 
						rootPath+"index"+(page>1?"_"+(page-1):"")+".html");
				OperLogUtil.log(operuser, "栏目静态化:"+channel.getName()+" 第"+page+"页", request);
				String content = FileUtil.readFile(rootPath+"index"+(page>1?"_"+(page-1):"")+".html");
				//如果内容里有<!--hasNextPage-->字符串则需要生成下一页
				if (content.indexOf(hasNextPage)>-1) {
					htmlPage(site, channel, context, request, templetPath, page+1, operuser,pagenum);
				}
			}
		}
	}

	/**
	 * 栏目页静态化
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void html(String siteid,String channelid,ServletContext context) throws IOException, TemplateException{
		init("siteService");
		Site site=siteService.findById(siteid);
		Channel channel=findById(channelid);
		if (site!=null && channel!=null
				&& site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
			//生成模板位置
			String templetPath="templet/"+site.getIndextemplet().trim()+"/channel.html";
			if (channel.getTemplet()!=null && channel.getTemplet().trim().length()>0) {
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+channel.getTemplet().trim();
			}
			//判断模板文件是否存在
			File templetFile=new File(context.getRealPath("/")+templetPath);
			channel.setSitepath(context.getContextPath()+"/site/"+site.getSourcepath()+"/");
			if (templetFile.exists()) {
				//先生成第一页
				htmlPage(site, channel, context,  templetPath, 1);
			}
		}
	}
	/**
	 * 栏目页静态化每一页
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void htmlPage(Site site,Channel channel,ServletContext context,String templetPath,int page) throws IOException, TemplateException{
		if (site!=null && channel!=null
				&& site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
			if (channel.getMaxpage()==0 || (channel.getMaxpage()>0 && channel.getMaxpage()>=page)) {
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				//传递site参数
				data.put("site", site);
				data.put("currChannel", channel);
				data.put("page", page);
				if (channel.getMaxpage()>0) {
					data.put("pagenum", channel.getMaxpage());
				}
				data.put("contextPath", context.getContextPath()+"/");
				data.put("contextPathNo", context.getContextPath());
				String rootPath=context.getRealPath("/")+"/site/"+site.getSourcepath()+"/"+channel.getFolder()+"/";
				//判断栏目文件夹是否存在
				File channelFolder=new File(rootPath);
				if (!channelFolder.exists()) {
					channelFolder.mkdirs();
				}
				FreeMarkerUtil.createHTML(context, data, 
						templetPath, 
						rootPath+"index"+(page>1?"_"+(page-1):"")+".html");
				String content = FileUtil.readFile(rootPath+"index"+(page>1?"_"+(page-1):"")+".html");
				//如果内容里有<!--hasNextPage-->字符串则需要生成下一页
				if (content.indexOf(hasNextPage)>-1) {
					htmlPage(site, channel, context, templetPath, page+1);
				}
			}
		}
	}
	/**
	 * 递归生成静态树
	 * @param content
	 * @param siteid
	 * @param parid
	 * @param state
	 * @param navigation
	 */
	public void createTree(StringBuffer content,String adminRoles,String siteid,String roleid,String parid,String state,String navigation,String type){
		List<Channel> channeList=findByPar(siteid, parid, state, navigation);
		if (channeList!=null && channeList.size()>0) {
			content.append("<ul>");
			for (int i = 0; i < channeList.size(); i++) {
				content.append("<li>");
				//判断管理员是否有此栏目权限
				if ("admin".equals(adminRoles) || roleChannelService.haves(adminRoles, channeList.get(i).getId())) {
					if ("checkbox".equals(type)) {
						content.append("<input type='checkbox' onclick='channelTreeClick(this)' name='channelTree' value='"+channeList.get(i).getId()+"' ");
						//判断是否有权限
						if (roleChannelService.have(roleid, channeList.get(i).getId())) {
							content.append(" checked ");
						}
						content.append("/>");
					}
				}
				content.append(channeList.get(i).getName());
				createTree(content, adminRoles,siteid,roleid, channeList.get(i).getId(), state, navigation, type);
				content.append("</li>");
			}
			content.append("</ul>");
		}
	}
	/**
	 * 更新栏目页静态化调度任务
	 * @param site
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	public void updateHtmlChannelJob(ServletContext servletContext,Site site,Channel channel,Htmlquartz htmlquartz) throws SchedulerException, ParseException{
		if (channel!=null) {
			 Trigger trigger = QuartzUtil.getScheduler().getTrigger("HtmlChannelTrigger"+channel.getId(),"HtmlChannelTrigger");  
			 if(trigger != null){  
				//停止触发器
				 QuartzUtil.getScheduler().pauseTrigger("HtmlChannelTrigger"+channel.getId(),"HtmlChannelTrigger");
				//移除触发器
				 QuartzUtil.getScheduler().unscheduleJob("HtmlChannelTrigger"+channel.getId(),"HtmlChannelTrigger"); 
				//删除任务 
				 QuartzUtil.getScheduler().deleteJob("HtmlChannelJob"+channel.getId(),"HtmlChannelJob");
			 }
			 //创建任务
			JobDetail jobDetail = null;
			//站点静态化调度
			jobDetail = new JobDetail("HtmlChannelJob"+channel.getId(), "HtmlChannelJob",HtmlChannelJob.class);
			trigger = new CronTrigger("HtmlChannelTrigger"+channel.getId(), "HtmlChannelTrigger");
			if (jobDetail!=null && trigger!=null) {
				//设置参数
				jobDetail.getJobDataMap().put("siteid", site.getId());
				jobDetail.getJobDataMap().put("channelid", channel.getId());
				jobDetail.getJobDataMap().put("servletContext", servletContext);
				//设置触发器
				String triggerStr=QuartzUtil.getTriggerStr(htmlquartz);
				if (triggerStr.trim().length()>0) {
					((CronTrigger) trigger).setCronExpression(triggerStr); 
					//添加到调度对列
					QuartzUtil.getScheduler().scheduleJob(jobDetail, trigger);
				}
			}
		}
	}
	/**
	 * 更新栏目页静态化调度任务
	 * @param site
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	public void delHtmlChannelJob(String channelid) throws SchedulerException, ParseException{
		if (channelid!=null) {
			 Trigger trigger = QuartzUtil.getScheduler().getTrigger("HtmlChannelTrigger"+channelid,"HtmlChannelTrigger");  
			 if(trigger != null){  
				//停止触发器
				 QuartzUtil.getScheduler().pauseTrigger("HtmlChannelTrigger"+channelid,"HtmlChannelTrigger");
				//移除触发器
				 QuartzUtil.getScheduler().unscheduleJob("HtmlChannelTrigger"+channelid,"HtmlChannelTrigger"); 
				//删除任务 
				 QuartzUtil.getScheduler().deleteJob("HtmlChannelJob"+channelid,"HtmlChannelJob");
			 }
		}
	}
	/**
	 * 查询栏目路径
	 * @return
	 */
	public List<Channel> findPath(String id){
		List<Channel> channelList=new ArrayList<Channel>();
		channelList=findParPath(id, channelList);
		if (channelList!=null && channelList.size()>0) {
			//把对象倒序，实现栏目级别从父到子
			List<Channel> channelListTemp=new ArrayList<Channel>();
			for (int i =channelList.size()-1; i >=0 ; i--) {
				channelListTemp.add(channelList.get(i));
			}
			channelList=channelListTemp;
		}
		return channelList;
	}
	/**
	 * 查询栏目路径(递归方法)
	 * @return
	 */
	public List<Channel> findParPath(String id,List<Channel> channelList){
		Channel channel=findById(id);
		if (channel!=null) {
			channelList.add(channel);
			//如果有父栏目则递归提取
			if (channel.getParid()!=null && channel.getParid().trim().length()>0) {
				findParPath(channel.getParid(), channelList);
			}
		}
		return channelList;
	}
	/**
	 * 查询所有子栏目(pagemark)
	 * @param siteid
	 * @param parid
	 * @param state
	 * @param navigation
	 * @return
	 */
	public List<Channel> findSonByPagemark(String siteid,String pagemark,String state,String navigation){
		List<Channel> list=new ArrayList<Channel>();
		Channel channel=findBySitePagemark(siteid, pagemark);
		if (channel!=null) {
			return findSonPro(list,siteid, channel.getId(), state, navigation);
		}
		return list;
	}
	/**
	 * 查询所有子栏目
	 * @param siteid
	 * @param parid
	 * @param state
	 * @param navigation
	 * @return
	 */
	public List<Channel> findSon(String siteid,String parid,String state,String navigation){
		List<Channel> list=new ArrayList<Channel>();
		return findSonPro(list,siteid, parid, state, navigation);
	}
	/**
	 * 查询所有子栏目（递归）
	 * @param siteid
	 * @param parid
	 * @param state
	 * @param navigation
	 * @return
	 */
	public List<Channel> findSonPro(List<Channel> list,String siteid,String parid,String state,String navigation){
		List<Channel> sonlist=findByPar(siteid, parid, state, navigation);
		if (sonlist!=null && sonlist.size()>0) {
			for (int i = 0; i < sonlist.size(); i++) {
				list.add(sonlist.get(i));//添加到总集合中
				//处理子栏目
				findSonPro(list, siteid, sonlist.get(i).getId(), state, navigation);
			}
		}
		return list;
	}
	public ChannelMapper getChannelMapper() {
		return channelMapper;
	}

	public void setChannelMapper(ChannelMapper channelMapper) {
		this.channelMapper = channelMapper;
	}
	public RoleChannelService getRoleChannelService() {
		return roleChannelService;
	}
	public void setRoleChannelService(RoleChannelService roleChannelService) {
		this.roleChannelService = roleChannelService;
	}
	public HtmlquartzService getHtmlquartzService() {
		return htmlquartzService;
	}
	public void setHtmlquartzService(HtmlquartzService htmlquartzService) {
		this.htmlquartzService = htmlquartzService;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
}
