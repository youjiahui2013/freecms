package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.HtmlquartzMapper;
import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.model.HtmlquartzExample;
import cn.freeteam.cms.model.HtmlquartzExample.Criteria;

/**
 * 
 * <p>Title: HtmlquartzService.java</p>
 * 
 * <p>Description: 静态化调度服务类</p>
 * 
 * <p>Date: Jan 23, 2013</p>
 * 
 * <p>Time: 10:20:29 PM</p>
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
public class HtmlquartzService extends BaseService{

	private HtmlquartzMapper htmlquartzMapper;
	
	public HtmlquartzService() {
		initMapper("htmlquartzMapper");
	}


	/**
	 * 更新
	 * @param site
	 */
	public void update(Htmlquartz htmlquartz){
		htmlquartzMapper.updateByPrimaryKeySelective(htmlquartz);
		DBCommit();
	}
	/**
	 * 添加
	 * @param site
	 * @return
	 */
	public String insert(Htmlquartz htmlquartz){
		htmlquartz.setId(UUID.randomUUID().toString());
		htmlquartzMapper.insert(htmlquartz);
		DBCommit();
		return htmlquartz.getId();
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Htmlquartz findById(String id){
		return htmlquartzMapper.selectByPrimaryKey(id);
	}
	/**
	 * 根据siteid查询
	 * @param id
	 * @return
	 */
	public Htmlquartz findBySiteid(String siteid){
		HtmlquartzExample example=new HtmlquartzExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		List<Htmlquartz> list=htmlquartzMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据siteid删除
	 * @param id
	 * @return
	 */
	public void delBySiteid(String siteid){
		HtmlquartzExample example=new HtmlquartzExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		htmlquartzMapper.deleteByExample(example);
		DBCommit();
	}
	/**
	 * 根据channelid查询
	 * @param id
	 * @return
	 */
	public Htmlquartz findByChannelid(String channelid){
		HtmlquartzExample example=new HtmlquartzExample();
		Criteria criteria=example.createCriteria();
		criteria.andChannelidEqualTo(channelid);
		List<Htmlquartz> list=htmlquartzMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据channelid删除
	 * @param id
	 * @return
	 */
	public void delByChannelid(String channelid){
		HtmlquartzExample example=new HtmlquartzExample();
		Criteria criteria=example.createCriteria();
		criteria.andChannelidEqualTo(channelid);
		htmlquartzMapper.deleteByExample(example);
		DBCommit();
	}
	/**
	 * 查询所有有效调度
	 * @return
	 */
	public List<Htmlquartz> findAll(){
		HtmlquartzExample example=new HtmlquartzExample();
		Criteria criteria=example.createCriteria();
		criteria.andTypeNotEqualTo("");
		return htmlquartzMapper.selectByExample(example);
	}
	public HtmlquartzMapper getHtmlquartzMapper() {
		return htmlquartzMapper;
	}

	public void setHtmlquartzMapper(HtmlquartzMapper htmlquartzMapper) {
		this.htmlquartzMapper = htmlquartzMapper;
	}
}
