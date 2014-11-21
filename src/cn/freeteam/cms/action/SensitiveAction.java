package cn.freeteam.cms.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Sensitive;
import cn.freeteam.cms.service.SensitiveService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;


/**
 * 
 * <p>Title: SensitiveAction.java</p>
 * 
 * <p>Description:敏感词相关操作 </p>
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
public class SensitiveAction extends BaseAction{

	private SensitiveService sensitiveService;
	private String msg;
	private String pageFuncId;
	private String order="";
	private String logContent;
	private String ids;
	private List<Sensitive> sensitiveList;
	private Sensitive sensitive;

	public SensitiveAction() {
		init("sensitiveService");
	}
	

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (sensitive==null ){
			sensitive=new Sensitive();
		}
		sensitiveList=sensitiveService.find(sensitive, order, currPage, pageSize);
		totalCount=sensitiveService.count(sensitive);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("sensitive.sensitiveword");
		pager.appendParam("sensitive.replaceto");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("sensitive_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (sensitive!=null && sensitive.getId()!=null && sensitive.getId().trim().length()>0) {
			sensitive=sensitiveService.findById(sensitive.getId());
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
			if (sensitive!=null && sensitive.getId()!=null) {
				oper="修改";
				sensitiveService.update(sensitive);
			}else {
				//添加
				sensitiveService.add(sensitive);
			}
			logContent=oper+"敏感词("+sensitive.getSensitiveword()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"敏感词("+sensitive.getSensitiveword()+")失败:"+e.toString()+"!";
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
							sensitive=sensitiveService.findById(idArr[i]);
							if (sensitive!=null) {
								sensitiveService.del(sensitive.getId());
								sb.append(idArr[i]+";");
								logContent="删除敏感词("+sensitive.getSensitiveword()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除敏感词("+sensitive.getSensitiveword()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}


	public SensitiveService getSensitiveService() {
		return sensitiveService;
	}

	public void setSensitiveService(SensitiveService sensitiveService) {
		this.sensitiveService = sensitiveService;
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


	public List<Sensitive> getSensitiveList() {
		return sensitiveList;
	}


	public void setSensitiveList(List<Sensitive> sensitiveList) {
		this.sensitiveList = sensitiveList;
	}


	public Sensitive getSensitive() {
		return sensitive;
	}


	public void setSensitive(Sensitive sensitive) {
		this.sensitive = sensitive;
	}

}
