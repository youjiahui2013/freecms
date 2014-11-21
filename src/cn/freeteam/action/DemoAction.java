package cn.freeteam.action;

import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.model.Demo;
import cn.freeteam.service.DemoService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
/**
 * 
 * <p>Title: DemoAction.java</p>
 * 
 * <p>Description:演示功能相关操作 </p>
 * 
 * <p>Date: Jun 19, 2013</p>
 * 
 * <p>Time: 1:41:57 PM</p>
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
public class DemoAction extends BaseAction{

	private DemoService demoService;
	private String msg;
	private String pageFuncId;
	private String order="";
	private String logContent;
	private String ids;
	private List<Demo> demoList;
	private Demo demo;
	
	public DemoAction() {
		init("demoService");
	}
	
	
	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (demo==null ){
			demo=new Demo();
		}
		demoList=demoService.find(demo, order, currPage, pageSize);
		totalCount=demoService.count(demo);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("demo.textdemo");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("demo_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (demo!=null && demo.getId()!=null && demo.getId().trim().length()>0) {
			demo=demoService.findById(demo.getId());
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
			if (demo!=null && demo.getId()!=null) {
				oper="修改";
				demoService.update(demo);
			}else {
				//添加
				demoService.add(demo);
			}
			logContent=oper+"演示功能("+demo.getTextdemo()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"演示功能("+demo.getTextdemo()+")失败:"+e.toString()+"!";
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
							demo=demoService.findById(idArr[i]);
							if (demo!=null) {
								demoService.del(demo.getId());
								sb.append(idArr[i]+";");
								logContent="演示功能("+demo.getTextdemo()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="演示功能("+demo.getTextdemo()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}

	

	public DemoService getDemoService() {
		return demoService;
	}

	public void setDemoService(DemoService demoService) {
		this.demoService = demoService;
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

	public List<Demo> getDemoList() {
		return demoList;
	}

	public void setDemoList(List<Demo> demoList) {
		this.demoList = demoList;
	}

	public Demo getDemo() {
		return demo;
	}

	public void setDemo(Demo demo) {
		this.demo = demo;
	}
	
}
