package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Applyopen;
import cn.freeteam.cms.service.ApplyopenService;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: ApplyopenAction.java</p>
 * 
 * <p>Description: 依申请公开</p>
 * 
 * <p>Date: Jan 18, 2013</p>
 * 
 * <p>Time: 8:58:23 PM</p>
 * 
 * <p>Copyright: 2013</p>
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
public class ApplyopenAction extends BaseAction{

	private ApplyopenService applyopenService;
	private UserService userService;
	
	private Applyopen applyopen;
	private List<Applyopen> applyopenList;
	private List<Users> userList;
	private String order=" addtime ";
	private Users user;
	private String msg;
	private String pageFuncId;
	private String logContent;
	private String ids;
	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public ApplyopenAction() {
		init("applyopenService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (applyopen==null ){
			applyopen=new Applyopen();
		}
		if (!isAdminLogin()) {
			//不是admin,只能查看自己的
			applyopen.setUserid(getLoginAdmin().getId());
		}
		applyopenList=applyopenService.find(applyopen, order, currPage, pageSize,false);
		totalCount=applyopenService.count(applyopen,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("applyopen.querycode");
		pager.appendParam("applyopen.name");
		pager.appendParam("applyopen.state");
		pager.appendParam("applyopen.userid");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("applyopen_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 转交
	 * @return
	 */
	public String forward(){
		init("userService");
		user=new Users();
		userList=userService.find(user,false);
		return "forward";
	}
	/**
	 * 转交处理
	 * @return
	 */
	public String forwardDo(){
		if (applyopen!=null && applyopen.getId()!=null && applyopen.getId().trim().length()>0) {
			init("userService");
			Applyopen updateApplyopen=new Applyopen();
			updateApplyopen.setId(applyopen.getId());
			//设置流转流程
			Applyopen oldApplyopen=applyopenService.findById(applyopen.getId());
			String proflow="";
			if (oldApplyopen.getProflow()==null || oldApplyopen.getProflow().trim().length()==0) {
				if (oldApplyopen.getUserid()!=null && oldApplyopen.getUserid().trim().length()>0) {
					user=userService.findById(oldApplyopen.getUserid());
					if (user!=null) {
						proflow=user.getName();
					}
				}else {
					proflow="其他";
				}
			}else {
				proflow=oldApplyopen.getProflow();
			}
			proflow+=" --> ";
			String toname="";
			updateApplyopen.setUserid(applyopen.getUserid());
			if (applyopen.getUserid()!=null && applyopen.getUserid().trim().length()>0) {
				user=userService.findById(applyopen.getUserid());
				if (user!=null) {
					proflow+=user.getName();
				}
			}else {
				proflow+="其他";
			}
			updateApplyopen.setProflow(proflow);
			try {
				applyopenService.update(updateApplyopen);
				msg="<script>alert('转交成功');location.href='applyopen_list.do?pageFuncId="+pageFuncId+"';</script>";
				logContent=oldApplyopen.getName()+" 依申请公开转交给 "+toname;
			} catch (Exception e) {
				msg="<script>alert('转交失败:"+e.getMessage()+"');location.href='applyopen_list.do?&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldApplyopen.getName()+" 依申请公开转交给 "+toname+" 时失败:"+e.getMessage();
			}finally{
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		return "msg";
	}
	/**
	 * 处理页面
	 * @return
	 */
	public String pro(){
		if (applyopen!=null && applyopen.getId()!=null && applyopen.getId().trim().length()>0) {
			applyopen=applyopenService.findById(applyopen.getId());
		}
		return "pro";
	}
	/**
	 * 办结处理
	 * @return
	 */
	public String proDo(){
		if (applyopen!=null && applyopen.getId()!=null && applyopen.getId().trim().length()>0) {
			Applyopen updateApplyopen=new Applyopen();
			updateApplyopen.setId(applyopen.getId());
			updateApplyopen.setRecontent(applyopen.getRecontent());
			updateApplyopen.setRetime(new Date());
			updateApplyopen.setState("1");
			Applyopen oldApplyopen=applyopenService.findById(applyopen.getId());
			try {
				applyopenService.update(updateApplyopen);
				msg="<script>alert('办结成功');location.href='applyopen_list.do?pageFuncId="+pageFuncId+"';</script>";
				logContent=oldApplyopen.getName()+" 依申请公开办结 ";
			} catch (Exception e) {
				msg="<script>alert('办结失败:"+e.getMessage()+"');location.href='applyopen_list.do?&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldApplyopen.getName()+" 依申请公开办结时失败:"+e.getMessage();
			}finally{
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		return "msg";
	}

	/**
	 * 删除
	 * @return
	 */
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							applyopen=applyopenService.findById(idArr[i]);
							if (applyopen!=null) {
								applyopenService.del(applyopen.getId());
								sb.append(idArr[i]+";");
								logContent="删除依申请公开("+applyopen.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除依申请公开("+applyopen.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}

	public ApplyopenService getApplyopenService() {
		return applyopenService;
	}

	public void setApplyopenService(ApplyopenService ApplyopenService) {
		this.applyopenService = ApplyopenService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Applyopen getApplyopen() {
		return applyopen;
	}

	public void setApplyopen(Applyopen Applyopen) {
		this.applyopen = Applyopen;
	}

	public List<Applyopen> getApplyopenList() {
		return applyopenList;
	}

	public void setApplyopenList(List<Applyopen> ApplyopenList) {
		this.applyopenList = ApplyopenList;
	}

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPageFuncId() {
		return pageFuncId;
	}

	public void setPageFuncId(String pageFuncId) {
		this.pageFuncId = pageFuncId;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
}
