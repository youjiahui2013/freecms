package cn.freeteam.action;

import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseAction;
import cn.freeteam.dao.UnitMapper;
import cn.freeteam.model.Unit;
import cn.freeteam.model.Users;
import cn.freeteam.service.UnitService;
import cn.freeteam.util.MybatisSessionFactory;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;



public class UnitAction extends BaseAction{
	private UnitMapper unitMapper;
	private String onclick;
	private String root;
	private String noid;
	private Unit unit;
	private String msg;
	private String parname;
	private List<Unit> unitList;
	private UnitService unitService;
	private String selectUnitIds;
	
	public UnitAction(){
		init("unitService");
	}
	
	public String son()
	{
		Users users=getLoginAdmin();
		List<Unit> list=null; 
		unitMapper=MybatisSessionFactory.getSession().getMapper(UnitMapper.class);
		if ("root".equals(root)) {
			//提取一级菜单 
			list=unitMapper.selectUnit("");
		}else {
			//提取子菜单
			list=unitMapper.selectUnit(root);
		}
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (!list.get(i).getId().equals(noid)) {
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
					if (unitService.hasChildren(list.get(i).getId())) {
						stringBuilder.append("true");
					}else {
						stringBuilder.append("false");
					}
					stringBuilder.append(",\"id\":\"");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("\" }");
				}
			}
		}
		stringBuilder.append("]");
		ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		return null;
	}
	//保存
	public String save(){
		Users users=getLoginAdmin();
		unitMapper=MybatisSessionFactory.getSession().getMapper(UnitMapper.class);
		String result="0";
		String idname="";
		try {
			if (unit.getId()!=null&&unit.getId().trim().length()>0) {
				msg="修改";
				unitMapper.updateByPrimaryKey(unit);
				MybatisSessionFactory.getSession().commit();
				unitService.callUnitUpdatePro(unit.getId());
			}else {
				msg="添加";
				unit.setId(UUID.randomUUID().toString());
				unitMapper.insert(unit);
				MybatisSessionFactory.getSession().commit();
				idname="<属性>"+unit.getId()+"<属性>"+unit.getName();
			}
			result="1";
		} catch (Exception e) {
			try {
				MybatisSessionFactory.getSession().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			result="0";
			msg+="单位 "+unit.getName()+" 失败:"+e.toString();
		}
		msg+="单位 "+unit.getName()+" 成功"+idname;
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	//保存
	public String find(){
		String result="0";
		StringBuilder sb=new StringBuilder();
		try {
			if (unit.getId()!=null && unit.getId().trim().length()>0) {
				unitMapper=MybatisSessionFactory.getSession().getMapper(UnitMapper.class);
				Unit currUnit=unitMapper.selectByPrimaryKey(unit.getId().trim());
				if (currUnit!=null) {
					sb.append(currUnit.getId());
					sb.append("<属性>");
					sb.append(currUnit.getName()!=null?currUnit.getName():"");
					sb.append("<属性>");
					sb.append(currUnit.getIsok());
					sb.append("<属性>");
					sb.append(currUnit.getOrdernum()!=null?currUnit.getOrdernum():"");
					sb.append("<属性>");
					sb.append(currUnit.getParid());
					sb.append("<属性>");
					sb.append(currUnit.getIsmail());
					result="1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result="0";	
		}
		ResponseUtil.writeUTF(getHttpResponse(),result+sb.toString());
		return null;
	}
	
	//删除
	public String del(){

		Users users=getLoginAdmin();
		unitMapper=MybatisSessionFactory.getSession().getMapper(UnitMapper.class);
		String result="0";
		try {
			if (unit.getId()!=null&&unit.getId().trim().length()>0) {
				//先看是否有子单位
				List<Unit> sons=unitMapper.selectUnitByparid(unit.getId());
				if (sons!=null && sons.size()>0) {
					//删除子单位
					for (int i = 0; i < sons.size(); i++) {
						unitService.callUnitDelPro(sons.get(i).getId());
						unitMapper.deleteByPrimaryKey(sons.get(i).getId());
					}
				}
				unitService.callUnitDelPro(unit.getId());
				unitMapper.deleteByPrimaryKey(unit.getId());
				MybatisSessionFactory.getSession().commit();
				result="1";
				msg="删除单位 "+unit.getName()+" 成功<属性>"+unit.getId();
			}else {
				msg="删除时没有获取到单位 "+unit.getName();
			}
		} catch (Exception e) {
			try {
				MybatisSessionFactory.getSession().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			result="0";
			msg="删除单位 "+unit.getName()+" 失败:"+e.toString();
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	//改变所属单位
	public String par(){

		Users users=getLoginAdmin();
		String result="0";
		msg="改变菜单 "+unit.getName()+" 的所属菜单为 "+parname+" ";
		try {
			if (unit.getId()!=null&&unit.getId().trim().length()>0) {
				unitMapper=MybatisSessionFactory.getSession().getMapper(UnitMapper.class);
				unitMapper.updatePar(unit);
				MybatisSessionFactory.getSession().commit();
				result="1";
				msg+="成功<属性>"+unit.getId()+"<属性>"+unit.getParid();
			}else {
				msg="删除时没有获取到菜单 "+unit.getName();
			}
		} catch (Exception e) {
			try {
				MybatisSessionFactory.getSession().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(users.getLoginname(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	/**
	 * 选择单位 
	 * @return
	 */
	public String select(){
		//查询所有有效单位
		if (isAdminLogin()) {
			unitList=unitService.findByUser("1","");
		}else {
			unitList=unitService.findByUser("1",getLoginAdmin().getId());
		}
		return "select";
	}
	public String getOnclick() {
		return onclick;
	}
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getNoid() {
		return noid;
	}
	public void setNoid(String noid) {
		this.noid = noid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getParname() {
		return parname;
	}
	public void setParname(String parname) {
		this.parname = parname;
	}
	public UnitMapper getUnitMapper() {
		return unitMapper;
	}
	public void setUnitMapper(UnitMapper unitMapper) {
		this.unitMapper = unitMapper;
	}
	public List<Unit> getUnitList() {
		return unitList;
	}
	public void setUnitList(List<Unit> unitList) {
		this.unitList = unitList;
	}
	public UnitService getUnitService() {
		return unitService;
	}
	public void setUnitService(UnitService unitService) {
		this.unitService = unitService;
	}

	public String getSelectUnitIds() {
		return selectUnitIds;
	}

	public void setSelectUnitIds(String selectUnitIds) {
		this.selectUnitIds = selectUnitIds;
	}
}

