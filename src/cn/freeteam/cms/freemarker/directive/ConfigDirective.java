package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.model.Config;
import cn.freeteam.model.Unit;
import cn.freeteam.service.ConfigService;
import cn.freeteam.service.UnitService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 
 * <p>Title: ConfigDirective.java</p>
 * 
 * <p>Description: 系统配置标签
 * 参数
 * code		系统配置编码
 * split	配置值分隔符，如果设置则则用分隔符分隔以数组形式处理配置值
 * 
 * 返回值
 * value		配置值
 * index		索引
 *
<@config code="mailType" split=",";configvalue,index>
${index+1} ${configvalue}
</@config>
 * 
 * </p>
 * 
 * <p>Date: May 16, 2012</p>
 * 
 * <p>Time: 1:00:26 PM</p>
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
public class ConfigDirective extends BaseDirective implements TemplateDirectiveModel{
	private ConfigService configService;

	public ConfigDirective() {
		init("configService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String code=getParam(params, "code");
		String split=getParam(params, "split");
		
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				Config config=configService.findByCode(code);
				if (config!=null && config.getConfigvalue()!=null) {
					if (split.length()>0) {
						//有分隔符，以数组形式处理
						String[] values=config.getConfigvalue().split(split);
						if (values!=null && values.length>0) {
							for (int i = 0; i < values.length; i++) {
								loopVars[0]=new StringModel(values[i],new BeansWrapper());  
								if(loopVars.length>1){
									loopVars[1]=new SimpleNumber(i);
								}
								body.render(env.getOut());  
							}
						}
					}else {
						loopVars[0]=new StringModel(config.getConfigvalue(),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(0);
						}
						body.render(env.getOut()); 
					}
				}
			}
		}
	}
	public ConfigService getConfigService() {
		return configService;
	}
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

}