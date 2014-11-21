package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.SensitiveMapper;
import cn.freeteam.cms.model.Sensitive;
import cn.freeteam.cms.model.SensitiveExample;
import cn.freeteam.cms.model.SensitiveExample.Criteria;
/**
 * 
 * <p>Title: SensitiveService.java</p>
 * 
 * <p>Description: 敏感词相关服务</p>
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
public class SensitiveService extends BaseService{

	private SensitiveMapper sensitiveMapper;
	
	public SensitiveService() {
		initMapper("sensitiveMapper");
	}

	/**
	 * 替换处理
	 * @param content
	 * @return
	 */
	public String replace(String content){
		List<Sensitive> list=find(null, "", true);
		if (list!=null && list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				content=content.replace(list.get(i).getSensitiveword(), list.get(i).getReplaceto());
			}
		}
		return content;
	}
	/**
	 * 分页查询
	 */
	public List<Sensitive> find(Sensitive sensitive,String order,int currPage,int pageSize){
		SensitiveExample example=new SensitiveExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(sensitive, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return sensitiveMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 */
	public List<Sensitive> find(Sensitive Sensitive,String order,boolean cache){
		SensitiveExample example=new SensitiveExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Sensitive, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		if (cache) {
			return sensitiveMapper.selectByExampleCache(example);
		}
		return sensitiveMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Sensitive Sensitive){
		SensitiveExample example=new SensitiveExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Sensitive, criteria);
		return sensitiveMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Sensitive sensitive,Criteria criteria){
		if (sensitive!=null ) {
			if (sensitive.getSensitiveword()!=null && sensitive.getSensitiveword().trim().length()>0) {
				criteria.andSensitivewordLike("%"+sensitive.getSensitiveword().trim()+"%");
			}
			if (sensitive.getReplaceto()!=null && sensitive.getReplaceto().trim().length()>0) {
				criteria.andReplacetoLike("%"+sensitive.getReplaceto().trim()+"%");
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Sensitive findById(String id){
		return sensitiveMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Sensitive sensitive){
		sensitiveMapper.updateByPrimaryKeySelective(sensitive);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Sensitive sensitive){
		sensitive.setId(UUID.randomUUID().toString());
		sensitiveMapper.insert(sensitive);
		DBCommit();
		return sensitive.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		sensitiveMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	public SensitiveMapper getSensitiveMapper() {
		return sensitiveMapper;
	}

	public void setSensitiveMapper(SensitiveMapper sensitiveMapper) {
		this.sensitiveMapper = sensitiveMapper;
	}
}
