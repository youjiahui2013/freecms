package cn.freeteam.action;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;


import cn.freeteam.base.BaseAction;
import cn.freeteam.dao.FuncMapper;
import cn.freeteam.dao.OperbuttonMapper;
import cn.freeteam.dao.UsersMapper;
import cn.freeteam.model.Func;
import cn.freeteam.model.Operbutton;
import cn.freeteam.model.Users;
import cn.freeteam.service.OperbuttonService;
import cn.freeteam.util.MD5;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;




/**
 * 操作按钮相关操作
 * 2011-3-12
 * @author freeteam
 *
 */

public class OperButtonAction extends BaseAction{

	private String msg;
	
	private Operbutton operbutton;
	
	private OperbuttonService operbuttonService;
	
	private String result;
	
	public OperButtonAction(){
		init("operbuttonService");
	}
	public String sql(){
		List<Operbutton> list=operbuttonService.findAll();
		if (list!=null && list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				operbutton=list.get(i);
				System.out.println("insert into freecms_operbutton " +
						"values('"+operbutton.getId()+"','"+operbutton.getFunc()+"','"+operbutton.getName()+"','"+operbutton.getCode()+"','"+operbutton.getIsok()+"',"+operbutton.getOrdernum()+");");
			}
		}
		return null;
	}
	public String add(){
		String id="";
		try {
			result="0";
			msg="添加操作按钮 "+operbutton.getName()+" ";
			//判断名称是否存在
			if (operbuttonService.haveName(operbutton.getFunc(), operbutton.getName())) {
				msg="此操作按钮名称已存在!";
			}else {
				operbutton.setId(UUID.randomUUID().toString());
				operbuttonService.insert(operbutton);
				result="1";
				msg+="成功";
				id="<属性>"+operbutton.getId();
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg+id);
		return null;
	}
	public String update(){
		try {
			result="0";
			msg="修改操作按钮 "+operbutton.getName()+" ";
			//获取老数据
			Operbutton old=operbuttonService.findById(operbutton.getId());
			if (old!=null) {
				//判断名称是否有变化
				if (!old.getName().equals(operbutton.getName())) {
					//有变化则判断是否已存在
					if (operbuttonService.haveName(old.getFunc(), operbutton.getName())) {
						msg="此操作按钮名称已存在!";
						ResponseUtil.writeUTF(getHttpResponse(),result+msg);
						return null;
					}
				}
				operbuttonService.update(operbutton);
				result="1";
				msg+="成功";
			}else {
				msg="此操作按钮不存在!";
				ResponseUtil.writeUTF(getHttpResponse(),result+msg);
				return null;
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	public String del(){
		String id="";
		try {
			result="0";
			msg="删除操作按钮 "+operbutton.getName()+" ";
			operbuttonService.delete(operbutton.getId());
			result="1";
			msg+="成功";
			id="<属性>"+operbutton.getId();
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg+id);
		return null;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Operbutton getOperbutton() {
		return operbutton;
	}
	public void setOperbutton(Operbutton operbutton) {
		this.operbutton = operbutton;
	}
	public OperbuttonService getOperbuttonService() {
		return operbuttonService;
	}
	public void setOperbuttonService(OperbuttonService operbuttonService) {
		this.operbuttonService = operbuttonService;
	}
}
