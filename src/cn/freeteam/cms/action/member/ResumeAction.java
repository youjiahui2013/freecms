package cn.freeteam.cms.action.member;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Resume;
import cn.freeteam.cms.service.ResumeService;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: ResumeAction.java</p>
 * 
 * <p>Description: 简历相关操作</p>
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
public class ResumeAction extends BaseAction{

	private ResumeService resumeService;
	private Resume resume;
	private List<Resume> resumeList;
	private String order=" addtime desc ";
	private String ids;
	

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (resume==null ){
			resume=new Resume();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		resume.setMemberid(getLoginMember().getId());
		resumeList=resumeService.find(resume, order, currPage, pageSize);
		totalCount=resumeService.count(resume);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("resume.job");
		pager.appendParam("resume.state");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStrNoTable("resume_list.do");
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
							resumeService.del(idArr[i]);
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
		if (resume!=null && StringUtils.isNotEmpty(resume.getId())) {
			resume=resumeService.findById(resume.getId());
		}
		return "info";
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

	public ResumeAction() {
		init("resumeService");
	}

	public ResumeService getResumeService() {
		return resumeService;
	}

	public void setResumeService(ResumeService resumeService) {
		this.resumeService = resumeService;
	}
	
}
