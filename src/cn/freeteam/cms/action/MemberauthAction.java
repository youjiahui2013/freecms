package cn.freeteam.cms.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Memberauth;
import cn.freeteam.cms.service.MemberauthService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
/**
 * 
 * <p>Title: MemberauthAction.java</p>
 * 
 * <p>Description: 会员权限相关操作</p>
 * 
 * <p>Date: Jan 30, 2013</p>
 * 
 * <p>Time: 8:55:06 PM</p>
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
public class MemberauthAction extends BaseAction{
	private String msg;
	private String pageFuncId;
	private String order=" ordernum ";
	private String logContent;
	private String ids;
	
	private Memberauth memberauth;
	private List<Memberauth> memberauthList;

	private MemberauthService memberauthService;
	
	public MemberauthAction() {
		init("memberauthService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (memberauth==null ){
			memberauth=new Memberauth();
		}
		if (order.trim().length()==0) {
			order=" ordernum ";
		}
		memberauthList=memberauthService.find(memberauth, order, currPage, pageSize);
		totalCount=memberauthService.count(memberauth);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("memberauth.name");
		pager.appendParam("memberauth.code");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("memberauth_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (memberauth!=null && memberauth.getId()!=null && memberauth.getId().trim().length()>0) {
			memberauth=memberauthService.findById(memberauth.getId());
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
			if (memberauth!=null && memberauth.getId()!=null) {
				Memberauth  oldMemberauth=memberauthService.findById(memberauth.getId());
				if (oldMemberauth!=null) {
					oldMemberauth.setName(memberauth.getName());
					oldMemberauth.setCode(memberauth.getCode());
					oldMemberauth.setOrdernum(memberauth.getOrdernum());
					oper="修改";
					memberauthService.update(oldMemberauth);
				}
			}else {
				//添加
				memberauthService.add(memberauth);
			}
			logContent=oper+"会员权限("+memberauth.getName()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"会员权限("+memberauth.getName()+")失败:"+e.toString()+"!";
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
							memberauth=memberauthService.findById(idArr[i]);
							if (memberauth!=null) {
								memberauthService.del(memberauth.getId());
								sb.append(idArr[i]+";");
								logContent="删除会员权限("+memberauth.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除会员权限("+memberauth.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}
	public MemberauthService getMemberauthService() {
		return memberauthService;
	}

	public void setMemberauthService(MemberauthService memberauthService) {
		this.memberauthService = memberauthService;
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

	public Memberauth getMemberauth() {
		return memberauth;
	}

	public void setMemberauth(Memberauth memberauth) {
		this.memberauth = memberauth;
	}

	public List<Memberauth> getMemberauthList() {
		return memberauthList;
	}

	public void setMemberauthList(List<Memberauth> memberauthList) {
		this.memberauthList = memberauthList;
	}
}
