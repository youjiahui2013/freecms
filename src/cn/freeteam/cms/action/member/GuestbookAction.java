package cn.freeteam.cms.action.member;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.service.CreditruleService;
import cn.freeteam.cms.service.GuestbookService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: GuestbookAction.java</p>
 * 
 * <p>Description: 留言相关操作</p>
 * 
 * <p>Date: Feb 6, 2013</p>
 * 
 * <p>Time: 2:08:47 PM</p>
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
public class GuestbookAction extends BaseAction {

	private GuestbookService guestbookService;
	private Guestbook guestbook;
	private List<Guestbook> guestbookList;
	private String order=" addtime desc ";
	private String ids;
	private CreditruleService creditruleService;
	

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (guestbook==null ){
			guestbook=new Guestbook();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		guestbook.setMemberid(getLoginMember().getId());
		guestbookList=guestbookService.find(guestbook, order, currPage, pageSize,false);
		totalCount=guestbookService.count(guestbook,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("guestbook.title");
		pager.appendParam("guestbook.state");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStrNoTable("guestbook_list.do");
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
				init("creditruleService");
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							guestbookService.del(idArr[i]);
							//处理积分
							creditruleService.credit(getLoginMember(), "guestbook_del");
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
	 * 留言详细信息
	 * @return
	 */
	public String info(){
		if (guestbook!=null && StringUtils.isNotEmpty(guestbook.getId())) {
			guestbook=guestbookService.findById(guestbook.getId());
		}
		return "info";
	}

	
	public List<Guestbook> getGuestbookList() {
		return guestbookList;
	}

	public void setGuestbookList(List<Guestbook> guestbookList) {
		this.guestbookList = guestbookList;
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

	public GuestbookAction() {
		init("guestbookService");
	}
	
	public GuestbookService getGuestbookService() {
		return guestbookService;
	}
	public void setGuestbookService(GuestbookService guestbookService) {
		this.guestbookService = guestbookService;
	}
	public Guestbook getGuestbook() {
		return guestbook;
	}
	public void setGuestbook(Guestbook guestbook) {
		this.guestbook = guestbook;
	}
	public CreditruleService getCreditruleService() {
		return creditruleService;
	}
	public void setCreditruleService(CreditruleService creditruleService) {
		this.creditruleService = creditruleService;
	}
	
}
