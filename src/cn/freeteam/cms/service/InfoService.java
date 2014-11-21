package cn.freeteam.cms.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.InfoMapper;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.InfoExample;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.InfoExample.Criteria;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.model.Users;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.SqlUtil;


import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * 
 * <p>Title: InfoService.java</p>
 * 
 * <p>Description: 信息服务</p>
 * 
 * <p>Date: Feb 7, 2012</p>
 * 
 * <p>Time: 1:55:57 PM</p>
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
public class InfoService extends BaseService{

	private InfoMapper infoMapper;
	private ChannelService channelService;
	private SiteService siteService;
	
	public InfoService(){
		initMapper("infoMapper");
		init("channelService","siteService");
	}

	/**
	 * 生成信息页
	 * @param id
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void html(String id,ServletContext context,String contextPath,HttpServletRequest request,String operuser) throws IOException, TemplateException{
		//查询信息
		Info info=findById(id);
		if (info!=null) {
			Channel channel=channelService.findById(info.getChannel());
			String templet="info.html";
			//判断info是否有信息页模板
			if (info.getTemplet()!=null && info.getTemplet().trim().length()>0) {
				templet=info.getTemplet();
			}else if (channel!=null && channel.getContenttemplet()!=null && channel.getContenttemplet().trim().length()>0) {
				templet=channel.getContenttemplet();
			}
			Site site=siteService.findById(info.getSite());
			if (site!=null && site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				//传递site参数
				data.put("site", site);
				channel.setSitepath(contextPath+"site/"+site.getSourcepath()+"/");
				data.put("currChannel", channel);
				data.put("currInfo", info);
				data.put("contextPath", contextPath);
				data.put("contextPathNo", request.getContextPath());
				//生成目录
				String rootFolder=request.getRealPath("/")+"/site/"+site.getSourcepath()+"/"+info.getChannelFolder()+"/info/"+(info.getAddtime().getYear()+1900)+"/";
				File folder=new File(rootFolder);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				FreeMarkerUtil.createHTML(context, data, 
						"templet/"+site.getIndextemplet().trim()+"/"+templet, 
						rootFolder+info.getHtmlFileName()+".html");
				OperLogUtil.log(operuser, "信息页静态化:"+info.getTitle(), request);
			}
		}
	}
	/**
	 * 删除信息页
	 * @param id
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void delhtml(String id,HttpServletRequest request) throws IOException, TemplateException{
		//查询信息
		Info info=findById(id);
		if (info!=null) {
			Site site=siteService.findById(info.getSite());
			if (site!=null ) {
				//删除静态文件
				String htmlfile=request.getRealPath("/")+"/site/"+site.getSourcepath()+
				"/"+info.getChannelFolder()+"/info/"+(info.getAddtime().getYear()+1900)+"/"+info.getHtmlFileName()+".html";
				File file=new File(htmlfile);
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}
	/**
	 * 分页查询
	 * @param info
	 * @param order
	 * @param currPage
	 * @param pageSize
	 * @param loginAdmin
	 * @return
	 */
	public List<Info> find(Info info,String order,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.selectPageByExample(example);
	}
	/**
	 * 查询所有
	 * @param info
	 * @param order
	 * @return
	 */
	public List<Info> findAll(Info info,String order){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return infoMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.countByExample(example);
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Info info,Criteria criteria){
		if (info!=null ) {
			info.setSearchKey(SqlUtil.replace(info.getSearchKey()));
			if (info.getSearchKey()!=null && info.getSearchKey().trim().length()>0) {
				criteria.andSql("(title like '%"+info.getSearchKey().trim()
						+"%' or shortTitle like '%"+info.getSearchKey().trim()
						+"%' or i.description like '%"+info.getSearchKey().trim()
						+"%' or tags like '%"+info.getSearchKey().trim()
						+"%' )");
			}
			info.setTags(SqlUtil.replace(info.getTags()));
			if (StringUtils.isNotEmpty(info.getTags())) {
				String[] tags=info.getTags().split(",");
				if (tags!=null && tags.length>0) {
					StringBuilder sb=new StringBuilder();
					for (int i = 0; i < tags.length; i++) {
						if (tags[i].trim().length()>0) {
							if (sb.toString().length()>0) {
								sb.append(" or ");
							}
							sb.append(" title like '%"+tags[i].trim()
									+"%' or shortTitle like '%"+tags[i].trim()
									+"%' or i.description like '%"+tags[i].trim()
									+"%' or tags like '%"+tags[i].trim()
									+"%' ");
						}
					}
					criteria.andSql("("+sb.toString()+")");
				}
			}
			if (info.getId()!=null && info.getId().trim().length()>0) {
				criteria.andInfoIdEqualTo(info.getId());
			}
			if (info.getSite()!=null && info.getSite().trim().length()>0) {
				criteria.andInfoSiteEqualTo(info.getSite());
			}
			if ("1".equals(info.getCheckOpenendtime())) {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				criteria.andSql(" (openendtime is null or openendtime = '' or opentimetype='1' or openendtime>='"+sdf.format(new Date())+"') ");
			}
			if (info.getChannel()!=null && info.getChannel().trim().length()>0) {
				criteria.andChannelEqualTo(info.getChannel());
			}
			if (info.getChannelids()!=null && info.getChannelids().size()>0) {
				criteria.andChannelIn(info.getChannelids());
			}
			info.setChannelParid(SqlUtil.replace(info.getChannelParid()));
			if (info.getChannelParid()!=null && info.getChannelParid().trim().length()>0) {
				criteria.andSql(" i.channel in (select id from freecms_channel where parid ='"+info.getChannelParid().trim()+"') ");
			}
			info.setChannelParPagemark(SqlUtil.replace(info.getChannelParPagemark()));
			if (info.getChannelParPagemark()!=null && info.getChannelParPagemark().trim().length()>0) {
				criteria.andSql(" i.channel in (select id from freecms_channel where parid in (select id from freecms_channel where site='"+info.getSite()+"' and pagemark='"+info.getChannelParPagemark().trim()+"')) ");
			}
			info.setChannelname(SqlUtil.replace(info.getChannelname()));
			if (info.getChannelname()!=null && info.getChannelname().trim().length()>0) {
				criteria.andSql(" c.name like '%"+info.getChannelname().trim()+"%' ");
			}
			if (info.getAdduser()!=null && info.getAdduser().trim().length()>0) {
				criteria.andAdduserEqualTo(info.getAdduser());
			}
			info.setAdduserLike(SqlUtil.replace(info.getAdduserLike()));
			if (info.getAdduserLike()!=null && info.getAdduserLike().trim().length()>0) {
				criteria.andSql(" (u.loginname like '%"+info.getAdduserLike().trim()+"%' or u.name like '%"+info.getAdduserLike().trim()+"%') ");
			}
			info.setSitename(SqlUtil.replace(info.getSitename()));
			if (info.getSitename()!=null && info.getSitename().trim().length()>0) {
				criteria.andSql(" (s.name like '%"+info.getSitename().trim()+"%') ");
			}
			info.setInfosite(SqlUtil.replace(info.getInfosite()));
			if (info.getInfosite()!=null && info.getInfosite().trim().length()>0) {
				criteria.andSql(" (i.site ='"+info.getInfosite().trim()+"') ");
			}
			info.setTitle(SqlUtil.replace(info.getTitle()));
			if (info.getTitle()!=null && info.getTitle().trim().length()>0) {
				criteria.andTitleLike("%"+info.getTitle().trim()+"%");
			}
			if (info.getIstop()!=null && info.getIstop().trim().length()>0) {
				criteria.andIstopEqualTo(info.getIstop());
			}
			if (info.getIssign()!=null && info.getIssign().trim().length()>0) {
				criteria.andIssignEqualTo(info.getIssign());
			}
			if (info.getIscomment()!=null && info.getIscomment().trim().length()>0) {
				criteria.andIscommentEqualTo(info.getIscomment());
			}
			if (info.getTopendtime()!=null) {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				criteria.andSql(" (topendtime>='"+sdf.format(info.getTopendtime())+"' or topendtime is null) ");
			}
			if (info.getNoids()!=null && info.getNoids().trim().length()>0) {
				String[] ids=info.getNoids().split(",");
				List<String> idList=new ArrayList<String>();
				if (ids!=null && ids.length>0) {
					for (int i = 0; i < ids.length; i++) {
						idList.add(ids[i]);
					}
				}
				criteria.andInfoIdNotIn(idList);
			}
			if (info.getChannels()!=null && info.getChannels().length>0) {
				List<String> idList=new ArrayList<String>();
				for (int i = 0; i < info.getChannels().length; i++) {
					idList.add(info.getChannels()[i]);
				}
				criteria.andChannelIn(idList);
			}
			info.setChannelPagemark(SqlUtil.replace(info.getChannelPagemark()));
			if (info.getChannelPagemark()!=null && info.getChannelPagemark().trim().length()>0) {
				criteria.andSql(" (channel in (select id from freecms_channel where site='"+info.getSite()+"' and pagemark='"+info.getChannelPagemark()+"')) ");
			}
			if (info.getImg()!=null && info.getImg().trim().length()>0) {
				criteria.andSql(" (i.img is not null and i.img !='') ");
			}
			if (info.getStarttime()!=null) {
				criteria.andAddtimeGreaterThanOrEqualTo(info.getStarttime());
			}
			if (info.getEndtime()!=null) {
				criteria.andAddtimeLessThanOrEqualTo(info.getEndtime());
			}
			if (info.getInfostarttime()!=null) {
				criteria.andInfoAddtimeGreaterThanOrEqualTo(info.getInfostarttime());
			}
			if (info.getInfoendtime()!=null) {
				criteria.andInfoAddtimeLessThanOrEqualTo(info.getInfoendtime());
			}
			if (info.getInfostarttimeNoeq()!=null) {
				criteria.andInfoAddtimeGreaterThan(info.getInfostarttimeNoeq());
			}
			if (info.getInfoendtimeNoeq()!=null) {
				criteria.andInfoAddtimeLessThan(info.getInfoendtimeNoeq());
			}
			if (info.getInfoStartClicknum()>0) {
				criteria.andInfoClicknumGreaterThan(info.getInfoStartClicknum());
			}
			if (info.getInfoEndClicknum()>0) {
				criteria.andInfoClicknumLessThan(info.getInfoEndClicknum());
			}
			if (info.getInfoendtime()!=null) {
				criteria.andInfoAddtimeLessThanOrEqualTo(info.getInfoendtime());
			}
			if (info.getHtmlIndexnum()>0) {
				criteria.andHtmlIndexnumEqualTo(info.getHtmlIndexnum());
			}
		}
	}
	/**
	 * 添加
	 * @param info
	 * @return
	 */
	public String insert(Info info){
		if (info!=null) {
			info.setId(UUID.randomUUID().toString());
			infoMapper.insert(info);
			DBCommit();
			return info.getId();
		}
		return "";
	}
	/**
	 * 更新
	 * @param info
	 */
	public void update(Info info){
		infoMapper.updateByPrimaryKeyWithBLOBs(info);
		DBCommit();
	}
	/**
	 * 点击
	 * @param info
	 * @return
	 */
	public void click(Info info){
		infoMapper.click(info);
		DBCommit();
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Info findById(String id){
		return infoMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Info findClickById(String id){
		return infoMapper.selectClickByPrimaryKey(id);
	}
	
	public void del(String id){
		infoMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	/**
	 * 工作量统计 
	 * @param info
	 * @return
	 */
	public List<Info> workload(Info info,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.workloadPage(example);
	}
	/**
	 * 工作量统计 
	 * @param info
	 * @return
	 */
	public List<Info> workload(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.workload(example);
	}
	/**
	 * 工作量统计 
	 * @param info
	 * @return
	 */
	public int workloadCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.workloadCount(example);
	}
	/**
	 * 工作量合计 
	 * @param info
	 * @return
	 */
	public int workloadSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.workloadSum(example);
	}
	
	/**
	 * 站点内容统计 
	 * @param info
	 * @return
	 */
	public List<Info> siteStat(Info info,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.siteStatPage(example);
	}
	/**
	 * 站点内容统计 
	 * @param info
	 * @return
	 */
	public List<Info> siteStat(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.siteStat(example);
	}
	/**
	 * 站点内容统计 
	 * @param info
	 * @return
	 */
	public int siteStatCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.siteStatCount(example);
	}
	/**
	 * 站点内容合计 
	 * @param info
	 * @return
	 */
	public int siteStatSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.siteStatSum(example);
	}
	/**
	 * 栏目信息统计 
	 * @param info
	 * @return
	 */
	public List<Info> channelStat(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.channelStat(example);
	}
	/**
	 * 栏目信息统计 
	 * @param info
	 * @return
	 */
	public int channelStatCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.channelStatCount(example);
	}
	/**
	 * 栏目信息合计 
	 * @param info
	 * @return
	 */
	public int channelStatSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.channelStatSum(example);
	}
	/**
	 * 信息更新统计 年 
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateYear(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateYear(example);
	}
	/**
	 * 信息更新统计 年 
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateYear(Info info,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.infoUpdateYearPage(example);
	}
	/**
	 * 信息更新统计 年
	 * @param info
	 * @return
	 */
	public int infoUpdateYearCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateYearCount(example);
	}

	/**
	 * 信息更新合计 年
	 * @param info
	 * @return
	 */
	public int infoUpdateYearSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateYearSum(example);
	}
	
	

	/**
	 * 信息更新统计 月
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateMonth(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateMonth(example);
	}
	/**
	 * 信息更新统计 月 
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateMonth(Info info,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.infoUpdateMonthPage(example);
	}
	/**
	 * 信息更新统计 月
	 * @param info
	 * @return
	 */
	public int infoUpdateMonthCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateMonthCount(example);
	}
	/**
	 * 信息更新合计 月
	 * @param info
	 * @return
	 */
	public int infoUpdateMonthSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateMonthSum(example);
	}
	

	/**
	 * 信息更新统计 日
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateDay(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateDay(example);
	}
	/**
	 * 信息更新统计 日 
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateDay(Info info,int currPage,int pageSize){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return infoMapper.infoUpdateDayPage(example);
	}
	/**
	 * 信息更新统计 日
	 * @param info
	 * @return
	 */
	public int infoUpdateDayCount(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateDayCount(example);
	}
	/**
	 * 信息更新合计 日
	 * @param info
	 * @return
	 */
	public int infoUpdateDaySum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateDaySum(example);
	}

	/**
	 * 信息更新合计 周
	 * @param info
	 * @return
	 */
	public int infoUpdateWeekSum(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateWeekSum(example);
	}

	/**
	 * 信息更新统计 周
	 * @param info
	 * @return
	 */
	public List<Info> infoUpdateWeek(Info info){
		InfoExample example=new InfoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(info, criteria);
		return infoMapper.infoUpdateWeek(example);
	}
	
	//set and get

	public InfoMapper getInfoMapper() {
		return infoMapper;
	}

	public void setInfoMapper(InfoMapper infoMapper) {
		this.infoMapper = infoMapper;
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
