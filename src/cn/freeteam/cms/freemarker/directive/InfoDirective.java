package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.service.InfoService;


import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: InfoDirective.java</p>
 * 
 * <p>Description: 信息标签
 * 参数
 * id		
 * 
 * 返回值
 * info			信息对象
 * 
 * 
 * 使用示例
<@info id="1c5c3311-62c3-4548-8573-51ba6cd6eb66";info>
${info.title}
</@info>
 * </p>
 * 
 * <p>Date: May 14, 2012</p>
 * 
 * <p>Time: 1:45:03 PM</p>
 * 
 * <p>Copyright: 2012</p>
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
public class InfoDirective extends BaseDirective implements TemplateDirectiveModel{

	private InfoService infoService;
	
	public InfoDirective(){
		init("infoService");
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
				//查询信息
				Info info=infoService.findById(id);
				loopVars[0]=new BeanModel(info,new BeansWrapper());  
				body.render(env.getOut()); 
			}
		}
	}
	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}

}
