package cn.freeteam.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;


import cn.freeteam.base.BaseAction;
import cn.freeteam.dao.RolesMapper;
import cn.freeteam.dao.UsersMapper;
import cn.freeteam.model.Roles;
import cn.freeteam.model.RolesExample;
import cn.freeteam.model.Unit;
import cn.freeteam.model.Users;
import cn.freeteam.model.UsersExample;
import cn.freeteam.model.RolesExample.Criteria;
import cn.freeteam.service.RoleService;
import cn.freeteam.service.RoleUserService;
import cn.freeteam.service.UnitService;
import cn.freeteam.service.UnitUserService;
import cn.freeteam.service.UserService;
import cn.freeteam.util.DES;
import cn.freeteam.util.MD5;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;


/**
 * 
 * <p>Title: UserAction.java</p>
 * 
 * <p>Description: 用户服务</p>
 * 
 * <p>Date: Dec 16, 2011</p>
 * 
 * <p>Time: 5:50:43 PM</p>
 * 
 * <p>Copyright: 2011</p>
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
public class UserAction extends BaseAction{

	private UserService userService;
	private RoleService roleService;
	private RoleUserService roleUserService;
	private UnitUserService unitUserService;
	private UnitService unitService;
	
	private UsersMapper usersMapper;
	private RolesMapper rolesMapper;
	
	private Users user;
	private Roles role;
	private String delRoles;
	private String roleid;
	private String rolename;
	private String unitid;
	private String unitname;
	
	private String operUser;
	
	private List<Users> userList;
	private List<Roles> roleList;
	private String order;

	private String logContent;
	private String ids;
	private String names;
	private String state;

	private String CurrentPassword="";
	private String NewPassword="";
	private String msg="";
	public UserAction(){
		init("userService","roleService","roleUserService","unitUserService","unitService");
		initMapper("usersMapper");
	}
	//修改密码
	public String pwd(){
		HttpSession session=getHttpSession();
		Users users=(Users)session.getAttribute("loginAdmin");
		try {
			//先判断原密码是否正确
			if (!MD5.MD5(CurrentPassword).equals(users.getPwd())) {
				msg="当前密码不正确!";
			}
			//如果新密码不等于旧密码时才修改，减少数据库操作
			else {
				if (!CurrentPassword.equals(NewPassword)) {
					users.setPwd(MD5.MD5(NewPassword));
					userService.update(users);
					session.setAttribute("loginAdmin", users);
				}
				msg="密码更新成功!";
			}
		} catch (Exception e) {
			DBProException(e);
			msg="密码更新失败:"+e.toString()+"!";
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		return "pwd";
	}
	/**
	 * 个人资料页面
	 * @return
	 */
	public String profile(){
		return "profile";
	}
	//个人资料
	public String profileDo(){
		HttpSession session=getHttpSession();
		Users users=(Users)session.getAttribute("loginAdmin");
		try {
			users.setBirthday(user.getBirthday());
			users.setEmail(user.getEmail());
			users.setName(user.getName());
			users.setSex(user.getSex());
			users.setTel(user.getTel());
			users.setMobilephone(user.getMobilephone());
			userService.update(users);
			users.setBirthdayStr(users.getBirthdayStr());
			session.setAttribute("loginAdmin", users);
			msg="个人资料更新成功!";
		} catch (Exception e) {
			DBProException(e);
			msg="个人资料更新失败:"+e.toString()+"!";
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		return profile();
	}
	public String add(){
		if (user!=null) {
			try {
				initMapper();
				//判断用户是否存在
				if (userService.have(user)>0) {
					write("<script>alert('此用户已存在!');history.back();</script>", "GBK");
					return null;
				}
				user.setId(UUID.randomUUID().toString());
				user.setAddTime(new Date());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				user.setIsok("1");
				user.setLoginFailNum(0);
				user.setPwd(MD5.MD5(user.getPwd()));
				user.setLastestlogintime(sdf.parse("2010-01-01 00:00:00"));
				user.setLastlogintime(sdf.parse("2010-01-01 00:00:00"));
				if (user.getBirthday()==null) {
					user.setBirthday(new Date());
				}
				user.setUnitIds(unitid);
				userService.save(user);
				//如果设置有角色则添加角色人员关联
				if (roleid!=null && roleid.trim().length()>0) {
					String roleids[]=roleid.split(";");
					if (roleids!=null && roleids.length>0) {
						Roles role;
						for (int i = 0; i < roleids.length; i++) {
							if (roleids[i].trim().length()>0) {
								if (!roleUserService.haveRoleUser(roleids[i], user.getId())) {
									role=roleService.findById(roleids[i]);
									if (role!=null) {
										roleUserService.add(roleids[i], user.getId());
										OperLogUtil.log(getLoginName(), "添加角色人员关联("+role.getName()+" "+user.getLoginname()+")成功", getHttpRequest());
									}
								}
							}
						}
					}
				}
				//如果设置有单位则添加单位人员关联
				if (unitid!=null && unitid.trim().length()>0) {
					String unitids[]=unitid.split(";");
					
					if (unitids!=null && unitids.length>0) {
						Unit unit;
						for (int i = 0; i < unitids.length; i++) {
							if (unitids[i].trim().length()>0) {
								if (!unitUserService.haveUnitUser(unitids[i], user.getId())) {
									unit=unitService.findById(unitids[i]);
									if (unit!=null) {
										unitUserService.add(unitids[i], user.getId());
										OperLogUtil.log(getLoginName(), "添加单位人员关联("+unit.getName()+" "+user.getLoginname()+")成功", getHttpRequest());
									}
								}
							}
						}
					}
				}
				msg="添加用户("+user.getLoginname()+")成功!";
			} catch (Exception e) {
				DBProException(e);
				msg="添加用户("+user.getLoginname()+")失败!";
			}
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		write("<script>alert('"+msg+"');location.href='userEdit.jsp?pageFuncId="+pageFuncId+"';</script>", "GBK");
		return null;
	}
	/**
	 * 用户列表
	 * @return
	 */
	public String list(){
		UsersExample example=new UsersExample();
		cn.freeteam.model.UsersExample.Criteria criteria=example.createCriteria();
		boolean isUnit=false;
		if (!isAdminLogin()) {
			criteria.andSql(" id in (select users from freecms_unit_user where unit in ("+getLoginUnitIdsSql()+")) ");
		}
		if (user!=null && user.getName()!=null && user.getName().trim().length()>0) {
			criteria.andNameLike("%"+user.getName().trim()+"%");
		}
		if (user!=null && user.getUnitNames()!=null && user.getUnitNames().trim().length()>0) {
			criteria.andUnitNamesLike("%"+user.getUnitNames().trim()+"%");
		}
		if (user!=null && user.getRoleNames()!=null && user.getRoleNames().trim().length()>0) {
			criteria.andRoleNamesLike("%"+user.getRoleNames().trim()+"%");
		}
		if (user!=null && user.getLoginname()!=null && user.getLoginname().trim().length()>0) {
			criteria.andLoginnameLike("%"+user.getLoginname().trim()+"%");
		}
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		initMapper();
		if (isUnit) {
			userList=usersMapper.selectPageByExampleUnits(example);
			totalCount=usersMapper.countByExampleUnits(example);
		}else {
			userList=usersMapper.selectPageByExample(example);
			totalCount=usersMapper.countByExample(example);
		}
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("user.name");
		pager.appendParam("user.loginname");
		pager.appendParam("user.companyname");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.appendParam("order");
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("user_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 用户授权
	 * @return
	 */
	public String auth(){
		if (user!=null && user.getId()!=null && user.getId().trim().length()>0) {
			//查询所有有效角色
			RolesExample example=new RolesExample();
			Criteria criteria=example.createCriteria();
			if (!isAdminLogin()) {
				//普通用户登录只查询自己有的角色
				criteria.andSql(" (id in (select roles from freecms_role_user where users='"+getLoginAdmin().getId()+"') or id in (select id from freecms_roles where adduser='"+getLoginName()+"'))");
			}
			criteria.andIsokEqualTo("1");
			initMapper("rolesMapper");
			roleList=rolesMapper.selectByExample(example);
			//如此用户有此角色权限则默认选中
			if(roleList!=null && roleList.size()>0){
				for (int i = 0; i < roleList.size(); i++) {
					if (roleUserService.haveRoleUser(roleList.get(i).getId(), user.getId())) {
						roleList.get(i).setHaveRoleUser(true);
					}else {
						roleList.get(i).setHaveRoleUser(false);
					}
				}
			}
		}
		return "auth";
	}
	/**
	 * 授权处理
	 * @return
	 */
	public String authDo(){

		if (user!=null && user.getId()!=null && user.getId().trim().length()>0
				&& roleid!=null ) {
			String[] roleids=roleid.split(";");
			String[] rolenames=rolename.split(";");
			String[] delRoleids=delRoles.split(";");
			initMapper("usersMapper");
			Users authUser=usersMapper.selectByPrimaryKey(user.getId());
			if (authUser!=null && roleids!=null && roleids.length>0) {
				try {
					if (roleids!=null && roleids.length>0) {
						for (int i = 0; i < roleids.length; i++) {
							if (roleids[i].trim().length()>0) {
								//检查是否已有权限
								if (!roleUserService.haveRoleUser(roleids[i], user.getId())) {
									//无则添加
									roleUserService.add(roleids[i], user.getId());
									OperLogUtil.log(getLoginName(), "给人员"+authUser.getName()+"("+authUser.getLoginname()+")"+"授权("+rolenames[i]+")", getHttpRequest());
								}
							}
						}
					}
					if (delRoleids!=null && delRoleids.length>0) {
						String delRole[];
						for (int i = 0; i < delRoleids.length; i++) {
							if(delRoleids[i].trim().length()>0){
								delRole=delRoleids[i].split(",");
								roleUserService.del(delRole[0], user.getId());
								OperLogUtil.log(getLoginName(), "人员授权删除("+user.getLoginname()+" "+delRole[1]+")", getHttpRequest());
							}
						}
					}
					//设置用户的角色名称
					authUser.setRoleNames(rolename);
					usersMapper.updateByPrimaryKey(authUser);
					DBCommit();
					write("1"+rolename, "UTF-8");
				} catch (Exception e) {
					DBProException(e);
					write("fail", "UTF-8");
				}
			}
		}
		return null;
	}


	//删除
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				try {
					initMapper();
					for (int i = 0; i < idArr.length; i++) {
						if (idArr[i].trim().length()>0) {
							//删除用户
							usersMapper.deleteByPrimaryKey(idArr[i].trim());
						}
					}
					msg="1";
					logContent="删除用户("+names+")成功!";
				} catch (Exception e) {
					DBProException(e);
					msg="删除用户失败!";
					logContent="删除用户("+names+")失败:"+e.toString()+"!";
				}
			}
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		write(msg, "UTF-8");
		return null;
	}

	//重置密码
	public String resetPwd(){
		if (ids!=null && ids.trim().length()>0) {
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				try {
					initMapper();
					//重置密码
					String uuid=UUID.randomUUID().toString();
					String resetPwd=uuid.substring(0, 6);
					user=new Users();
					user.setPwd(MD5.MD5(resetPwd));
					for (int i = 0; i < idArr.length; i++) {
						if (idArr[i].trim().length()>0) {
							user.setId(idArr[i].trim());
							usersMapper.updatePwd(user);
						}
					}
					msg="1密码重置为:"+resetPwd;
					logContent="密码重置("+names+")成功!";
					DBCommit();
				} catch (Exception e) {
					DBProException(e);
					msg="密码重置失败!";
					logContent="密码重置("+names+")失败:"+e.toString()+"!";
				}
			}
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		write(msg, "UTF-8");
		return null;
	}

	public String ajaxEdit(){
		if(user!=null&&user.getId()!=null && user.getId().trim().length()>0){
			initMapper();
			user=usersMapper.selectByPrimaryKey(user.getId().trim());
		}
		return "ajaxEdit";
	}/**
	 * ajax修改
	 * @return
	 */
	public String ajaxEditDo(){
		if (user!=null && user.getId()!=null && user.getId().trim().length()>0) {
			//更新
			try {
				initMapper();
				Users obj=usersMapper.selectByPrimaryKey(user.getId());
				if (obj!=null) {
					obj.setName(user.getName());
					obj.setSex(user.getSex());
					obj.setIsmail(user.getIsmail());
					obj.setBirthday(user.getBirthday());
					obj.setTel(user.getTel());
					obj.setMobilephone(user.getMobilephone());
					obj.setEmail(user.getEmail());
					obj.setUnitIds(unitid);
					obj.setUnitNames(user.getUnitNames());
					usersMapper.updateByPrimaryKey(obj);
					//如果设置有单位则添加单位人员关联
					if (unitid!=null && unitid.trim().length()>0) {
						String unitids[]=unitid.split(";");
						
						if (unitids!=null && unitids.length>0) {
							Unit unit;
							for (int i = 0; i < unitids.length; i++) {
								if (unitids[i].trim().length()>0) {
									if (!unitUserService.haveUnitUser(unitids[i], obj.getId())) {
										unit=unitService.findById(unitids[i]);
										if (unit!=null) {
											unitUserService.add(unitids[i], obj.getId());
											OperLogUtil.log(getLoginName(), "编辑人员时添加单位人员关联("+unit.getName()+" "+obj.getLoginname()+")成功", getHttpRequest());
										}
									}
								}
							}
						}
					}
					DBCommit();
					msg="1编辑用户成功!";
					logContent="编辑用户("+user.getLoginname()+")成功!";
				}else {
					msg="0此用户不存在!";
				}
			} catch (Exception e) {
				DBProException(e);
				msg="0编辑用户失败!";
				logContent="编辑用户("+user.getLoginname()+")失败:"+e.toString()+"!";
			}
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		write(msg, "UTF-8");
		return null;
	}
	//启用/禁用
	public String state(){
		if (ids!=null && ids.trim().length()>0) {
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				String oper="1".equals(state)?"启用":"禁用";
				try {
					initMapper();
					for (int i = 0; i < idArr.length; i++) {
						if (idArr[i].trim().length()>0) {
							usersMapper.updateById(idArr[i].trim(), state);
						}
					}
					
					msg="1";
					logContent=""+oper+"用户("+names+")成功!";
				} catch (Exception e) {
					DBProException(e);
					msg=oper+"用户失败!";
					logContent=""+oper+"用户("+names+")失败:"+e.toString()+"!";
				}
			}
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		write(msg, "UTF-8");
		return null;
	}
	//setter and getter
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}


	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public List<Roles> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Roles> roleList) {
		this.roleList = roleList;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public RoleUserService getRoleUserService() {
		return roleUserService;
	}

	public void setRoleUserService(RoleUserService roleUserService) {
		this.roleUserService = roleUserService;
	}

	public String getDelRoles() {
		return delRoles;
	}

	public void setDelRoles(String delRoles) {
		this.delRoles = delRoles;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getCurrentPassword() {
		return CurrentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		CurrentPassword = currentPassword;
	}
	public String getNewPassword() {
		return NewPassword;
	}
	public void setNewPassword(String newPassword) {
		NewPassword = newPassword;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUnitid() {
		return unitid;
	}
	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}
	public String getUnitname() {
		return unitname;
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public UnitUserService getUnitUserService() {
		return unitUserService;
	}
	public void setUnitUserService(UnitUserService unitUserService) {
		this.unitUserService = unitUserService;
	}
	public UnitService getUnitService() {
		return unitService;
	}
	public void setUnitService(UnitService unitService) {
		this.unitService = unitService;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public UsersMapper getUsersMapper() {
		return usersMapper;
	}
	public void setUsersMapper(UsersMapper usersMapper) {
		this.usersMapper = usersMapper;
	}
	public RolesMapper getRolesMapper() {
		return rolesMapper;
	}
	public void setRolesMapper(RolesMapper rolesMapper) {
		this.rolesMapper = rolesMapper;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
