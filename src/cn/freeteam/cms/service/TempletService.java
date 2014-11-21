package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.TempletMapper;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletExample;
import cn.freeteam.cms.model.TempletExample.Criteria;


/**
 * 
 * <p>Title: TempletService.java</p>
 * 
 * <p>Description: 模板服务</p>
 * 
 * <p>Date: Feb 12, 2012</p>
 * 
 * <p>Time: 9:00:56 PM</p>
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
public class TempletService extends BaseService{

	private TempletMapper templetMapper;
	public TempletService(){
		initMapper("templetMapper");
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
	public List<Templet> find(Templet templet,String order,int currPage,int pageSize){
		TempletExample example=new TempletExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(templet, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return templetMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 * @param info
	 * @param order
	 * @param currPage
	 * @param pageSize
	 * @param loginAdmin
	 * @return
	 */
	public List<Templet> findAll(Templet templet,String order){
		TempletExample example=new TempletExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(templet, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return templetMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Templet templet){
		TempletExample example=new TempletExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(templet, criteria);
		return templetMapper.countByExample(example);
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Templet templet,Criteria criteria){
		if (templet!=null ) {
			if (templet.getName()!=null && templet.getName().trim().length()>0) {
				criteria.andNameLike("%"+templet.getName()+"%");
			}
			if (templet.getNoDel()!=null && templet.getNoDel().trim().length()>0) {
				criteria.andStateNotEqualTo(Templet.STATE_DEL);
			}
			if (templet.getAdduser()!=null && templet.getAdduser().trim().length()>0) {
				criteria.andAdduserEqualTo(templet.getAdduser());
			}
			if (templet.getUsesites()!=null && templet.getUsesites().trim().length()>0) {
				if ("newSite".equals(templet.getUsesites())) {
					criteria.andSql(" usesites='' ");
				}else {
					criteria.andSql(" (usesites like '%"+templet.getUsesites()+";%' or usesites='') ");
				}
			}
		}
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Templet findById(String id){
		return templetMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param templet
	 */
	public void update(Templet templet){
		templetMapper.updateByPrimaryKey(templet);
		DBCommit();
	}
	/**
	 * 添加
	 * @param templet
	 * @return
	 */
	public String add(Templet templet){
		templet.setId(UUID.randomUUID().toString());
		templetMapper.insert(templet);
		DBCommit();
		return templet.getId();
	}
	//set and get 

	public TempletMapper getTempletMapper() {
		return templetMapper;
	}

	public void setTempletMapper(TempletMapper templetMapper) {
		this.templetMapper = templetMapper;
	}
}
