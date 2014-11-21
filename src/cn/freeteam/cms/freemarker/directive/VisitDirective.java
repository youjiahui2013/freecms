package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Visit;
import cn.freeteam.cms.service.VisitService;
import cn.freeteam.util.DateUtil;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 
 * <p>Title: VisitDirective.java</p>
 * 
 * <p>Description:访问统计 </p>
 * 
 * 参数
 * siteid	站点id
 * type		统计类型
 * 			today	今天
 * 			yesterday	昨天
 * 			month	本月
 * 
 * 返回值
 * count	统计量
 * 
 * <p>Date: Jul 8, 2013</p>
 * 
 * <p>Time: 9:17:05 PM</p>
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
public class VisitDirective extends BaseDirective implements TemplateDirectiveModel{

	private VisitService visitService;
	public VisitDirective() {
		init("visitService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//站点id
		String siteid=getParam(params, "siteid");
		//类型
		String type=getParam(params, "type");
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				
				java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
			    java.util.Calendar calendar=java.util.Calendar.getInstance();
			    String today=df.format(calendar.getTime());
			    calendar.roll(java.util.Calendar.DAY_OF_YEAR,-1);
			    String yesterday=df.format(calendar.getTime());
			    calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
			    calendar.roll(java.util.Calendar.MONTH,-1);
			    String month=df.format(calendar.getTime());
			    
				Visit visit=new Visit();
				visit.setSiteid(siteid);
				if ("today".equals(type)) {
					//今天
					visit.setStarttime(DateUtil.parse(today, "yyyy-MM-dd"));
				}else if ("yesterday".equals(type)) {
					//昨天
					visit.setStarttime(DateUtil.parse(yesterday, "yyyy-MM-dd"));
					visit.setEndtime(DateUtil.parse(today, "yyyy-MM-dd"));
				}else if ("month".equals(type)) {
					//本月
					visit.setStarttime(DateUtil.parse(month, "yyyy-MM-dd"));
				}
				
				loopVars[0]=new SimpleNumber(visitService.count(visit,"true".equals(getParam(params, "cache"))?true:false));  
				body.render(env.getOut()); 
			}
		}
	}
	
	public VisitService getVisitService() {
		return visitService;
	}
	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}
}
