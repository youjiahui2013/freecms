package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Report;
import cn.freeteam.cms.service.ReportService;
import cn.freeteam.model.Users;
import cn.freeteam.service.ConfigService;
import cn.freeteam.service.UserService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: ReportAction.java</p>
 * 
 * <p>Description: 在线申报</p>
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
public class ReportAction extends BaseAction{

	private ReportService reportService;
	private UserService userService;
	
	private Report report;
	private List<Report> reportList;
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

	public ReportAction() {
		init("reportService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (report==null ){
			report=new Report();
		}
		if (!isAdminLogin()) {
			//不是admin,只能查看自己的
			report.setUserid(getLoginAdmin().getId());
		}
		reportList=reportService.find(report, order, currPage, pageSize,false);
		totalCount=reportService.count(report,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("report.querycode");
		pager.appendParam("report.name");
		pager.appendParam("report.issuer");
		pager.appendParam("report.linkman");
		pager.appendParam("report.state");
		pager.appendParam("report.userid");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("report_list.do");
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
		if (report!=null && report.getId()!=null && report.getId().trim().length()>0) {
			init("userService");
			Report updateReport=new Report();
			updateReport.setId(report.getId());
			//设置流转流程
			Report oldreport=reportService.findById(report.getId());
			String proflow="";
			if (oldreport.getProflow()==null || oldreport.getProflow().trim().length()==0) {
				if (oldreport.getUserid()!=null && oldreport.getUserid().trim().length()>0) {
					user=userService.findById(oldreport.getUserid());
					if (user!=null) {
						proflow=user.getName();
					}
				}else {
					proflow="其他";
				}
			}else {
				proflow=oldreport.getProflow();
			}
			proflow+=" --> ";
			String toname="";
			updateReport.setUserid(report.getUserid());
			if (report.getUserid()!=null && report.getUserid().trim().length()>0) {
				user=userService.findById(report.getUserid());
				if (user!=null) {
					proflow+=user.getName();
				}
			}else {
				proflow+="其他";
			}
			updateReport.setProflow(proflow);
			try {
				reportService.update(updateReport);
				msg="<script>alert('转交成功');location.href='report_list.do?pageFuncId="+pageFuncId+"';</script>";
				logContent=oldreport.getName()+" 申报项目转交给 "+toname;
			} catch (Exception e) {
				msg="<script>alert('转交失败:"+e.getMessage()+"');location.href='report_list.do?&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldreport.getName()+" 申报项目转交给 "+toname+" 时失败:"+e.getMessage();
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
		if (report!=null && report.getId()!=null && report.getId().trim().length()>0) {
			report=reportService.findById(report.getId());
		}
		return "pro";
	}
	/**
	 * 办结处理
	 * @return
	 */
	public String proDo(){
		if (report!=null && report.getId()!=null && report.getId().trim().length()>0) {
			Report updatereport=new Report();
			updatereport.setId(report.getId());
			updatereport.setRecontent(report.getRecontent());
			updatereport.setRetime(new Date());
			updatereport.setState("1");
			Report oldreport=reportService.findById(report.getId());
			try {
				reportService.update(updatereport);
				msg="<script>alert('办结成功');location.href='report_list.do?pageFuncId="+pageFuncId+"';</script>";
				logContent=oldreport.getName()+" 申报项目办结 ";
			} catch (Exception e) {
				msg="<script>alert('办结失败:"+e.getMessage()+"');location.href='report_list.do?&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldreport.getName()+" 申报项目办结时失败:"+e.getMessage();
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
							report=reportService.findById(idArr[i]);
							if (report!=null) {
								reportService.del(report.getId());
								sb.append(idArr[i]+";");
								logContent="删除申报项目("+report.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除申报项目("+report.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<Report> getReportList() {
		return reportList;
	}

	public void setReportList(List<Report> reportList) {
		this.reportList = reportList;
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
