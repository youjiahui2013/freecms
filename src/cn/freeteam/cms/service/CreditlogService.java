package cn.freeteam.cms.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.CreditlogMapper;
import cn.freeteam.cms.model.Creditlog;
import cn.freeteam.cms.model.CreditlogExample;
import cn.freeteam.cms.model.CreditlogExample.Criteria;
import cn.freeteam.util.DateUtil;

/**
 * 
 * <p>Title: CreditlogService.java</p>
 * 
 * <p>Description:积分日志相关服务 </p>
 * 
 * <p>Date: Feb 1, 2013</p>
 * 
 * <p>Time: 7:59:23 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @or freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class CreditlogService extends BaseService{

	private CreditlogMapper creditlogMapper;
	
	public CreditlogService() {
		initMapper("creditlogMapper");
	}

	/**
	 * 分页查询
	 */
	public List<Creditlog> find(Creditlog Creditlog,String order,int currPage,int pageSize){
		CreditlogExample example=new CreditlogExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Creditlog, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return creditlogMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 */
	public List<Creditlog> find(Creditlog Creditlog,String order){
		CreditlogExample example=new CreditlogExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Creditlog, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return creditlogMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Creditlog creditlog){
		CreditlogExample example=new CreditlogExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(creditlog, criteria);
		return creditlogMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Creditlog Creditlog,Criteria criteria){
		if (Creditlog!=null ) {
			if (Creditlog.getCreditruleid()!=null && Creditlog.getCreditruleid().trim().length()>0) {
				criteria.andCreditruleidEqualTo(Creditlog.getCreditruleid().trim());
			}
			if (Creditlog.getMemberid()!=null && Creditlog.getMemberid().trim().length()>0) {
				criteria.andMemberidEqualTo(Creditlog.getMemberid().trim());
			}
			if (Creditlog.getMembername()!=null && Creditlog.getMembername().trim().length()>0) {
				criteria.andMembernameLike("%"+Creditlog.getMembername().trim()+"%");
			}
			if (Creditlog.getCredittimeToday()!=null) {
				criteria.andCredittimeBetween(
						DateUtil.parse(DateUtil.format(Creditlog.getCredittimeToday(), "yyyy-MM-dd")+" 00:00:00", "yyyy-MM-dd HH:mm:ss"),
						DateUtil.parse(DateUtil.format(Creditlog.getCredittimeToday(), "yyyy-MM-dd")+" 23:59:59", "yyyy-MM-dd HH:mm:ss"));
			}
			if (Creditlog.getCredittimeGreater()!=null) {
				criteria.andCredittimeGreaterThan(Creditlog.getCredittimeGreater());
			}
			if (Creditlog.getType()!=null) {
				criteria.andTypeEqualTo(Creditlog.getType());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Creditlog findById(String id){
		return creditlogMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Creditlog Creditlog){
		creditlogMapper.updateByPrimaryKeySelective(Creditlog);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Creditlog Creditlog){
		Creditlog.setId(UUID.randomUUID().toString());
		creditlogMapper.insert(Creditlog);
		DBCommit();
		return Creditlog.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		creditlogMapper.deleteByPrimaryKey(id);
		DBCommit();
	}

	public CreditlogMapper getCreditlogMapper() {
		return creditlogMapper;
	}

	public void setCreditlogMapper(CreditlogMapper creditlogMapper) {
		this.creditlogMapper = creditlogMapper;
	}
}
