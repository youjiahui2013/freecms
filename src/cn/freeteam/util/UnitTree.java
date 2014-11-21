package cn.freeteam.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.freeteam.model.Unit;
import cn.freeteam.model.Users;



/**
 * 
 * <p>Title: UnitTree.java</p>
 * 
 * <p>Description: 生成静态单位树</p>
 * 
 * <p>Date: Nov 28, 2011</p>
 * 
 * <p>Time: 10:09:03 PM</p>
 * 
 * <p>Copyright: 2011</p>
 * 
 * <p>Company: bfsfot</p>
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
public class UnitTree {

	private StringBuffer content=new StringBuffer();
	/**
	 * 生成树
	 * @param content
	 * @param list
	 * @param parid
	 */
	public void createTree(StringBuffer content,List<Unit> list,String parid,String selectUnitIds){
		//循环数据
		Unit unit;
		for (int i = 0; i < list.size(); i++) {
			unit=list.get(i);
			//生成parid为参数值的数据
			if (unit.getParid().equals(parid)) {
				content.append("<li><span class=\"hasChildren\">");
				content.append("<input name=\"unitids\" id=\"unitids"+unit.getId()+"\" type=\"checkbox\" value=\""+unit.getId()+"\" unit=\""+unit.getName()+"\"");
				if (selectUnitIds.indexOf(unit.getId())>-1) {
					content.append(" checked ");
				}
				content.append("/>"+unit.getName());
				content.append("</span><ul>");
				//如果有子部门则递归调用此函数
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).getParid().equals(unit.getId())) {
						createTree(content,list,unit.getId(),selectUnitIds);
						break;
					}
				}
				content.append("</ul></li>");
			}
		}
	}
	/**
	 * 生成树
	 * @param content
	 * @param list
	 * @param parid
	 */
	public void listTree(StringBuffer content,List<Unit> list,String selectUnitIds){
		//循环数据
		Unit unit;
		for (int i = 0; i < list.size(); i++) {
			unit=list.get(i);
			content.append("<li><span >");
			content.append("<input name=\"unitids\" type=\"checkbox\" value=\""+unit.getId()+"\" unit=\""+unit.getName()+"\"");
			if (selectUnitIds.indexOf(unit.getId())>-1) {
				content.append(" checked ");
			}
			content.append("/>"+unit.getName());
			content.append("</span></li>");
		}
	}
	public String create(List<Unit> list,String selectUnitIds,HttpServletRequest request){
		if (list!=null && list.size()>0) {
			if (request.getSession().getAttribute("loginAdmin")!=null) {
				Users user= (Users)request.getSession().getAttribute("loginAdmin");
				if (user.getLoginname().equals("admin")) {
					createTree(content,list,"",selectUnitIds);
				}else {
					listTree(content,list,selectUnitIds);
				}
			}
		}
		return content.toString();
	}
}
