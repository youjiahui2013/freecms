package cn.freeteam.cms.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.Creditlog;
import cn.freeteam.cms.model.Creditrule;
import cn.freeteam.cms.service.CreditlogService;
import cn.freeteam.cms.service.CreditruleService;
import cn.freeteam.model.OperlogsExample;
import cn.freeteam.model.OperlogsExample.Criteria;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: CreditlogAction.java</p>
 * 
 * <p>Description:积分记录相关操作 </p>
 * 
 * <p>Date: Feb 4, 2013</p>
 * 
 * <p>Time: 7:52:23 PM</p>
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
public class CreditlogAction extends BaseAction{
	
	private Creditlog creditlog;
	private CreditlogService creditlogService;
	private CreditruleService creditruleService;
	private List<Creditlog> creditlogList;
	private List<Creditrule> creditruleList;
	private String order="credittime desc";
	
	public CreditlogAction() {
		init("creditlogService","creditruleService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (creditlog==null ){
			creditlog=new Creditlog();
		}
		if (order.trim().length()==0) {
			order=" credittime desc ";
		}
		creditruleList=creditruleService.find(null, "ordernum", true);
		creditlogList=creditlogService.find(creditlog, order, currPage, pageSize);
		totalCount=creditlogService.count(creditlog);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("creditlog.membername");
		pager.appendParam("creditlog.creditruleid");
		pager.appendParam("creditlog.type");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.appendParam("order");
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("creditlog_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	
	
	public Creditlog getCreditlog() {
		return creditlog;
	}
	public void setCreditlog(Creditlog creditlog) {
		this.creditlog = creditlog;
	}
	public CreditlogService getCreditlogService() {
		return creditlogService;
	}
	public void setCreditlogService(CreditlogService creditlogService) {
		this.creditlogService = creditlogService;
	}
	public List<Creditlog> getCreditlogList() {
		return creditlogList;
	}
	public void setCreditlogList(List<Creditlog> creditlogList) {
		this.creditlogList = creditlogList;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}

	public List<Creditrule> getCreditruleList() {
		return creditruleList;
	}

	public void setCreditruleList(List<Creditrule> creditruleList) {
		this.creditruleList = creditruleList;
	}

	public CreditruleService getCreditruleService() {
		return creditruleService;
	}

	public void setCreditruleService(CreditruleService creditruleService) {
		this.creditruleService = creditruleService;
	}

}
