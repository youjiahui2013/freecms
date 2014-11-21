package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Job;
import cn.freeteam.cms.service.JobService;
import cn.freeteam.cms.util.FreemarkerPager;
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
 * <p>Title: JobPageDirective.java</p>
 * 
 * <p>Description: 职位分页列表</p>
 * 参数
 * id			职位id
 * name			名称
 * cache		是否使用缓存，默认为false
 * order		排序 1时间倒序(默认) 2时间正序
 * num			数量
 * page			当前第几页，默认1		
 * action		分页跳转页面
 * siteid		站点id
 * isend		是否结束，1:是,0:否,空字符串：所有。
 * 
 * 
 * 返回值
 * jobList		职位对象列表
 * pager			分页对象
 * 
 * 示例
<@jobPage  num='1' page='${page!1}' siteid='${site.id}' action='${contextPath}templet_pro.do?siteid=${site.id}&currChannelid=${currChannel.id}&templetPath=job/jobPage.html';jobList,pager>

<ul>
	<#list jobList as job>
	<li>
		<a href="${contextPath}templet_pro.do?siteid=${site.id}&currChannelid=${currChannel.id}&templetPath=job/job.html&jobid=${job.id}">${job.name}</a>
	</li>
	</#list>
</ul>
${pager.formPageStr}
</@jobPage>
 * <p>Date: Jan 23, 2013</p>
 * 
 * <p>Time: 6:53:26 PM</p>
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
public class JobPageDirective extends BaseDirective implements TemplateDirectiveModel{


	private JobService jobService;
	
	public JobPageDirective() {
		init("jobService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		if (body != null) {
			// 设置循环变量
			if (loopVars != null && loopVars.length > 0) {
				// 查询
				// 显示数量
				int num = getParamInt(params, "num", 10);
				// 当前第几页
				int page = getParamInt(params, "page", 1);
				String id = getParam(params, "id");
				String siteid = getParam(params, "siteid");
				String order = getParam(params, "order");
				String orderSql = " addtime desc ";
				if ("2".equals(order)) {
					orderSql = " addtime ";
				}
				boolean cache = "true".equals(getParam(params, "cache")) ? true
						: false;
				Job job=new Job();
				job.setId(id);
				job.setSiteid(siteid);
				job.setName(getParam(params, "name"));
				job.setIsend(getParam(params, "isend"));
				List<Job> jobList = jobService.find(job,
						orderSql, page, num, cache);
				int count = jobService.count(job, cache);
				FreemarkerPager pager = new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setAction(getParam(params, "action"));
				loopVars[0] = new ArrayModel(jobList.toArray(),
						new BeansWrapper());
				if (loopVars.length > 1) {
					loopVars[1] = new BeanModel(pager, new BeansWrapper());
				}
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