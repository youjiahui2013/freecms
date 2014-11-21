package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.model.Demo;
import cn.freeteam.service.DemoService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 
 * <p>Title: DemoDirective.java</p>
 * 
 * <p>Description: 演示标签
 * 参数
 * id		
 * 
 * 返回值
 * demo			演示对象
 * 
 * 
 * 使用示例
<@demo id="1c5c3311-62c3-4548-8573-51ba6cd6eb66";demo>
${demo.textdemo}
</@demo>
 * </p>
 * 
 * <p>Date: Jun 19, 2013</p>
 * 
 * <p>Time: 1:39:41 PM</p>
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
public class DemoDirective extends BaseDirective implements TemplateDirectiveModel{
	
	private DemoService demoService;
	
	public DemoDirective() {
		init("demoService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {

		//获取参数
		//id
		String id=getParam(params, "id");
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				Demo demo=demoService.findById(id);
				loopVars[0]=new BeanModel(demo,new BeansWrapper());  
				body.render(env.getOut()); 
			}
		}
		
	}

	public DemoService getDemoService() {
		return demoService;
	}

	public void setDemoService(DemoService demoService) {
		this.demoService = demoService;
	}
}