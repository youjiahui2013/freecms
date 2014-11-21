package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Report;
import cn.freeteam.cms.service.ReportService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 
 * <p>Title: ReportQueryDirective.java</p>
 * 
 * <p>Description: 根据查询码查询申报处理结果</p>
 * 参数
 * querycode 查询码
 * cache		是否使用缓存，默认为false
 * 
 * 返回值
 * report	report对象
 * 
 * 示例
<@reportQuery querycode="" ;report>
${report.recontent}
</@reportQuery>
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
public class ReportQueryDirective extends BaseDirective implements TemplateDirectiveModel{

	private ReportService reportService;
	
	public ReportQueryDirective() {
		init("reportService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		String querycode=getParam(params, "querycode");
		if (querycode.trim().length()>0) {
			if (body!=null) {
				//设置循环变量
				if (loopVars!=null && loopVars.length>0) {
					Report report=reportService.findByQuerycode(querycode,"true".equals(getParam(params, "cache"))?true:false);
					if (report!=null) {
						loopVars[0]=new BeanModel(report,new BeansWrapper());  
					}else {
						loopVars[0]=new BeanModel(new Report(),new BeansWrapper());  
					}
					body.render(env.getOut());  
				}
			}
		}
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
}