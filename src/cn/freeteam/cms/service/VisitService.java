package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.VisitMapper;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.InfoExample;
import cn.freeteam.cms.model.Visit;
import cn.freeteam.cms.model.VisitExample;
import cn.freeteam.cms.model.VisitExample.Criteria;

public class VisitService extends BaseService{

	private VisitMapper visitMapper;
	
	public VisitService() {
		initMapper("visitMapper");
	}

	/**
	 * 添加
	 * @return
	 */
	public String add(Visit visit){
		visit.setId(UUID.randomUUID().toString());
		visitMapper.insert(visit);
		DBCommit();
		return visit.getId();
	}
	/**
	 * 栏目访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> channelVisit(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.channelVisit(example);
	}
	/**
	 * 栏目访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> channelVisit(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.channelVisitPage(example);
	}
	/**
	 * 栏目访问统计
	 * @param visit
	 * @return
	 */
	public int channelVisitCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.channelVisitCount(example);
	}
	/**
	 * 栏目访问合计
	 * @param visit
	 * @return
	 */
	public int channelVisitSum(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.channelVisitSum(example);
	}
	/**
	 * 站点访问合计
	 * @param visit
	 * @return
	 */
	public int siteVisitSum(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.siteVisitSum(example);
	}
	/**
	 * 站点访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> siteVisit(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.siteVisit(example);
	}
	/**
	 * 站点访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> siteVisit(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.siteVisitPage(example);
	}
	/**
	 * 站点访问统计
	 * @param visit
	 * @return
	 */
	public int siteVisitCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.siteVisitCount(example);
	}

	/**
	 * 信息访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> infoVisit(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.infoVisit(example);
	}
	/**
	 * 信息访问统计 
	 * @param visit
	 * @return
	 */
	public List<Visit> infoVisit(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.infoVisitPage(example);
	}
	/**
	 * 信息访问统计
	 * @param visit
	 * @return
	 */
	public int infoVisitCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.infoVisitCount(example);
	}
	/**
	 * 信息访问合计
	 * @param visit
	 * @return
	 */
	public int infoVisitSum(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.infoVisitSum(example);
	}
	/**
	 * 访问频率统计 年 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitYear(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitYear(example);
	}
	/**
	 * 访问频率统计 年 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitYear(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.visitYearPage(example);
	}
	/**
	 * 访问频率统计 年 
	 * @param visit
	 * @return
	 */
	public int visitYearCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitYearCount(example);
	}

	/**
	 * 访问频率统计 月 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitMonth(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitMonth(example);
	}
	/**
	 * 访问频率统计 月 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitMonth(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.visitMonthPage(example);
	}
	/**
	 * 访问频率统计 月 
	 * @param visit
	 * @return
	 */
	public int visitMonthCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitMonthCount(example);
	}

	/**
	 * 访问频率统计 日 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitDay(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitDay(example);
	}
	/**
	 * 访问频率统计 日 
	 * @param visit
	 * @return
	 */
	public List<Visit> visitDay(Visit visit,int currPage,int pageSize){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return visitMapper.visitDayPage(example);
	}
	/**
	 * 访问频率统计 日 
	 * @param visit
	 * @return
	 */
	public int visitDayCount(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitDayCount(example);
	}
	/**
	 * 访问频率统计 周
	 * @param visit
	 * @return
	 */
	public List<Visit> visitWeek(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitWeek(example);
	}
	/**
	 * 访问合计
	 * @param visit
	 * @return
	 */
	public int visitSum(Visit visit){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		return visitMapper.visitSum(example);
	}
	/**
	 * 处理查询条件
	 * @param visit
	 * @param criteria
	 */
	public void proSearchParam(Visit visit,Criteria criteria){
		if (visit!=null ) {
			if (visit.getChannelname()!=null && visit.getChannelname().trim().length()>0) {
				criteria.andSql(" c.name like '%"+visit.getChannelname().trim()+"%'");
			}
			if (visit.getSitename()!=null && visit.getSitename().trim().length()>0) {
				criteria.andSql(" s.name like '%"+visit.getSitename().trim()+"%'");
			}
			if (visit.getInfoname()!=null && visit.getInfoname().trim().length()>0) {
				criteria.andSql(" i.title like '%"+visit.getInfoname().trim()+"%'");
			}
			if (StringUtils.isNotEmpty(visit.getSiteid())) {
				criteria.andVisitSiteidEqualTo(visit.getSiteid());
			}
			if (visit.getStarttime()!=null) {
				criteria.andVisitAddtimeGreaterThanOrEqualTo(visit.getStarttime());
			}
			if (visit.getEndtime()!=null) {
				criteria.andVisitAddtimeLessThanOrEqualTo(visit.getEndtime());
			}
			if ("channel".equals(visit.getStatType())) {
				criteria.andChannelidIsNotNull();
			}else if ("site".equals(visit.getStatType())) {
				criteria.andSiteidIsNotNull();
			}else if ("info".equals(visit.getStatType())) {
				criteria.andInfoidIsNotNull();
			}
		}
	}
	/**
	 * 统计
	 * @param visit
	 * @return
	 */
	public int count(Visit visit,boolean cache){
		VisitExample example=new VisitExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(visit, criteria);
		if (cache) {
			return visitMapper.countByExampleCache(example);
		}
		return visitMapper.countByExample(example);
	}
	public VisitMapper getVisitMapper() {
		return visitMapper;
	}

	public void setVisitMapper(VisitMapper visitMapper) {
		this.visitMapper = visitMapper;
	}
}
