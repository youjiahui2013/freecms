package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.service.MailService;
import cn.freeteam.model.Unit;
import cn.freeteam.model.Users;
import cn.freeteam.service.ConfigService;
import cn.freeteam.service.UnitService;
import cn.freeteam.service.UserService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: MailAction.java</p>
 * 
 * <p>Description: 互动信件</p>
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
public class MailAction extends BaseAction{

	private MailService mailService;
	private UnitService unitService;
	private UserService userService;
	
	private Mail mail;
	private List<Mail> mailList;
	private String order;
	private String[] mailtypes;
	private List<Unit> unitList;
	private List<Users> userList;
	private Unit unit;
	private Users user;
	private String msg;
	private String pageFuncId;
	private String forwardtype;
	private String logContent;
	private String ids;
	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public MailAction() {
		init("mailService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		init("unitService","userService");
		unitList=unitService.findByPar("", "1", "1",false);
		user=new Users();
		user.setIsmail("1");
		userList=userService.find(user,false);
		mailtypes=getConfig().get("mailType").toString().split(",");
		if (mail==null ){
			mail=new Mail();
		}
		if (!isAdminLogin()) {
			//不是admin,只能查看自己所属单位或个人的
			if ("unit".equals(mail.getType())) {
				mail.setUnitids(getLoginUnitIdsSql());
				if (mail.getUnitids().trim().length()==0) {
					mail.setUnitids("'no'");
				}
			}else if ("user".equals(mail.getType())) {
				mail.setUserid(getLoginAdmin().getId());
			}
		}
		mailList=mailService.find(mail, order, currPage, pageSize,false);
		totalCount=mailService.count(mail,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("mail.type");
		pager.appendParam("mail.querycode");
		pager.appendParam("mail.mailtype");
		pager.appendParam("mail.title");
		pager.appendParam("mail.writer");
		pager.appendParam("mail.state");
		pager.appendParam("mail.unitid");
		pager.appendParam("mail.userid");
		pager.appendParam("mail.isopen");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("mail_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 转交
	 * @return
	 */
	public String forward(){
		init("unitService","userService");
		unitList=unitService.findByPar("", "1", "1",false);
		user=new Users();
		user.setIsmail("1");
		userList=userService.find(user,false);
		return "forward";
	}
	/**
	 * 转交处理
	 * @return
	 */
	public String forwardDo(){
		if (mail!=null && mail.getId()!=null && mail.getId().trim().length()>0) {
			Mail updateMail=new Mail();
			updateMail.setId(mail.getId());
			//设置流转流程
			Mail oldmail=mailService.findById(mail.getId());
			String proflow="";
			if (oldmail.getProflow()==null || oldmail.getProflow().trim().length()==0) {
				if (oldmail.getUnitid()!=null && oldmail.getUnitid().trim().length()>0) {
					proflow=oldmail.getUnitname();
				}
				else if (oldmail.getUserid()!=null && oldmail.getUserid().trim().length()>0) {
					proflow=oldmail.getUsername();
				}else {
					proflow="其他";
				}
			}else {
				proflow=oldmail.getProflow();
			}
			proflow+=" --> ";
			String toname="";
			if ("unit".equals(forwardtype)) {
				updateMail.setUnitid(mail.getUnitid());
				updateMail.setUserid("");
				proflow+=mail.getUnitname();
				toname=mail.getUnitname();
			}else {
				updateMail.setUnitid("");
				updateMail.setUserid(mail.getUserid());
				proflow+=mail.getUsername();
				toname=mail.getUsername();
			}
			updateMail.setProflow(proflow);
			try {
				mailService.update(updateMail);
				msg="<script>alert('转交成功');location.href='mail_list.do?mail.type="+mail.getType()+"&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldmail.getTitle()+" 信件转交给 "+toname;
			} catch (Exception e) {
				msg="<script>alert('转交失败:"+e.getMessage()+"');location.href='mail_list.do?mail.type="+mail.getType()+"&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldmail.getTitle()+" 信件转交给 "+toname+" 时失败:"+e.getMessage();
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
		String type=mail.getType();
		if (mail!=null && mail.getId()!=null && mail.getId().trim().length()>0) {
			mail=mailService.findById(mail.getId());
			mail.setType(type);
		}
		return "pro";
	}
	/**
	 * 办结处理
	 * @return
	 */
	public String proDo(){
		if (mail!=null && mail.getId()!=null && mail.getId().trim().length()>0) {
			Mail updateMail=new Mail();
			updateMail.setId(mail.getId());
			updateMail.setRecontent(mail.getRecontent());
			updateMail.setRetime(new Date());
			updateMail.setState("1");
			Mail oldmail=mailService.findById(mail.getId());
			try {
				mailService.update(updateMail);
				msg="<script>alert('办结成功');location.href='mail_list.do?mail.type="+mail.getType()+"&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldmail.getTitle()+" 信件办结 ";
			} catch (Exception e) {
				msg="<script>alert('办结失败:"+e.getMessage()+"');location.href='mail_list.do?mail.type="+mail.getType()+"&pageFuncId="+pageFuncId+"';</script>";
				logContent=oldmail.getTitle()+" 信件办结时失败:"+e.getMessage();
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
							mail=mailService.findById(idArr[i]);
							if (mail!=null) {
								mailService.del(mail.getId());
								sb.append(idArr[i]+";");
								logContent="删除信件("+mail.getTitle()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除信件("+mail.getTitle()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}
	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}


	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<Mail> getMailList() {
		return mailList;
	}

	public void setMailList(List<Mail> mailList) {
		this.mailList = mailList;
	}

	public String[] getMailtypes() {
		return mailtypes;
	}

	public void setMailtypes(String[] mailtypes) {
		this.mailtypes = mailtypes;
	}

	public List<Unit> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<Unit> unitList) {
		this.unitList = unitList;
	}

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public UnitService getUnitService() {
		return unitService;
	}

	public void setUnitService(UnitService unitService) {
		this.unitService = unitService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
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

	public String getForwardtype() {
		return forwardtype;
	}

	public void setForwardtype(String forwardtype) {
		this.forwardtype = forwardtype;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
}
