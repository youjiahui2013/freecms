package cn.freeteam.cms.action.member;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.service.MemberService;
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
		return "send";
	}
	/**
	 * 发送站内信
	 * @return
	 */
	public String sendDo() {
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
							currMsg.setIssys("0");
							currMsg.setIsread("0");
							currMsg.setMemberid(getLoginMember().getId());
							currMsg.setMembername(getLoginMemberName());
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
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	

	/**
	 * 收件箱
	 * @return
	 */
	public String tolist(){
		if (msg==null ){
			msg=new Msg();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		msg.setTomemberid(getLoginMember().getId());
		msgList=msgService.find(msg, order, currPage, pageSize);
		totalCount=msgService.count(msg);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("msg.title");
		pager.appendParam("msg.membername");
		pager.appendParam("msg.content");
		pager.appendParam("msg.issys");
		pager.appendParam("msg.isread");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStrNoTable("msg_tolist.do");
		pageStr=pager.getOutStrNoTable();
		return "tolist";
	}
	/**
	 * 发件箱
	 * @return
	 */
	public String sendlist(){
		if (msg==null ){
			msg=new Msg();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		msg.setMemberid(getLoginMember().getId());
		msgList=msgService.find(msg, order, currPage, pageSize);
		totalCount=msgService.count(msg);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("msg.title");
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
		pager.setOutStrNoTable("msg_sendlist.do");
		pageStr=pager.getOutStrNoTable();
		return "sendlist";
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
			if (msg!=null && !"1".equals(msg.getIsread())) {
				//设置为已读
				msg.setIsread("1");
				msgService.update(msg);
			}
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
	
}
