package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.ApplyopenMapper;
import cn.freeteam.cms.model.Applyopen;
import cn.freeteam.cms.model.ApplyopenExample;
import cn.freeteam.cms.model.ApplyopenExample.Criteria;

public class ApplyopenService extends BaseService{

	private ApplyopenMapper applyopenMapper;
	
	public ApplyopenService() {
		initMapper("applyopenMapper");
	}
	

	/**
	 * 添加
	 * @param Applyopen
	 * @return
	 */
	public String insert(Applyopen applyopen){
		applyopen.setId(UUID.randomUUID().toString());
		applyopenMapper.insert(applyopen);
		DBCommit();
		return applyopen.getId();
	}

	/**
	 * 分页查询
	 */
	public List<Applyopen> find(Applyopen applyopen,String order,int currPage,int pageSize,boolean cache){
		ApplyopenExample example=new ApplyopenExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(applyopen, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		if (cache) {
			return applyopenMapper.selectPageByExampleCache(example);
		}else {
			return applyopenMapper.selectPageByExample(example);
		}
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Applyopen findById(String id){
		return applyopenMapper.selectByPrimaryKey(id);
	}
	/**
	 * 根据querycode查询
	 * @param id
	 * @return
	 */
	public Applyopen findByQuerycode(String querycode,boolean cache){
		if (cache) {
			return applyopenMapper.selectByQuerycodeCache(querycode);
		}else {
			return applyopenMapper.selectByQuerycode(querycode);
		}
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Applyopen Applyopen,boolean cache){
		ApplyopenExample example=new ApplyopenExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Applyopen, criteria);
		if (cache) {
			return applyopenMapper.countByExampleCache(example);
		}else {
			return applyopenMapper.countByExample(example);
		}
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Applyopen Applyopen,Criteria criteria){
		if (Applyopen!=null ) {
			if (Applyopen.getQuerycode()!=null && Applyopen.getQuerycode().trim().length()>0) {
				criteria.andQuerycodeLike("%"+Applyopen.getQuerycode().trim()+"%");
			}
			if (Applyopen.getName()!=null && Applyopen.getName().trim().length()>0) {
				criteria.andNameLike("%"+Applyopen.getName().trim()+"%");
			}
			if (Applyopen.getLpname()!=null && Applyopen.getLpname().trim().length()>0) {
				criteria.andLpnameLike("%"+Applyopen.getLpname().trim()+"%");
			}
			if (Applyopen.getUserid()!=null && Applyopen.getUserid().trim().length()>0) {
				criteria.andUseridEqualTo(Applyopen.getUserid().trim());
			}
			if (Applyopen.getState()!=null && Applyopen.getState().trim().length()>0) {
				if ("1".equals(Applyopen.getState())) {
					criteria.andStateEqualTo("1");
				}else if ("0".equals(Applyopen.getState())) {
					criteria.andSql(" (state is null or state='0') ");
				}
			}
			if (Applyopen.getType()!=null && Applyopen.getType().trim().length()>0) {
				if ("1".equals(Applyopen.getType())) {
					criteria.andTypeEqualTo("1");
				}else if ("0".equals(Applyopen.getType())) {
					criteria.andSql(" (type is null or type='0') ");
				}
			}
			
		}
	}
	/**
	 * 更新
	 * @param templet
	 */
	public void update(Applyopen Applyopen){
		applyopenMapper.updateByPrimaryKeySelective(Applyopen);
		DBCommit();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		applyopenMapper.deleteByPrimaryKey(id);
		DBCommit();
	}

	public ApplyopenMapper getApplyopenMapper() {
		return applyopenMapper;
	}


	public void setApplyopenMapper(ApplyopenMapper ApplyopenMapper) {
		this.applyopenMapper = ApplyopenMapper;
	}
}
