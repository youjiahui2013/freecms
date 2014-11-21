package cn.freeteam.cms.service;

import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.RoleChannelMapper;
import cn.freeteam.cms.model.RoleChannel;
import cn.freeteam.cms.model.RoleChannelExample;
import cn.freeteam.cms.model.RoleChannelExample.Criteria;


public class RoleChannelService extends BaseService{

	private RoleChannelMapper roleChannelMapper;
	
	public RoleChannelService() {
		initMapper("roleChannelMapper");
	}
	/**
	 * 根据角色删除
	 * @param roleid
	 */
	public void delByRole(String roleid){
		RoleChannelExample example=new RoleChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andRoleidEqualTo(roleid);
		roleChannelMapper.deleteByExample(example);
		DBCommit();
	}
	/**
	 * 保存
	 * @param roleid
	 * @param channelid
	 */
	public void save(String roleid,String channelid){
		RoleChannel roleChannel=new RoleChannel();
		roleChannel.setId(UUID.randomUUID().toString());
		roleChannel.setRoleid(roleid);
		roleChannel.setChannelid(channelid);
		roleChannelMapper.insert(roleChannel);
		DBCommit();
	}
	/**
	 * 判断是否有权限
	 * @param roleid
	 * @param channelid
	 */
	public boolean have(String roleid,String channelid){
		RoleChannelExample example=new RoleChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andRoleidEqualTo(roleid);
		criteria.andChannelidEqualTo(channelid);
		return roleChannelMapper.countByExample(example)>0;
	}
	/**
	 * 判断是否有权限
	 * @param roleid
	 * @param channelid
	 */
	public boolean haves(String roles,String channelid){
		RoleChannelExample example=new RoleChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andSql(" roleid in ("+roles+") ");
		criteria.andChannelidEqualTo(channelid);
		return roleChannelMapper.countByExample(example)>0;
	}

	public RoleChannelMapper getRoleChannelMapper() {
		return roleChannelMapper;
	}

	public void setRoleChannelMapper(RoleChannelMapper roleChannelMapper) {
		this.roleChannelMapper = roleChannelMapper;
	}
}
