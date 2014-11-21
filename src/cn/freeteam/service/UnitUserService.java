package cn.freeteam.service;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.dao.UnitUserMapper;
import cn.freeteam.model.UnitUser;
import cn.freeteam.model.UnitUserExample;
import cn.freeteam.model.UnitUserExample.Criteria;



/**
 * 单位用户服务类
 * @author freeteam
 * 2011-4-16
 */
public class UnitUserService extends BaseService{

	private UnitUserMapper unitUserMapper;

	public UnitUserService(){
		initMapper("unitUserMapper");
	}

	public UnitUserMapper getUnitUserMapper() {
		return unitUserMapper;
	}

	public void setUnitUserMapper(UnitUserMapper unitUserMapper) {
		this.unitUserMapper = unitUserMapper;
	}
	

	/**
	 * 判断一个用户与一个单位是否关联
	 * @param unit
	 * @param user
	 * @return
	 */
	public boolean haveUnitUser(String unit,String user){
		UnitUserExample example=new UnitUserExample();
		Criteria criteria=example.createCriteria();
		criteria.andUsersEqualTo(user);
		criteria.andUnitEqualTo(unit);
		return unitUserMapper.countByExample(example)>0;
	}
	/**
	 * 添加关联
	 * @param role
	 * @param user
	 * @return
	 */
	public int add(String unit,String user){
		UnitUser unitUser=new UnitUser();
		unitUser.setUnit(unit);
		unitUser.setUsers(user);
		unitUser.setId(UUID.randomUUID().toString());
		return unitUserMapper.insert(unitUser);
	}
}
