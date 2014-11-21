package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.RoleSiteMapper;
import cn.freeteam.cms.model.RoleSite;
import cn.freeteam.cms.model.RoleSiteExample;
import cn.freeteam.cms.model.RoleSiteExample.Criteria;


public class RoleSiteService extends BaseService{

	private RoleSiteMapper roleSiteMapper;

	public RoleSiteService() {
		initMapper("roleSiteMapper");
	}
	/**
	 * 根据站点和角色判断是否有权限
	 * @param siteid
	 * @param roleid
	 */
	public boolean have(String siteid,String roleid){
		RoleSiteExample example=new RoleSiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		criteria.andRoleidEqualTo(roleid);
		return roleSiteMapper.countByExample(example)>0;
	}
	/**
	 * 根据站点和角色查询
	 * @param siteid
	 * @param roleid
	 */
	public RoleSite findBySiteRole(String siteid,String roleid){
		RoleSiteExample example=new RoleSiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		criteria.andRoleidEqualTo(roleid);
		List<RoleSite> list=roleSiteMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据站点和角色查询
	 * @param siteid
	 * @param roleid
	 */
	public RoleSite findBySiteRoles(String siteid,String roles,String siteadmin){
		RoleSiteExample example=new RoleSiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		criteria.andSql(" roleid in ("+roles+") ");
		if (siteadmin!=null && siteadmin.trim().length()>0) {
			criteria.andSiteadminEqualTo(siteadmin);
		}
		List<RoleSite> list=roleSiteMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据站点和角色删除
	 * @param siteid
	 * @param roleid
	 */
	public void del(String siteid,String roleid){
		RoleSiteExample example=new RoleSiteExample();
		Criteria criteria=example.createCriteria();
		criteria.andSiteidEqualTo(siteid);
		criteria.andRoleidEqualTo(roleid);
		roleSiteMapper.deleteByExample(example);
		DBCommit();
	}
	/**
	 * 根据站点和角色保存
	 * @param siteid
	 * @param roleid
	 */
	public void save(String siteid,String roleid,String siteadmin){
		RoleSite roleSite=new RoleSite();
		roleSite.setId(UUID.randomUUID().toString());
		roleSite.setSiteid(siteid);
		roleSite.setRoleid(roleid);
		if (siteadmin!=null && siteadmin.trim().length()>0) {
			roleSite.setSiteadmin(siteadmin);
		}else {
			roleSite.setSiteadmin("0");
		}
		roleSiteMapper.insert(roleSite);
		DBCommit();
	}
	
	public RoleSiteMapper getRoleSiteMapper() {
		return roleSiteMapper;
	}

	public void setRoleSiteMapper(RoleSiteMapper roleSiteMapper) {
		this.roleSiteMapper = roleSiteMapper;
	}
}
