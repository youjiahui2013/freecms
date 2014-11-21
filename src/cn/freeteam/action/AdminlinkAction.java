package cn.freeteam.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Sensitive;
import cn.freeteam.model.Adminlink;
import cn.freeteam.service.AdminlinkService;
import cn.freeteam.util.HtmlCode;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
/**
 * 
 * <p>Title: AdminlinkAction.java</p>
 * 
 * <p>Description: 系统链接相关操作</p>
 * 
 * <p>Date: Jan 14, 2013</p>
 * 
 * <p>Time: 8:26:09 PM</p>
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
public class AdminlinkAction extends BaseAction{
	private String msg;
	private String pageFuncId;
	private String order="ordernum";
	private String logContent;
	private String ids;
	private AdminlinkService adminlinkService;
	private List<Adminlink> adminlinkList;
	private Adminlink adminlink;
	
	public AdminlinkAction() {
		init("adminlinkService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (adminlink==null ){
			adminlink=new Adminlink();
		}
		if (Adminlink.TYPE_USER.equals(adminlink.getType())) {
			//个人链接，添加用户条件
			adminlink.setUserid(getLoginAdmin().getId());
		}
		adminlinkList=adminlinkService.find(adminlink, order,false);
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (adminlink!=null && adminlink.getId()!=null && adminlink.getId().trim().length()>0) {
			adminlink=adminlinkService.findById(adminlink.getId());
		}
		return "edit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		String oper="添加";
		try {
			adminlink.setUrl(HtmlCode.url(adminlink.getUrl()));
			if (Adminlink.TYPE_USER.equals(adminlink.getType())) {
				//个人链接，添加用户条件
				adminlink.setUserid(getLoginAdmin().getId());
			}
			if (adminlink!=null && adminlink.getId()!=null) {
				oper="修改";
				adminlinkService.update(adminlink);
			}else {
				//添加
				adminlinkService.add(adminlink);
			}
			logContent=oper+(Adminlink.TYPE_SYS.equals(adminlink.getType())?"系统":"个人")+"链接("+adminlink.getName()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+(Adminlink.TYPE_SYS.equals(adminlink.getType())?"系统":"个人")+"链接("+adminlink.getName()+")失败:"+e.toString()+"!";
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
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							adminlink=adminlinkService.findById(idArr[i]);
							if (adminlink!=null) {
								adminlinkService.del(adminlink.getId());
								sb.append(idArr[i]+";");
								logContent="删除"+(Adminlink.TYPE_SYS.equals(adminlink.getType())?"系统":"个人")+"链接("+adminlink.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除"+(Adminlink.TYPE_SYS.equals(adminlink.getType())?"系统":"个人")+"链接("+adminlink.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
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
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
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
	public AdminlinkService getAdminlinkService() {
		return adminlinkService;
	}
	public void setAdminlinkService(AdminlinkService adminlinkService) {
		this.adminlinkService = adminlinkService;
	}
	public List<Adminlink> getAdminlinkList() {
		return adminlinkList;
	}
	public void setAdminlinkList(List<Adminlink> adminlinkList) {
		this.adminlinkList = adminlinkList;
	}
	public Adminlink getAdminlink() {
		return adminlink;
	}
	public void setAdminlink(Adminlink adminlink) {
		this.adminlink = adminlink;
	}
}
