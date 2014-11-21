package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.service.GuestbookService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: GuestbookAction.java</p>
 * 
 * <p>Description:留言本相关操作 </p>
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
public class GuestbookAction extends BaseAction{

	private GuestbookService guestbookService;

	
	private Guestbook guestbook;
	private List<Guestbook> guestbookList;
	private String order=" addtime ";
	private String msg;
	private String pageFuncId;
	private String logContent;
	private String ids;
	
	public GuestbookAction() {
		init("guestbookService");
	}
	

	
	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (guestbook==null ){
			guestbook=new Guestbook();
		}
		guestbook.setSiteid(getManageSite().getId());
		guestbookList=guestbookService.find(guestbook, order, currPage, pageSize,false);
		totalCount=guestbookService.count(guestbook,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("guestbook.title");
		pager.appendParam("guestbook.name");
		pager.appendParam("guestbook.membername");
		pager.appendParam("guestbook.reusername");
		pager.appendParam("guestbook.state");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("guestbook_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	

	/**
	 * 处理页面
	 * @return
	 */
	public String pro(){
		if (guestbook!=null && guestbook.getId()!=null && guestbook.getId().trim().length()>0) {
			guestbook=guestbookService.findById(guestbook.getId());
		}
		return "pro";
	}
	/**
	 * 办结处理
	 * @return
	 */
	public String proDo(){
		if (guestbook!=null && guestbook.getId()!=null && guestbook.getId().trim().length()>0) {
			Guestbook updateguestbook=new Guestbook();
			updateguestbook.setId(guestbook.getId());
			updateguestbook.setRecontent(guestbook.getRecontent());
			updateguestbook.setRetime(new Date());
			updateguestbook.setState(guestbook.getState());
			updateguestbook.setReuserid(getLoginAdmin().getId());
			updateguestbook.setReusername(getLoginName());
			Guestbook oldguestbook=guestbookService.findById(guestbook.getId());
			try {
				guestbookService.update(updateguestbook);
				showMessage="回复成功";
				logContent="回复留言:"+oldguestbook.getTitle();
				forwardSeconds=3;
			} catch (Exception e) {
				showMessage="回复失败:"+e.getMessage();
				logContent="回复留言:"+oldguestbook.getTitle()+" 失败:"+e.getMessage();
			}finally{
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		return showMessage(showMessage, "guestbook_list.do?pageFuncId="+pageFuncId, forwardSeconds);
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
							guestbook=guestbookService.findById(idArr[i]);
							if (guestbook!=null) {
								guestbookService.del(guestbook.getId());
								sb.append(idArr[i]+";");
								logContent="回复留言("+guestbook.getTitle()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="回复留言("+guestbook.getTitle()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
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

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
