package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.GuestbookMapper;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.model.GuestbookExample;
import cn.freeteam.cms.model.GuestbookExample.Criteria;
/**
 * 
 * <p>Title: GuestbookService.java</p>
 * 
 * <p>Description: 留言本相关服务</p>
 * 
 * <p>Date: Feb 4, 2013</p>
 * 
 * <p>Time: 7:50:54 PM</p>
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
public class GuestbookService extends BaseService{

	private GuestbookMapper guestbookMapper;
	
	public GuestbookService() {
		initMapper("guestbookMapper");
	}
	

	/**
	 * 分页查询
	 */
	public List<Guestbook> find(Guestbook guestbook,String order,int currPage,int pageSize,boolean cache){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		if (cache) {
			return guestbookMapper.selectPageByExampleCache(example);
		}else {
			return guestbookMapper.selectPageByExample(example);
		}
	}
	/**
	 * 统计
	 * @param guestbook
	 * @return
	 */
	public int count(Guestbook Guestbook,boolean cache){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Guestbook, criteria);
		if (cache) {
			return guestbookMapper.countByExampleCache(example);
		}else {
			return guestbookMapper.countByExample(example);
		}
	}

	/**
	 * 处理查询条件
	 * @param guestbook
	 * @param criteria
	 */
	public void proSearchParam(Guestbook guestbook,Criteria criteria){
		if (guestbook!=null ) {
			if (StringUtils.isNotEmpty(guestbook.getSiteid())) {
				criteria.andSiteidEqualTo(guestbook.getSiteid());
			}
			if (StringUtils.isNotEmpty(guestbook.getTitle())) {
				criteria.andTitleLike("%"+guestbook.getTitle()+"%");
			}
			if (StringUtils.isNotEmpty(guestbook.getMembername())) {
				criteria.andMembernameLike("%"+guestbook.getMembername()+"%");
			}
			if (StringUtils.isNotEmpty(guestbook.getReusername())) {
				criteria.andReusernameLike("%"+guestbook.getReusername()+"%");
			}
			if (StringUtils.isNotEmpty(guestbook.getName())) {
				criteria.andNameLike("%"+guestbook.getName()+"%");
			}
			if (StringUtils.isNotEmpty(guestbook.getState())) {
				criteria.andStateEqualTo(guestbook.getState());
			}
			if (StringUtils.isNotEmpty(guestbook.getMemberid())) {
				criteria.andMemberidEqualTo(guestbook.getMemberid());
			}
			if (guestbook.getStarttime()!=null) {
				criteria.andAddtimeGreaterThanOrEqualTo(guestbook.getStarttime());
			}
			if (guestbook.getEndtime()!=null) {
				criteria.andAddtimeLessThanOrEqualTo(guestbook.getEndtime());
			}
			if (StringUtils.isNotEmpty(guestbook.getSitename())) {
				criteria.andSitenameLike("%"+guestbook.getSitename()+"%");
			}
			if (StringUtils.isNotEmpty(guestbook.getGuestbookstate())) {
				criteria.andGuestbookstateEqualTo(guestbook.getGuestbookstate());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Guestbook findById(String id){
		return guestbookMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Guestbook Guestbook){
		guestbookMapper.updateByPrimaryKeySelective(Guestbook);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Guestbook Guestbook){
		Guestbook.setId(UUID.randomUUID().toString());
		guestbookMapper.insert(Guestbook);
		DBCommit();
		return Guestbook.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		guestbookMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	

	/**
	 * 留言频率统计 年 
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateYear(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateYear(example);
	}
	/**
	 * 留言频率统计 年 
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateYear(Guestbook guestbook,int currPage,int pageSize){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return guestbookMapper.guestbookUpdateYearPage(example);
	}
	/**
	 * 留言频率统计 年
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateYearCount(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateYearCount(example);
	}

	/**
	 * 留言频率合计 年
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateYearSum(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateYearSum(example);
	}
	
	

	/**
	 * 留言频率统计 月
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateMonth(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateMonth(example);
	}
	/**
	 * 留言频率统计 月 
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateMonth(Guestbook guestbook,int currPage,int pageSize){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return guestbookMapper.guestbookUpdateMonthPage(example);
	}
	/**
	 * 留言频率统计 月
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateMonthCount(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateMonthCount(example);
	}
	/**
	 * 留言频率合计 月
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateMonthSum(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateMonthSum(example);
	}
	

	/**
	 * 留言频率统计 日
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateDay(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateDay(example);
	}
	/**
	 * 留言频率统计 日 
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateDay(Guestbook guestbook,int currPage,int pageSize){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return guestbookMapper.guestbookUpdateDayPage(example);
	}
	/**
	 * 留言频率统计 日
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateDayCount(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateDayCount(example);
	}
	/**
	 * 留言频率合计 日
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateDaySum(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateDaySum(example);
	}

	/**
	 * 留言频率合计 周
	 * @param guestbook
	 * @return
	 */
	public int guestbookUpdateWeekSum(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateWeekSum(example);
	}

	/**
	 * 留言频率统计 周
	 * @param guestbook
	 * @return
	 */
	public List<Guestbook> guestbookUpdateWeek(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.guestbookUpdateWeek(example);
	}

	/**
	 * 站点留言统计 
	 * @param info
	 * @return
	 */
	public List<Guestbook> sysSiteGuestbook(Guestbook guestbook,int currPage,int pageSize){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return guestbookMapper.sysSiteGuestbookPage(example);
	}
	/**
	 * 站点留言统计 
	 * @param Guestbook
	 * @return
	 */
	public List<Guestbook> sysSiteGuestbook(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.sysSiteGuestbook(example);
	}
	/**
	 * 站点留言统计 
	 * @param Guestbook
	 * @return
	 */
	public int sysSiteGuestbookCount(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.sysSiteGuestbookCount(example);
	}
	/**
	 * 站点留言合计 
	 * @param Guestbook
	 * @return
	 */
	public int sysSiteGuestbookSum(Guestbook guestbook){
		GuestbookExample example=new GuestbookExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		return guestbookMapper.sysSiteGuestbookSum(example);
	}
	

	public GuestbookMapper getGuestbookMapper() {
		return guestbookMapper;
	}

	public void setGuestbookMapper(GuestbookMapper guestbookMapper) {
		this.guestbookMapper = guestbookMapper;
	}
}
