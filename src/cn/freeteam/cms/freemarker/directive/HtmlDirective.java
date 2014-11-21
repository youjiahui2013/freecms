package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.util.HtmlCode;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 
 * <p>Title: HtmlDirective.java</p>
 * 
 * <p>Description: html功能标签
 * 参数
 * code		html代码
 * length	显示长度 
 * delHtml	删除html标签 默认删除 0不删除
 * 
 * 返回值
 * value	处理后的值
 *
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
public class HtmlDirective extends BaseDirective implements TemplateDirectiveModel{
		public void execute(Environment env, Map params, TemplateModel[] loopVars, 
				TemplateDirectiveBody body)throws TemplateException, IOException {
			
			//获取参数
			String code=getParam(params, "code");
			int length=getParamInt(params, "length",0);
			String delHtml=getParam(params, "delHtml");
			if (body!=null) {
				//设置循环变量
				if (loopVars!=null && loopVars.length>0 ) {
					if (code.trim().length()>0) {
						if (!"0".equals(delHtml)) {
							code=HtmlCode.replaceHtml(code);
						}
						if (length>0 && code.length()>length) {
							code=code.substring(0, length);
						}
					}
					loopVars[0]=new StringModel(code,new BeansWrapper());  
					body.render(env.getOut());  
				}
			}
		}
}
