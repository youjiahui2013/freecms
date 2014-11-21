package cn.freeteam.action;

import java.util.List;


import cn.freeteam.base.BaseAction;
import cn.freeteam.dao.RolesMapper;
import cn.freeteam.model.Func;
import cn.freeteam.model.Operbutton;
import cn.freeteam.model.RoleFunc;
import cn.freeteam.model.Roles;
import cn.freeteam.model.RolesExample;
import cn.freeteam.model.RolesExample.Criteria;
import cn.freeteam.service.FuncService;
import cn.freeteam.service.RoleFuncService;
import cn.freeteam.service.RoleService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;





/**
 * 角色相关操作
 * 2011-4-3
 * @author freeteam
 *
 */

public class RoleAction extends BaseAction{

	private Roles role;
	private String msg;
	private String logContent;
	private String order;
	private List<Roles> roleList;
	private String ids;
	private String result;
	private Func func;
	private FuncService funcService;
	private RoleFuncService roleFuncService;
	private RolesMapper rolesMapper;

	private StringBuilder checkIds=new StringBuilder();
	private RoleService roleService;

	public RoleAction(){
		init("roleService","funcService","roleFuncService");
	}
	public String edit(){
		return "edit";
	}
	//添加
	public String add(){
		try {
			//判断角色名称是否已存在
			if (roleService.haveRoleName(role.getName())) {
				write("<script>alert('角色名称已存在!');history.back();</script>", "GBK");
				return null;
			}
			role.setAdduser(getLoginName());
			//添加
			roleService.insert(role);
			msg="添加角色("+role.getName()+")成功!";
			logContent=msg;
			write("<script>alert('"+msg+"');location.href='role_edit.do?pageFuncId="+pageFuncId+"';</script>", "GBK");
			return null;
		} catch (Exception e) {
			DBProException(e);
			msg="添加角色失败!";
			logContent="添加角色("+role.getName()+")失败:"+e.toString()+"!";
		}

		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}
	/**
	 * 角色列表 
	 * @return
	 */
	public String list(){
		if (role==null) {
			role=new Roles();
		}
		if (isAdminLogin()) {
			roleList=roleService.find(role, order, currPage, pageSize,null);
		}else {
			roleList=roleService.find(role, order, currPage, pageSize,getLoginAdmin());
		}
		totalCount=roleService.count(role);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("role.name");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("role_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * ajax编辑角色显示页面
	 * @return
	 */
	public String ajaxEdit(){
		if (role!=null && role.getId()!=null) {
			role=roleService.findById(role.getId());
		}
		return "ajaxEdit";
	}
	/**
	 * ajax编辑角色
	 * @return
	 */
	public String ajaxEditDo(){
		try {
			if (role!=null && role.getId()!=null) {
				Roles  oldrole=roleService.findById(role.getId());
				if (oldrole!=null) {
					//如果修改了名称则判断角色名称是否已存在
					if (!oldrole.getName().equals(role.getName())) {
						//判断角色名称是否已存在
						if (roleService.haveRoleName(role.getName())) {
							write("haveName", "GBK");
							return null;
						}
					}
					//更新
					roleService.update(role);
					logContent="修改角色("+role.getName()+")成功!";
					write("succ", "GBK");
				}
			}
		} catch (Exception e) {
			DBProException(e);
			logContent="修改角色("+role.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}
	/**
	 * 删除
	 * @return
	 */
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			initMapper("rolesMapper");
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							role=roleService.findById(idArr[i]);
							if (role!=null) {
								roleService.del(idArr[i]);
								sb.append(idArr[i]+";");
								logContent="删除角色("+role.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除角色("+role.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "GBK");
		}
		return null;
	}

	/**
	 * 授权页面
	 * @return
	 */
	public String auth(){
		if (role!=null && role.getId()!=null) {
			role=roleService.findById(role.getId());
		}
		return "auth";
	}
	/**
	 * 选择一个菜单
	 * @return
	 */
	public String funcChecked(){
		result="0";
		msg="";
		if (func!=null && func.getId()!=null && func.getId().trim().length()>0 &&
				role!=null && role.getId()!=null && role.getId().trim().length()>0) {
			try {
				role=roleService.findById(role.getId().trim());
				if (role!=null) {
					func=funcService.selectById(func.getId().trim());
					if (func!=null) {
						//对本身进行操作
						if (!roleFuncService.haveRoleFunc(role.getId(), func.getId())) {
							//添加权限
							roleFuncService.addRoleFunc(role.getId(), func.getId(),"");
							logContent="添加角色菜单权限("+role.getName()+"-"+func.getName()+")成功!";
							OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
						}
						//判断所有上级是否已选
						List<Func> parList=funcService.getPars(func.getId().trim(), null);
						if (parList!=null && parList.size()>0) {
							for (int i = 0; i < parList.size(); i++) {
								if (!roleFuncService.haveRoleFunc(role.getId(), parList.get(i).getId())) {
									//添加权限,数据级别默认为个人
									roleFuncService.addRoleFunc(role.getId(), parList.get(i).getId(),"");
									checkIds.append("func");
									checkIds.append(parList.get(i).getId());
									checkIds.append(";");
									logContent="添加角色菜单权限("+role.getName()+"-"+parList.get(i).getName()+")成功!";
									OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
								}
							}
						}
						result="1";
						msg="添加角色菜单权限("+role.getName()+"-"+func.getName()+")成功!";
					}else {
						msg="没有此菜单！";
					}
				}else {
					msg="没有此角色!";
				}
			} catch (Exception e) {
				DBProException(e);
				msg="添加角色菜单权限("+role.getName()+","+func.getName()+")失败!";
				logContent="添加角色菜单权限("+role.getName()+","+func.getName()+")失败:"+e.toString()+"!";
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		write(result+msg+";"+checkIds.toString(), "UTF-8");
		return null;
	}/**
	 * 取消选择一个菜单
	 * @return
	 */
	public String funcCheckedNo(){
		result="0";
		msg="";
		if (func!=null && func.getId()!=null && func.getId().trim().length()>0 &&
				role!=null && role.getId()!=null && role.getId().trim().length()>0) {
			try {
				role=roleService.findById(role.getId().trim());
				if (role!=null) {
					func=funcService.selectById(func.getId().trim());
					if (func!=null) {
						//对本身进行操作
						if (roleFuncService.haveRoleFunc(role.getId(), func.getId())) {
							//删除权限
							roleFuncService.delRoleFunc(role.getId(), func.getId());
							logContent="删除角色菜单权限("+role.getName()+"-"+func.getName()+")成功!";
							OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
						}
						
						//取消所有子菜单权限
						init("funcService");
						List<Func> sonList=funcService.getSons(func.getId(), null);
						if (sonList!=null && sonList.size()>0) {
							for (int i = 0; i < sonList.size(); i++) {
								if (roleFuncService.haveRoleFunc(role.getId(), sonList.get(i).getId())) {
									//删除菜单权限
									roleFuncService.delRoleFunc(role.getId(), sonList.get(i).getId());
									checkIds.append("func");
									checkIds.append(sonList.get(i).getId());
									checkIds.append(";");
									logContent="删除角色菜单权限("+role.getName()+"-"+sonList.get(i).getName()+")成功!";
									OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
								}
							}
						}
						result="1";
						msg="删除角色菜单权限("+role.getName()+"-"+func.getName()+")成功!";
					}else {
						msg="没有此菜单！";
					}
				}else {
					msg="没有此角色!";
				}
			} catch (Exception e) {
				DBProException(e);
				msg="删除角色菜单权限("+role.getName()+","+func.getName()+")失败!";
				logContent="删除角色菜单权限("+role.getName()+","+func.getName()+")失败:"+e.toString()+"!";
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		write(result+msg+";"+checkIds.toString(), "UTF-8");
		return null;
	}
	public String select(){
		//查询所有有效角色
		RolesExample example=new RolesExample();
		Criteria criteria=example.createCriteria();
		//普通用户根据当前企业查询
		if (!isAdminLogin()) {
			//普通用户登录只查询自己有的角色
			criteria.andSql(" (id in (select roles from freecms_role_user where users='"+getLoginAdmin().getId()+"') or id in (select id from freecms_roles where adduser='"+getLoginName()+"'))");
		}
		criteria.andIsokEqualTo("1");
		initMapper("rolesMapper");
		roleList=rolesMapper.selectByExample(example);
		return "select";
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<Roles> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Roles> roleList) {
		this.roleList = roleList;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Func getFunc() {
		return func;
	}

	public void setFunc(Func func) {
		this.func = func;
	}

	public FuncService getFuncService() {
		return funcService;
	}

	public void setFuncService(FuncService funcService) {
		this.funcService = funcService;
	}

	public RoleFuncService getRoleFuncService() {
		return roleFuncService;
	}

	public void setRoleFuncService(RoleFuncService roleFuncService) {
		this.roleFuncService = roleFuncService;
	}

	public StringBuilder getCheckIds() {
		return checkIds;
	}

	public void setCheckIds(StringBuilder checkIds) {
		this.checkIds = checkIds;
	}
	public RolesMapper getRolesMapper() {
		return rolesMapper;
	}
	public void setRolesMapper(RolesMapper rolesMapper) {
		this.rolesMapper = rolesMapper;
	}
}
