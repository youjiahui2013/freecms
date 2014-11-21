package cn.freeteam.service;

import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.dao.RoleUserMapper;
import cn.freeteam.model.RoleUser;
import cn.freeteam.model.RoleUserExample;
import cn.freeteam.model.RoleUserExample.Criteria;


public class RoleUserService extends BaseService{

	private RoleUserMapper roleUserMapper;
	
	public RoleUserService(){
		initMapper("roleUserMapper");
	}
	/**
	 * 判断是用户是否有角色权限
	 * @return
	 */
	public boolean haveRoleUser(String role,String user){
		RoleUserExample example=new RoleUserExample();
		Criteria criteria=example.createCriteria();
		criteria.andUsersEqualTo(user);
		criteria.andRolesEqualTo(role);
		return roleUserMapper.countByExample(example)>0;
	}
	

	/**
	 * 添加关联
	 * @param role
	 * @param user
	 * @return
	 */
	public int add(String role,String user){
		RoleUser roleUser=new RoleUser();
		roleUser.setRoles(role);
		roleUser.setUsers(user);
		roleUser.setId(UUID.randomUUID().toString());
		int id= roleUserMapper.insert(roleUser);
		DBCommit();
		return id;
	}
	/**
	 * 删除关联
	 * @param role
	 * @param user
	 * @return
	 */
	public void del(String role,String user){
		RoleUserExample example=new RoleUserExample();
		Criteria criteria=example.createCriteria();
		criteria.andUsersEqualTo(user);
		criteria.andRolesEqualTo(role);
		roleUserMapper.deleteByExample(example);
		DBCommit();
	}
	public RoleUserMapper getRoleUserMapper() {
		return roleUserMapper;
	}

	public void setRoleUserMapper(RoleUserMapper roleUserMapper) {
		this.roleUserMapper = roleUserMapper;
	}
}
