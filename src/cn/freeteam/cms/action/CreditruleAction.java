package cn.freeteam.cms.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Creditrule;
import cn.freeteam.cms.service.CreditruleService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
/**
 * 
 * <p>Title: CreditruleAction.java</p>
 * 
 * <p>Description:积分规则相关操作 </p>
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
public class CreditruleAction extends BaseAction{

	private CreditruleService creditruleService;
	private String msg;
	private String pageFuncId;
	private String order=" ordernum ";
	private String logContent;
	private String ids;
	
	private Creditrule creditrule;
	private List<Creditrule> creditruleList;
	
	public CreditruleAction() {
		init("creditruleService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (creditrule==null ){
			creditrule=new Creditrule();
		}
		if (order.trim().length()==0) {
			order=" ordernum ";
		}
		creditruleList=creditruleService.find(creditrule, order, currPage, pageSize);
		totalCount=creditruleService.count(creditrule);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("creditrule.name");
		pager.appendParam("creditrule.code");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("creditrule_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (creditrule!=null && creditrule.getId()!=null && creditrule.getId().trim().length()>0) {
			creditrule=creditruleService.findById(creditrule.getId());
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
			if (creditrule!=null && creditrule.getId()!=null) {
				oper="修改";
				creditruleService.update(creditrule);
			}else {
				//添加
				creditruleService.add(creditrule);
			}
			logContent=oper+"积分规则("+creditrule.getName()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"积分规则("+creditrule.getName()+")失败:"+e.toString()+"!";
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
							creditrule=creditruleService.findById(idArr[i]);
							if (creditrule!=null) {
								creditruleService.del(creditrule.getId());
								sb.append(idArr[i]+";");
								logContent="删除积分规则("+creditrule.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除积分规则("+creditrule.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}

	public CreditruleService getCreditruleService() {
		return creditruleService;
	}

	public void setCreditruleService(CreditruleService creditruleService) {
		this.creditruleService = creditruleService;
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

	public Creditrule getCreditrule() {
		return creditrule;
	}

	public void setCreditrule(Creditrule creditrule) {
		this.creditrule = creditrule;
	}

	public List<Creditrule> getCreditruleList() {
		return creditruleList;
	}

	public void setCreditruleList(List<Creditrule> creditruleList) {
		this.creditruleList = creditruleList;
	}
}
