package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import cn.freeteam.base.BaseDirective;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: URLEncoderDirective.java</p>
 * 
 * <p>Description: 调用URLEncoder类</p>
 * 参数
 * str		内容
 * encode	编码，默认UTF-8
 * 
 * 返回值
 * content	转码后的内容
 * 
 * 示例
<a href="${contextPath}templet_pro.do?siteid=${site.id}&templetPath=<@URLEncoder str="信件.html";content>${content}</@URLEncoder>">互动信件</a>
 * 
 * <p>Date: Jan 17, 2013</p>
 * 
 * <p>Time: 9:33:17 PM</p>
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
public class URLEncoderDirective extends BaseDirective implements TemplateDirectiveModel{
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//内容
		String str=getParam(params, "str");
		//编码，默认UTF-8
		String encode=getParam(params, "encode","UTF-8");
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && str.trim().length()>0) {
				StringBuilder sb=new StringBuilder();
				sb.append(URLEncoder.encode(str,encode));
				loopVars[0]=new StringModel(sb.toString(),new BeansWrapper());  
				body.render(env.getOut());  
			}
		}
	}
}
