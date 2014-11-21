package cn.freeteam.service;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseService;
import cn.freeteam.dao.RoleFuncMapper;
import cn.freeteam.model.RoleFunc;
import cn.freeteam.model.RoleFuncExample;
import cn.freeteam.model.RoleFuncExample.Criteria;
import cn.freeteam.util.MybatisSessionFactory;


/**
 * 关于菜单角色的相关服务
 * @author freeteam
 * 2011-4-8
 */
public class RoleFuncService extends BaseService{

	private RoleFuncMapper roleFuncMapper;
	
	public RoleFuncService(){
		initMapper("roleFuncMapper");
	}
	/**
	 * 检查是否有此记录
	 * @param roleid
	 * @param funcid
	 * @return
	 */
	public boolean haveRoleFunc(String roleid,String funcid){
		initMapper();
		RoleFuncExample example=new RoleFuncExample();
		Criteria criteria=example.createCriteria();
		criteria.andFuncidEqualTo(funcid);
		criteria.andRoleidEqualTo(roleid);
		return roleFuncMapper.countByExample(example)>0;
	}
	/**
	 * 检查是否有此记录
	 * @param roleid
	 * @param funcid
	 * @return
	 */
	public RoleFunc findRoleFunc(String roleid,String funcid){
		initMapper();
		RoleFuncExample example=new RoleFuncExample();
		Criteria criteria=example.createCriteria();
		criteria.andFuncidEqualTo(funcid);
		criteria.andRoleidEqualTo(roleid);
		List<RoleFunc> list=roleFuncMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 添加权限
	 * @param roleid
	 * @param funcid
	 * @return
	 */
	public int addRoleFunc(String roleid,String funcid,String dataLevel){
		initMapper();
		RoleFunc roleFunc=new RoleFunc();
		roleFunc.setFuncid(funcid);
		roleFunc.setRoleid(roleid);
		roleFunc.setId(UUID.randomUUID().toString());
		return roleFuncMapper.insert(roleFunc);
	}
	/**
	 * 删除权限
	 * @param roleid
	 * @param funcid
	 * @return
	 */
	public int delRoleFunc(String roleid,String funcid){
		initMapper();
		RoleFuncExample example=new RoleFuncExample();
		Criteria criteria=example.createCriteria();
		criteria.andFuncidEqualTo(funcid);
		criteria.andRoleidEqualTo(roleid);
		return roleFuncMapper.deleteByExample(example);
	}
	/**
	 * 更新
	 * @param roleFunc
	 * @return
	 */
	public int update(RoleFunc roleFunc){
		initMapper();
		return roleFuncMapper.updateByPrimaryKey(roleFunc);
	}
	public RoleFuncMapper getRoleFuncMapper() {
		return roleFuncMapper;
	}
	public void setRoleFuncMapper(RoleFuncMapper roleFuncMapper) {
		this.roleFuncMapper = roleFuncMapper;
	}
}
