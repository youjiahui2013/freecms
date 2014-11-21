package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Job;
import cn.freeteam.cms.service.JobService;
import cn.freeteam.model.Demo;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
/**
 * 
 * <p>Title: JobAction.java</p>
 * 
 * <p>Description:职位功能相关操作 </p>
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
public class JobAction extends BaseAction{

	private JobService jobService;
	private String msg;
	private String pageFuncId;
	private String order="";
	private String logContent;
	private String ids;
	private Job job;
	private List<Job> jobList;
	
	

	
	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (job==null) {
			job=new Job();
		}
		job.setSiteid(getManageSite().getId());
		jobList=jobService.find(job, order, currPage, pageSize,false);
		totalCount=jobService.count(job,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("job.name");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("job_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}

	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (job!=null && job.getId()!=null && job.getId().trim().length()>0) {
			job=jobService.findById(job.getId(),false);
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
			if (job!=null && StringUtils.isNotEmpty(job.getId())) {
				oper="修改";
				jobService.update(job);
			}else {
				//添加
				if (job.getAddtime()==null) {
					job.setAddtime(new Date());
				}
				jobService.add(job);
			}
			logContent=oper+"职位("+job.getName()+")成功!";
			forwardUrl="job_list.do?pageFuncId="+pageFuncId;
			forwardSeconds=3;
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"职位("+job.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return showMessage(logContent, forwardUrl, forwardSeconds);
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
							job=jobService.findById(idArr[i],false);
							if (job!=null) {
								jobService.del(job.getId());
								sb.append(idArr[i]+";");
								logContent="职位("+job.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="职位("+job.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
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

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public List<Job> getJobList() {
		return jobList;
	}

	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}

	public JobAction() {
		init("jobService");
	}

	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
}
