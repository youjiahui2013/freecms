package cn.freeteam.cms.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
import cn.freeteam.cms.dao.SiteMapper;
import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.SiteExample;
import cn.freeteam.cms.model.SiteExample.Criteria;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.cms.util.HtmlChannelJob;
import cn.freeteam.cms.util.HtmlSiteJob;
import cn.freeteam.cms.util.QuartzUtil;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.OperLogUtil;


import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * 
 * <p>Title: SiteService.java</p>
 * 
 * <p>Description: 绔欑偣鐩稿叧鏈嶅姟</p>
 * 
 * <p>Date: Jan 21, 2012</p>
 * 
 * <p>Time: 2:31:27 PM</p>
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
public class SiteService extends BaseService{

	private SiteMapper siteMapper;
	private HtmlquartzService htmlquartzService;
	
	
	public SiteService(){
		initMapper("siteMapper");
	}
	
	
	/**
	 * 鏌ヨ鏄惁鏈夊瓙鏁版嵁
	 * @param parId
	 * @return
	 */
	public boolean hasChildren(String parId){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		return siteMapper.countByExample(example)>0;
	}
	/**
	 * 鏌ヨ瀛愮珯鐐�
	 * @param parid
	 * @return
	 */
	public List<Site> selectByParId(String parid){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parid);
		example.setOrderByClause("ordernum");
		return siteMapper.selectByExample(example);
	}
	/**
	 * 鏌ヨ绗竴涓瓙绔欑偣
	 * @param parid
	 * @return
	 */
	public Site selectFirstByParId(String parid){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parid);
		example.setOrderByClause("ordernum");
		example.setCurrPage(1);
		example.setPageSize(1);
		List<Site> list = siteMapper.selectPageByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 鏌ヨ瑙掕壊鍙鐞嗙珯鐐�
	 * @param parid
	 * @return
	 */
	public List<Site> selectByRoles(String roles){
		if (roles!=null && roles.trim().length()>0) {
			SiteExample example=new SiteExample();
			Criteria criteria=example.createCriteria();
			criteria.andSql(" id in (select siteid from freecms_role_site where roleid in ("+roles+" ))");
			example.setOrderByClause("ordernum");
			return siteMapper.selectByExample(example);
		}
		return null;
	}
	/**
	 * 鏌ヨ鐢ㄦ埛绗竴涓彲绠＄悊绔欑偣
	 * @param parid
	 * @return
	 */
	public Site selectFirstByRoles(String roles){
		if (roles!=null && roles.trim().length()>0) {
			SiteExample example=new SiteExample();
			Criteria criteria=example.createCriteria();
			criteria.andSql(" id in (select siteid from freecms_role_site where roleid in ("+roles+" ))");
			example.setOrderByClause("ordernum");
			example.setCurrPage(1);
			example.setPageSize(1);
			List<Site> list = siteMapper.selectPageByExample(example);
			if (list!=null && list.size()>0) {
				return list.get(0);
			}
		}
		return null;
	}
	/**
	 * 鏍规嵁id鏌ヨ
	 * @param id
	 * @return
	 */
	public Site findById(String id){
		return siteMapper.selectByPrimaryKey(id);
	}
	/**
	 * 鏍规嵁id鏌ヨ
	 * @param id
	 * @return
	 */
	public Site findByDomain(String domain,boolean cache){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSitedomainEqualTo(domain);
		example.setCurrPage(1);
		example.setPageSize(1);
		List<Site> list=null;
		if (cache) {
			list = siteMapper.selectPageByExampleCache(example);
		}else {
			list = siteMapper.selectPageByExample(example);
		}
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 鏍规嵁sourcepath鏌ヨ
	 * @param id
	 * @return
	 */
	public Site findBySourcepath(String sourcepath){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSourcepathEqualTo(sourcepath);
		example.setCurrPage(1);
		example.setPageSize(1);
		List<Site> list = siteMapper.selectPageByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 鐢熸垚棣栭〉
	 * @param id
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void html(String id,ServletContext context,String contextPath,HttpServletRequest request,String operuser) throws IOException, TemplateException{
		//鏌ヨ绔欑偣
		Site site=findById(id);
		if (site!=null && site.getIndextemplet()!=null 
				&& site.getIndextemplet().trim().length()>0) {
			//鐢熸垚闈欐�侀〉闈�
			Map<String,Object> data=new HashMap<String,Object>();
			//浼犻�抯ite鍙傛暟
			data.put("site", site);
			data.put("contextPath", contextPath);
			data.put("contextPathNo", request.getContextPath());
			FreeMarkerUtil.createHTML(context, data, 
					"templet/"+site.getIndextemplet().trim()+"/index.html", 
					request.getRealPath("/")+"/site/"+site.getSourcepath()+"/index.html");
			OperLogUtil.log(operuser, "棣栭〉闈欐�佸寲:"+site.getName(), request);
		}
	}
	/**
	 * 鐢熸垚棣栭〉
	 * @param id
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void html(String id,ServletContext context) throws IOException, TemplateException{
		//鏌ヨ绔欑偣
		Site site=findById(id);
		if (site!=null && site.getIndextemplet()!=null 
				&& site.getIndextemplet().trim().length()>0) {
			//鐢熸垚闈欐�侀〉闈�
			Map<String,Object> data=new HashMap<String,Object>();
			//浼犻�抯ite鍙傛暟
			data.put("site", site);
			data.put("contextPath", context.getContextPath()+"/");
			FreeMarkerUtil.createHTML(context, data, 
					"templet/"+site.getIndextemplet().trim()+"/index.html", 
					context.getRealPath("/")+"/site/"+site.getSourcepath()+"/index.html");
		}
	}
	/**
	 * 闈欐�佹枃浠�
	 * @param id
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void delhtml(String id,HttpServletRequest request) throws IOException, TemplateException{
		//鏌ヨ绔欑偣
		Site site=findById(id);
		if (site!=null ) {
			//鍒犻櫎闈欐�侀〉闈�
			String rootPath=request.getRealPath("/")+"/site/"+site.getSourcepath();
			//鍒ゆ柇鏍忕洰鏂囦欢澶规槸鍚﹀瓨鍦�
			File folder=new File(rootPath);
			if (folder.exists()) {
				FileUtil.deleteFile(folder);
			}
		}
	}
	/**
	 * 鏌ヨ鏄惁鏈夋鐩綍
	 * @param path
	 * @return
	 */
	public boolean haveSourcePath(String path){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSourcepathEqualTo(path);
		return siteMapper.countByExample(example)>0;
	}

	/**
	 * 鏇存柊
	 * @param site
	 */
	public void update(Site site){
		siteMapper.updateByPrimaryKey(site);
		DBCommit();
	}
	/**
	 * 娣诲姞
	 * @param site
	 * @return
	 */
	public String insert(Site site){
		site.setId(UUID.randomUUID().toString());
		siteMapper.insert(site);
		DBCommit();
		return site.getId();
	}
	/**
	 * 鍒犻櫎
	 * @param siteId
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void del(String siteId,HttpServletRequest request) throws IOException, TemplateException{
		init("htmlquartzService");
		delhtml(siteId, request);
		delPar(siteId, request);
		siteMapper.deleteByPrimaryKey(siteId);
		DBCommit();
	}
	/**
	 * 閫掑綊鍒犻櫎
	 * @param parId
	 */
	public void delPar(String parId,HttpServletRequest request){
		SiteExample example=new SiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		List<Site> siteList=siteMapper.selectByExample(example);
		if (siteList!=null && siteList.size()>0) {
			for (int i = 0; i < siteList.size(); i++) {
				delPar(siteList.get(i).getId(), request);
			}
		}
		//鍒犻櫎闈欐�佸寲璋冨害浠诲姟
		try {
			delHtmlSiteJob(parId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//鍒犻櫎闈欐�佸寲璋冨害鏁版嵁
		htmlquartzService.delBySiteid(parId);
		siteMapper.deleteByPrimaryKey(parId);
	}
	/**
	 * 鏇存柊棣栭〉闈欐�佸寲璋冨害浠诲姟
	 * @param site
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	public void updateHtmlSiteJob(ServletContext servletContext,Site site,Htmlquartz htmlquartz) throws SchedulerException, ParseException{
		if (site!=null) {
			 Trigger trigger = QuartzUtil.getScheduler().getTrigger("HtmlSiteTrigger"+site.getId(),"HtmlSiteTrigger");  
			 if(trigger != null){  
				//鍋滄瑙﹀彂鍣�
				 QuartzUtil.getScheduler().pauseTrigger("HtmlSiteTrigger"+site.getId(),"HtmlSiteTrigger");
				//绉婚櫎瑙﹀彂鍣�
				 QuartzUtil.getScheduler().unscheduleJob("HtmlSiteTrigger"+site.getId(),"HtmlSiteTrigger"); 
				//鍒犻櫎浠诲姟 
				 QuartzUtil.getScheduler().deleteJob("HtmlSiteJob"+site.getId(),"HtmlSiteJob");
			 }
			 //鍒涘缓浠诲姟
			JobDetail jobDetail = null;
			//绔欑偣闈欐�佸寲璋冨害
			jobDetail = new JobDetail("HtmlSiteJob"+site.getId(), "HtmlSiteJob",HtmlSiteJob.class);
			trigger = new CronTrigger("HtmlSiteTrigger"+site.getId(), "HtmlSiteTrigger");
			if (jobDetail!=null && trigger!=null) {
				//璁剧疆鍙傛暟
				jobDetail.getJobDataMap().put("siteid", site.getId());
				jobDetail.getJobDataMap().put("servletContext", servletContext);
				//璁剧疆瑙﹀彂鍣�
				String triggerStr=QuartzUtil.getTriggerStr(htmlquartz);
				if (triggerStr.trim().length()>0) {
					((CronTrigger) trigger).setCronExpression(triggerStr); 
					//娣诲姞鍒拌皟搴﹀鍒�
					QuartzUtil.getScheduler().scheduleJob(jobDetail, trigger);
				}
			}
		}
	}
	/**
	 * 鍒犻櫎棣栭〉闈欐�佸寲璋冨害浠诲姟
	 * @param site
	 * @throws SchedulerException 
	 * @throws ParseException 
	 */
	public void delHtmlSiteJob(String siteid) throws SchedulerException, ParseException{
		if (siteid!=null) {
			 Trigger trigger = QuartzUtil.getScheduler().getTrigger("HtmlSiteTrigger"+siteid,"HtmlSiteTrigger");  
			 if(trigger != null){  
				//鍋滄瑙﹀彂鍣�
				 QuartzUtil.getScheduler().pauseTrigger("HtmlSiteTrigger"+siteid,"HtmlSiteTrigger");
				//绉婚櫎瑙﹀彂鍣�
				 QuartzUtil.getScheduler().unscheduleJob("HtmlSiteTrigger"+siteid,"HtmlSiteTrigger"); 
				//鍒犻櫎浠诲姟 
				 QuartzUtil.getScheduler().deleteJob("HtmlSiteJob"+siteid,"HtmlSiteJob");
			 }
		}
	}
	public SiteMapper getSiteMapper() {
		return siteMapper;
	}

	public void setSiteMapper(SiteMapper siteMapper) {
		this.siteMapper = siteMapper;
	}


	public HtmlquartzService getHtmlquartzService() {
		return htmlquartzService;
	}


	public void setHtmlquartzService(HtmlquartzService htmlquartzService) {
		this.htmlquartzService = htmlquartzService;
	}
}
