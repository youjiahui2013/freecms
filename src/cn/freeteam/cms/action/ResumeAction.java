package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Resume;
import cn.freeteam.cms.service.ResumeService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: ResumeAction.java</p>
 * 
 * <p>Description:简历相关操作 </p>
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
public class ResumeAction extends BaseAction{

	private ResumeService resumeService;
	private Resume resume;
	private List<Resume> resumeList;
	private String order=" addtime ";
	private String msg;
	private String pageFuncId;
	private String logContent;
	private String ids;
	
	public ResumeAction() {
		init("resumeService");
	}


	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (resume==null ){
			resume=new Resume();
		}
		resume.setSiteid(getManageSite().getId());
		resumeList=resumeService.find(resume, order, currPage, pageSize);
		totalCount=resumeService.count(resume);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("resume.job");
		pager.appendParam("resume.name");
		pager.appendParam("resume.membername");
		pager.appendParam("resume.reusername");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("resume_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	

	/**
	 * 处理页面
	 * @return
	 */
	public String pro(){
		if (resume!=null && resume.getId()!=null && resume.getId().trim().length()>0) {
			resume=resumeService.findById(resume.getId());
		}
		return "pro";
	}
	/**
	 * 办结处理
	 * @return
	 */
	public String proDo(){
		if (resume!=null && resume.getId()!=null && resume.getId().trim().length()>0) {
			Resume updateresume=new Resume();
			updateresume.setId(resume.getId());
			updateresume.setRecontent(resume.getRecontent());
			updateresume.setRetime(new Date());
			updateresume.setReuserid(getLoginAdmin().getId());
			updateresume.setReusername(getLoginName());
			updateresume.setState("1");
			Resume oldresume=resumeService.findById(resume.getId());
			try {
				resumeService.update(updateresume);
				showMessage="回复成功";
				logContent="回复简历:"+oldresume.getName();
				forwardSeconds=3;
				forwardUrl="resume_list.do?pageFuncId="+pageFuncId;
			} catch (Exception e) {
				showMessage="回复失败:"+e.getMessage();
				logContent="回复简历:"+oldresume.getName()+" 失败:"+e.getMessage();
			}finally{
				OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
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
							resume=resumeService.findById(idArr[i]);
							if (resume!=null) {
								resumeService.del(resume.getId());
								sb.append(idArr[i]+";");
								logContent="回复简历("+resume.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="回复简历("+resume.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}

	
	
	public ResumeService getResumeService() {
		return resumeService;
	}

	public void setResumeService(ResumeService resumeService) {
		this.resumeService = resumeService;
	}


	public Resume getResume() {
		return resume;
	}


	public void setResume(Resume resume) {
		this.resume = resume;
	}


	public List<Resume> getResumeList() {
		return resumeList;
	}


	public void setResumeList(List<Resume> resumeList) {
		this.resumeList = resumeList;
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
