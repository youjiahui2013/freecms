package cn.freeteam.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.model.Membergroup;
import cn.freeteam.cms.service.MemberService;
import cn.freeteam.cms.service.MembergroupService;
import cn.freeteam.model.Msg;
import cn.freeteam.service.MsgService;
import cn.freeteam.util.Pager;

public class MsgAction extends BaseAction{

	private MsgService msgService;
	private Msg msg;
	private String tomembernames;
	private List<Msg> msgList;
	private Member member;
	private MemberService memberService;
	private String order=" addtime desc ";
	private String ids;
	private List<Membergroup> membergroupList;
	private MembergroupService membergroupService;
	private String[] membergroups;

	public List<Membergroup> getMembergroupList() {
		return membergroupList;
	}
	public void setMembergroupList(List<Membergroup> membergroupList) {
		this.membergroupList = membergroupList;
	}
	public MembergroupService getMembergroupService() {
		return membergroupService;
	}
	public void setMembergroupService(MembergroupService membergroupService) {
		this.membergroupService = membergroupService;
	}
	public Msg getMsg() {
		return msg;
	}
	public void setMsg(Msg msg) {
		this.msg = msg;
	}
	public String getTomembernames() {
		return tomembernames;
	}
	public void setTomembernames(String tomembernames) {
		this.tomembernames = tomembernames;
	}
	public MsgAction() {
		init("msgService","memberService");
	}
	/**
	 * 发送站内信页面
	 * @return
	 */
	public String send(){
		//提取会员组
		init("membergroupService");
		membergroupList=membergroupService.find(null, " ordernum ");
		return "send";
	}
	/**
	 * 发送站内信
	 * @return
	 */
	public String sendDo() {
		//收信人
		if (StringUtils.isNotEmpty(tomembernames)) {
			try {
				String[] tomembers=tomembernames.split(";");
				if (tomembers!=null && tomembers.length>0) {
					for (int i = 0; i < tomembers.length; i++) {
						member=memberService.findByLoginname(tomembers[i]);
						if (member!=null) {
							Msg currMsg=new Msg();
							currMsg.setTitle(msg.getTitle());
							currMsg.setContent(msg.getContent());
							currMsg.setAddtime(new Date());
							currMsg.setIssys("1");
							currMsg.setIsread("0");
							currMsg.setUserid(getLoginAdmin().getId());
							currMsg.setUsername(getLoginName());
							currMsg.setTomemberid(member.getId());
							currMsg.setTomembername(member.getLoginname());
							msgService.add(currMsg);
						}
					}
				}
				showMessage="发送成功!";
			} catch (Exception e) {
				showMessage="发送失败:"+e.getMessage();
			}
		}
		//收信会员组
		if (membergroups!=null && membergroups.length>0) {
			try {
				List<String> groupids=new ArrayList<String>();
				for (int i = 0; i < membergroups.length; i++) {
					groupids.add(membergroups[i]);
				}
				member=new Member();
				member.setGroupids(groupids);
				List<Member> memberList=memberService.find(member, "");
				if (memberList!=null && memberList.size()>0) {
					for (int i = 0; i < memberList.size(); i++) {
						Msg currMsg=new Msg();
						currMsg.setTitle(msg.getTitle());
						currMsg.setContent(msg.getContent());
						currMsg.setAddtime(new Date());
						currMsg.setIssys("1");
						currMsg.setIsread("0");
						currMsg.setUserid(getLoginAdmin().getId());
						currMsg.setUsername(getLoginName());
						currMsg.setTomemberid(memberList.get(i).getId());
						currMsg.setTomembername(memberList.get(i).getLoginname());
						msgService.add(currMsg);
					}
				}
				showMessage="发送成功!";
			} catch (Exception e) {
				showMessage="发送失败:"+e.getMessage();
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (msg==null ){
			msg=new Msg();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		msgList=msgService.find(msg, order, currPage, pageSize);
		totalCount=msgService.count(msg);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("msg.title");
		pager.appendParam("msg.membername");
		pager.appendParam("msg.tomembername");
		pager.appendParam("msg.content");
		pager.appendParam("msg.issys");
		pager.appendParam("msg.isread");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStrNoTable("msg_list.do");
		pageStr=pager.getOutStrNoTable();
		return "list";
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
							msgService.del(idArr[i]);
							sb.append(idArr[i]+";");
						} catch (Exception e) {
							DBProException(e);
						}
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}
	/**
	 * 站内信详细
	 * @return
	 */
	public String info(){
		if (msg!=null && StringUtils.isNotEmpty(msg.getId())) {
			msg=msgService.findById(msg.getId());
		}
		return "info";
	}
	public MsgService getMsgService() {
		return msgService;
	}

	public void setMsgService(MsgService msgService) {
		this.msgService = msgService;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public List<Msg> getMsgList() {
		return msgList;
	}
	public void setMsgList(List<Msg> msgList) {
		this.msgList = msgList;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String[] getMembergroups() {
		return membergroups;
	}
	public void setMembergroups(String[] membergroups) {
		this.membergroups = membergroups;
	}
	
}
