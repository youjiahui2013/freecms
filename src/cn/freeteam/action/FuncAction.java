package cn.freeteam.action;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseAction;
import cn.freeteam.model.Func;
import cn.freeteam.model.RoleFunc;
import cn.freeteam.model.Roles;
import cn.freeteam.model.Users;
import cn.freeteam.service.FuncService;
import cn.freeteam.service.RoleFuncService;
import cn.freeteam.util.HtmlCode;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;





/**
 * 系统菜单相关操作
 * 2011-3-8
 * @author freeteam
 *
 */

public class FuncAction extends BaseAction{

	private String root;
	private Func func;
	private String msg;
	private String noid;
	private String parname;
	private String onclick;
	private Roles role;
	private FuncService funcService;
	private RoleFuncService roleFuncService;
	
	public FuncAction(){
		init("funcService","roleFuncService");
	}
	public String sql(){
		List<Func> list=funcService.selectAll();
		if (list!=null && list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				func=list.get(i);
				System.out.println("insert into freecms_func values('"+func.getId()+"','"+func.getName()+"','"+func.getIsok()+"',"+func.getOrdernum()+",'"+func.getUrl()+"','"+func.getParid()+"');");
			}
		}
		return null;
	}
	/**
	 * 菜单管理ajax加载子菜单
	 * @return
	 */
	public String son(){
		List<Func> list=null; 
		if ("root".equals(root)) {
			//提取一级菜单 
			list=funcService.selectRoot();
		}else {
			//提取子菜单
			list=funcService.selectByParid(root);
		}
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (noid!=null && noid.trim().length()>0 && noid.equals(list.get(i).getId())) {
					continue;
				}
				if (!"[".equals(stringBuilder.toString())) {
					stringBuilder.append(",");
				}
				stringBuilder.append("{ \"text\": \"<a  onclick=");
				if (onclick!=null && onclick.trim().length()>0) {
					stringBuilder.append(onclick);
				}else {
					stringBuilder.append("showDetail");
				}
				stringBuilder.append("('");
				stringBuilder.append(list.get(i).getId());
				stringBuilder.append("','"+list.get(i).getName().replaceAll(" ", "")+"')>");
				stringBuilder.append(list.get(i).getName());
				stringBuilder.append("\", \"hasChildren\": ");
				if (funcService.haveSon(list.get(i).getId())) {
					stringBuilder.append("true");
				}else {
					stringBuilder.append("false");
				}
				stringBuilder.append(",\"id\":\"");
				stringBuilder.append(list.get(i).getId());
				stringBuilder.append("\" }");
			}
		}
		stringBuilder.append("]");
		ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		return null;
	}/**
	 * 菜单显示ajax加载子菜单
	 * @return
	 */
	public String sonUrl(){
		List<Func> list=null; 
		if ("root".equals(root)) {
			//提取一级菜单 
			if (isAdminLogin()) {
				list=funcService.selectRoot();
			}else {
				list=funcService.selectRootAuth(getLoginAdmin().getId());
			}
		}else {
			//提取子菜单
			if (isAdminLogin()) {
				list=funcService.selectByParid(root);
			}else {
				list=funcService.selectByParidAuth(root,getLoginAdmin().getId());
			}
		}
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (noid!=null && noid.trim().length()>0 && noid.equals(list.get(i).getId())) {
					continue;
				}
				if (!"[".equals(stringBuilder.toString())) {
					stringBuilder.append(",");
				}
				stringBuilder.append("{ \"text\": \"<a  ");
				if (list.get(i).getUrl()!=null && list.get(i).getUrl().trim().length()>0) {
					stringBuilder.append(" href='");
					stringBuilder.append(list.get(i).getUrl().trim());
					if (list.get(i).getUrl().trim().indexOf("?")>-1) {
						stringBuilder.append("&");
					}else {
						stringBuilder.append("?");
					}
					stringBuilder.append("pageFuncId=");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("' ");
				}
				stringBuilder.append(" target='right' >");
				stringBuilder.append(list.get(i).getName());
				stringBuilder.append("\", \"hasChildren\": ");
				if (funcService.haveSon(list.get(i).getId())) {
					stringBuilder.append("true");
				}else {
					stringBuilder.append("false");
				}
				stringBuilder.append(",\"id\":\"");
				stringBuilder.append(list.get(i).getId());
				stringBuilder.append("\" }");
			}
		}
		stringBuilder.append("]");
		ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		return null;
	}/**
	 * 菜单显示ajax加载子菜单
	 * @return
	 */
	public String sonAuth(){
		try {
			List<Func> list=null; 
			if ("root".equals(root)) {
				//提取一级菜单 
				if (isAdminLogin()) {
					list=funcService.selectRoot();
				}else {
					list=funcService.selectRootAuth(getLoginAdmin().getId());
				}
			}else {
				//提取子菜单
				if (isAdminLogin()) {
					list=funcService.selectByParid(root);
				}else {
					list=funcService.selectByParidAuth(root,getLoginAdmin().getId());
				}
			}
			//生成树
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("[");
			if (list!=null && list.size()>0) {
				RoleFunc roleFunc=null;
				RoleFunc loginUserRoleFunc=null;
				for (int i = 0; i <list.size() ; i++) {
					if (noid!=null && noid.trim().length()>0 && noid.equals(list.get(i).getId())) {
						continue;
					}
					if (!"[".equals(stringBuilder.toString())) {
						stringBuilder.append(",");
					}
					//判断角色是否有此菜单权限
					if (role!=null && role.getId()!=null && role.getId().trim().length()>0 ) {
						roleFunc=roleFuncService.findRoleFunc(role.getId().trim(), list.get(i).getId());
					}
					stringBuilder.append("{ \"text\": \"<input type='checkbox' id='func");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("' value='");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("' onclick='funcChecked(this)'");
					//判断角色是否有此菜单权限
					if (roleFunc!=null) {
						stringBuilder.append(" checked='checked' ");
					}
					stringBuilder.append("/>");
					stringBuilder.append(list.get(i).getName());
					stringBuilder.append("\", \"hasChildren\": ");
					if (funcService.haveSon(list.get(i).getId())) {
						stringBuilder.append("true");
					}else {
						stringBuilder.append("false");
					}
					stringBuilder.append(",\"id\":\"");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("\" }");
				}
			}
			stringBuilder.append("]");
			ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	//保存
	public String save(){
		Users users=getLoginAdmin();
		String result="0";
		String idname="";
		try {
			func.setUrl(HtmlCode.url(func.getUrl()));
			if (func.getId()!=null&&func.getId().trim().length()>0) {
				msg="修改";
				funcService.update(func);
			}else {
				msg="添加";
				func.setId(UUID.randomUUID().toString());
				funcService.insert(func);
				idname="<属性>"+func.getId()+"<属性>"+func.getName();
			}
			result="1";
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="菜单 "+func.getName()+" 失败:"+e.toString();
		}
		msg+="菜单 "+func.getName()+" 成功"+idname;
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	public String find(){
		String result="0";
		StringBuilder sb=new StringBuilder();
		try {
			if (func.getId()!=null && func.getId().trim().length()>0) {
				Func currfunc=funcService.selectById(func.getId().trim());
				if (currfunc!=null) {
					sb.append(currfunc.getId());
					sb.append("<属性>");
					sb.append(currfunc.getName()!=null?currfunc.getName():"");
					sb.append("<属性>");
					sb.append(currfunc.getIsok());
					sb.append("<属性>");
					sb.append(currfunc.getOrdernum()!=null?currfunc.getOrdernum():"");
					sb.append("<属性>");
					sb.append(currfunc.getUrl()!=null?currfunc.getUrl():"");
					sb.append("<属性>");
					sb.append(currfunc.getParid());
					result="1";
				}
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
		}
		ResponseUtil.writeUTF(getHttpResponse(),result+sb.toString());
		return null;
	}
	//删除
	public String del(){

		Users users=getLoginAdmin();
		String result="0";
		try {
			if (func.getId()!=null&&func.getId().trim().length()>0) {
				funcService.delete(func.getId().trim());
				result="1";
				msg="删除菜单 "+func.getName()+" 成功<属性>"+func.getId();
			}else {
				msg="删除时没有获取到菜单 "+func.getName();
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg="删除菜单 "+func.getName()+" 失败:"+e.toString();
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}

	//改变所属菜单
	public String par(){

		Users users=getLoginAdmin();
		String result="0";
		msg="改变菜单 "+func.getName()+" 的所属菜单为 "+parname+" ";
		try {
			if (func.getId()!=null&&func.getId().trim().length()>0) {
				funcService.updatePar(func);
				result="1";
				msg+="成功<属性>"+func.getId()+"<属性>"+func.getParid();
			}else {
				msg="删除时没有获取到菜单 "+func.getName();
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public Func getFunc() {
		return func;
	}
	public void setFunc(Func func) {
		this.func = func;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getNoid() {
		return noid;
	}
	public void setNoid(String noid) {
		this.noid = noid;
	}
	public String getParname() {
		return parname;
	}
	public void setParname(String parname) {
		this.parname = parname;
	}
	public Roles getRole() {
		return role;
	}
	public void setRole(Roles role) {
		this.role = role;
	}
	public RoleFuncService getRoleFuncService() {
		return roleFuncService;
	}
	public void setRoleFuncService(RoleFuncService roleFuncService) {
		this.roleFuncService = roleFuncService;
	}
	public String getOnclick() {
		return onclick;
	}
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	public FuncService getFuncService() {
		return funcService;
	}
	public void setFuncService(FuncService funcService) {
		this.funcService = funcService;
	}
}
