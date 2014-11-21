package cn.freeteam.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.dao.DemoMapper;
import cn.freeteam.model.Demo;
import cn.freeteam.model.DemoExample;
import cn.freeteam.model.DemoExample.Criteria;
/**
 * 
 * <p>Title: DemoService.java</p>
 * 
 * <p>Description: 演示功能服务</p>
 * 
 * <p>Date: Jun 19, 2013</p>
 * 
 * <p>Time: 1:42:25 PM</p>
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
public class DemoService extends BaseService{

	private DemoMapper demoMapper;
	
	public DemoService() {
		initMapper("demoMapper");
	}

	public DemoMapper getDemoMapper() {
		return demoMapper;
	}

	public void setDemoMapper(DemoMapper demoMapper) {
		this.demoMapper = demoMapper;
	}
	

	/**
	 * 分页查询
	 */
	public List<Demo> find(Demo demo,String order,int currPage,int pageSize){
		DemoExample example=new DemoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(demo, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return demoMapper.selectPageByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Demo demo){
		DemoExample example=new DemoExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(demo, criteria);
		return demoMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Demo demo,Criteria criteria){
		if (demo!=null ) {
			if (demo.getTextdemo()!=null && demo.getTextdemo().trim().length()>0) {
				criteria.andTextdemoLike("%"+demo.getTextdemo().trim()+"%");
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Demo findById(String id){
		return demoMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Demo demo){
		demoMapper.updateByPrimaryKeySelective(demo);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Demo demo){
		demo.setId(UUID.randomUUID().toString());
		demoMapper.insert(demo);
		DBCommit();
		return demo.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		demoMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
}
