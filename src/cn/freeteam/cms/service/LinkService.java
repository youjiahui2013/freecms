package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.LinkMapper;
import cn.freeteam.cms.model.Link;
import cn.freeteam.cms.model.LinkExample;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.LinkExample.Criteria;


/**
 * 
 * <p>Title: LinkService.java</p>
 * 
 * <p>Description: 链接服务</p>
 * 
 * <p>Date: May 15, 2012</p>
 * 
 * <p>Time: 3:09:47 PM</p>
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
public class LinkService extends BaseService{

	private LinkMapper linkMapper;
	
	public LinkService(){
		initMapper("linkMapper");
	}

	/**
	 * 更新
	 * @param templet
	 */
	public void update(Link link){
		linkMapper.updateByPrimaryKey(link);
		DBCommit();
	}
	/**
	 * 添加
	 * @param link
	 * @return
	 */
	public String add(Link link){
		link.setId(UUID.randomUUID().toString());
		linkMapper.insert(link);
		DBCommit();
		return link.getId();
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Link findById(String id){
		return linkMapper.selectByPrimaryKey(id);
	}
	/**
	 * 检验是否已存在页面标识
	 * @param siteid
	 * @param type
	 * @param isClass
	 * @return
	 */
	public boolean hasPagemark(String siteid,String type,boolean isClass,String pagemark){
		LinkExample example=new LinkExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteEqualTo(siteid);
		criteria.andTypeEqualTo(type);
		criteria.andPagemarkEqualTo(pagemark);
		if (isClass) {
			criteria.andSql(" (parid is null or parid = '') ");
		}else {
			criteria.andSql(" (parid is not null and parid != '') ");
		}
		return linkMapper.countByExample(example)>0;
	}
	/**
	 * 分页查询
	 */
	public List<Link> find(Link link,String order,int currPage,int pageSize){
		LinkExample example=new LinkExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(link, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return linkMapper.selectPageByExample(example);
	}
	/**
	 * 分页查询
	 */
	public List<Link> findAll(Link link,String order){
		LinkExample example=new LinkExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(link, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return linkMapper.selectByExample(example);
	}

	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Link link){
		LinkExample example=new LinkExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(link, criteria);
		return linkMapper.countByExample(example);
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Link link,Criteria criteria){
		if (link!=null ) {
			if (link.getSite()!=null && link.getSite().trim().length()>0) {
				criteria.andSiteEqualTo(link.getSite());
			}
			if (link.getName()!=null && link.getName().trim().length()>0) {
				criteria.andNameLike("%"+link.getName().trim()+"%");
			}
			if (link.getPagemark()!=null && link.getPagemark().trim().length()>0) {
				criteria.andPagemarkLike("%"+link.getPagemark().trim()+"%");
			}
			if ("1".equals(link.getIsClass())) {
				criteria.andSql(" (parid is null or parid = '') ");
			}else {
				criteria.andSql(" (parid is not null and parid != '') ");
			}
			if (link.getClassName()!=null && link.getClassName().trim().length()>0) {
				criteria.andSql(" (parid in (select id from freecms_link where name like '%"+link.getClassName()+"%')) ");;
			}
			if (link.getIsok()!=null && link.getIsok().trim().length()>0) {
				criteria.andIsokEqualTo(link.getIsok());
			}
			if (link.getParid()!=null && link.getParid().trim().length()>0) {
				criteria.andParidEqualTo(link.getParid());
			}
			if (link.getType()!=null && link.getType().trim().length()>0) {
				criteria.andTypeEqualTo(link.getType());
			}
			if (link.getPagemarks()!=null && link.getPagemarks().trim().length()>0) {
				criteria.andSql(" (pagemark in ('"+link.getPagemarks().replaceAll(",", "','")+"')) ");
			}
			if (link.getClassPagemarks()!=null && link.getClassPagemarks().trim().length()>0) {
				criteria.andSql(" (parid in (select id from freecms_link where pagemark in ('"+link.getClassPagemarks().replaceAll(",", "','")+"'))) ");
			}
		}
	}
	/**
	 * 删除分类
	 * @param id
	 */
	public void delClass(String id){
		if (id!=null && id.trim().length()>0) {
			//先删除子链接
			LinkExample example=new LinkExample();
			Criteria criteria=example.createCriteria();
			criteria.andParidEqualTo(id);
			linkMapper.deleteByExample(example);
			//删除此分类
			linkMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	/**
	 * 删除
	 * @param id
	 */
	public void del(String id){
		if (id!=null && id.trim().length()>0) {
			//删除此分类
			linkMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	public LinkMapper getLinkMapper() {
		return linkMapper;
	}

	public void setLinkMapper(LinkMapper linkMapper) {
		this.linkMapper = linkMapper;
	}
}
