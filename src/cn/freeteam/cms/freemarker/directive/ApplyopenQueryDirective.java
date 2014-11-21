package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Applyopen;
import cn.freeteam.cms.service.ApplyopenService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 
 * <p>Title: ApplyopenQueryDirective.java</p>
 * 
 * <p>Description: 根据查询码查询申请处理结果</p>
 * 参数
 * querycode 查询码
 * cache		是否使用缓存，默认为false
 * 
 * 返回值
 * applyopen	applyopen对象
 * 
 * 示例
<@applyopenQuery querycode="" ;applyopen>
${applyopen.recontent}
</@applyopenQuery>
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
public class ApplyopenQueryDirective extends BaseDirective implements TemplateDirectiveModel{

	private ApplyopenService applyopenService;
	
	public ApplyopenQueryDirective() {
		init("applyopenService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		String querycode=getParam(params, "querycode");
		if (querycode.trim().length()>0) {
			if (body!=null) {
				//设置循环变量
				if (loopVars!=null && loopVars.length>0) {
					Applyopen applyopen=applyopenService.findByQuerycode(querycode,"true".equals(getParam(params, "cache"))?true:false);
					if (applyopen!=null) {
						loopVars[0]=new BeanModel(applyopen,new BeansWrapper());  
					}else {
						loopVars[0]=new BeanModel(new Applyopen(),new BeansWrapper());  
					}
					body.render(env.getOut());  
				}
			}
		}
	}

	public ApplyopenService getApplyopenService() {
		return applyopenService;
	}

	public void setApplyopenService(ApplyopenService applyopenService) {
		this.applyopenService = applyopenService;
	}
}