package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Job;
import cn.freeteam.cms.service.JobService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: JobListDirective.java</p>
 * 
 * <p>Description: 职位列表</p>
 * 参数
 * id			职位id
 * name			名称
 * cache		是否使用缓存，默认为false
 * order		排序 1时间倒序(默认) 2时间正序
 * num			数量
 * nameLen		名称显示长度
 * siteid		站点id
 * isend		是否结束，1:是,0:否,空字符串：所有。
 * 
 * 
 * 返回值
 * job			职位对象
 * index			索引
 * 
 * 示例
<@jobList ;job,index>
${index} : ${job.name}<br>
</@jobList>
 * 
 * <p>Date: Jan 18, 2013</p>
 * 
 * <p>Time: 7:23:23 PM</p>
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
public class JobListDirective extends BaseDirective implements TemplateDirectiveModel{

	private JobService jobService;
	
	public JobListDirective() {
		init("jobService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//查询
				String id=getParam(params, "id");
				String siteid=getParam(params, "siteid");
				String order=getParam(params, "order");
				String orderSql=" addtime desc ";
				//标题长度
				int nameLen=getParamInt(params, "nameLen",0);
				if ("2".equals(order)) {
					orderSql=" addtime ";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				Job job=new Job();
				job.setId(id);
				job.setSiteid(siteid);
				job.setName(getParam(params, "name"));
				job.setIsend(getParam(params, "isend"));
				List<Job> jobList=jobService.find(job, orderSql, 1, getParamInt(params, "num", 1), cache);
				if (jobList!=null && jobList.size()>0) {
					for (int i = 0; i < jobList.size(); i++) {
						if (nameLen>0 && jobList.get(i).getName().length()>nameLen) {
							jobList.get(i).setName(jobList.get(i).getName().substring(0, nameLen));
						}
						loopVars[0]=new BeanModel(jobList.get(i),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
			}
		}
	}


	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}


}