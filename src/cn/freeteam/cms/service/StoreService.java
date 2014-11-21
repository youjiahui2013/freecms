package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.StoreMapper;
import cn.freeteam.cms.model.Store;
import cn.freeteam.cms.model.StoreExample;
import cn.freeteam.cms.model.StoreExample.Criteria;

public class StoreService extends BaseService{

	private StoreMapper storeMapper;
	
	public StoreService() {
		initMapper("storeMapper");
	}

	/**
	 * 添加
	 * @param store
	 * @return
	 */
	public String insert(Store store){
		if (store!=null) {
			store.setId(UUID.randomUUID().toString());
			storeMapper.insert(store);
			DBCommit();
			return store.getId();
		}
		return "";
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		storeMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	/**
	 * 分页查询
	 */
	public List<Store> find(Store guestbook,String order,int currPage,int pageSize){
		StoreExample example=new StoreExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(guestbook, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return storeMapper.selectPageByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Store store){
		StoreExample example=new StoreExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(store, criteria);
		return storeMapper.countByExample(example);
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Store store,Criteria criteria){
		if (store!=null ) {
			if (StringUtils.isNotEmpty(store.getMemberid())) {
				criteria.andMemberidEqualTo(store.getMemberid());
			}
			if (StringUtils.isNotEmpty(store.getObjid())) {
				criteria.andObjidEqualTo(store.getObjid());
			}
			if (StringUtils.isNotEmpty(store.getObjtype())) {
				criteria.andObjtypeEqualTo(store.getObjtype());
			}
			if (StringUtils.isNotEmpty(store.getSiteid())) {
				criteria.andSiteidEqualTo(store.getSiteid());
			}
			if (StringUtils.isNotEmpty(store.getChannelid())) {
				criteria.andChannelidEqualTo(store.getChannelid());
			}
			if (StringUtils.isNotEmpty(store.getObjtitle())) {
				criteria.andObjtitleLike("%"+store.getObjtitle()+"%");
			}
			if (StringUtils.isNotEmpty(store.getSitename())) {
				criteria.andSitenameLike("%"+store.getSitename()+"%");
			}
			if (StringUtils.isNotEmpty(store.getChannelname())) {
				criteria.andChannelnameLike("%"+store.getChannelname()+"%");
			}
		}
	}
	public StoreMapper getStoreMapper() {
		return storeMapper;
	}

	public void setStoreMapper(StoreMapper storeMapper) {
		this.storeMapper = storeMapper;
	}
}
