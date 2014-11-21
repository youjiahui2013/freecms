package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Answer;
import cn.freeteam.cms.model.Job;
import cn.freeteam.cms.model.Question;
import cn.freeteam.cms.service.AnswerService;
import cn.freeteam.cms.service.JobService;
import cn.freeteam.cms.service.QuestionService;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: JobDirective.java</p>
 * 
 * <p>Description: 提取指定id的职位</p>
 * 参数
 * id	职位id
 * cache		是否使用缓存，默认为false
 * 
 * 
 * 返回值
 * job		职位对象
 * 
 * 示例
<@job id="03d86aaa-0b64-44a4-a1ff-e154591a8379" ; job>
${job.name!""}
</@job>
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
public class JobDirective extends BaseDirective implements TemplateDirectiveModel{

	private JobService jobService;
	
	public JobDirective() {
		init("jobService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//查询
				String id=getParam(params, "id");
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				Job job=jobService.findById(id, cache);
				if (job==null) {
					job=new Job();
				}
				loopVars[0]=new BeanModel(job,new BeansWrapper());  
				body.render(env.getOut());  
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