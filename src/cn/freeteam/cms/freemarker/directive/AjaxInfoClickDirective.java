package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
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
 * <p>Title: AjaxInfoClickDirective.java</p>
 * 
 * <p>Description: AjaxInfoClickDirective</p>
 * 参数 
 * infoid		信息id
 * spanAttr		点击量显示span的属性
 * loadjs  		是否加载依赖的js
 * 
 * 返回值
 * ajaxInfoClickHtml 生成的内容
 * 
 * 此标签依赖的文件
<script type="text/javascript" src="${contextPath}js/jquery-1.5.1.min.js"></script>
 * <p>Date: May 22, 2012</p>
 * 
 * <p>Time: 8:11:13 PM</p>
 * 
 * <p>Copyright: 2011</p>
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
public class AjaxInfoClickDirective extends BaseDirective implements TemplateDirectiveModel{
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//信息id
		String infoid=getParam(params, "infoid");
		//点击量显示span的属性
		String spanAttr=getParam(params, "spanAttr");
		//是否加载引用的js
		String loadjs=getParam(params, "loadjs");
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && infoid.trim().length()>0) {
				String contextPath=env.getDataModel().get("contextPath").toString();
				StringBuilder sb=new StringBuilder();

				if ("true".equals(loadjs)) {
					//导入js
					sb.append("<script src='"+contextPath+"js/jquery-1.5.1.min.js'></script>");
				}
				//生成唯一标识
				String uuid=UUID.randomUUID().toString().replace("-", "");
				//生成显示点击量的span,默认显示loading
				sb.append("<span id='ajaxInfoClickSpan"+uuid+"' "+spanAttr+"><img src='"+contextPath+"js/images/ajax-loader.gif'/></span>");
				sb.append("<script>");
				//执行ajax操作
				sb.append("$.post('"+contextPath+"info_ajaxClick.do','info.id="+infoid+"',ajaxInfoClickComplete"+uuid+",'text');");
				//回调函数
				sb.append("function ajaxInfoClickComplete"+uuid+"(data){");
				//显示点击量
				sb.append("$('#ajaxInfoClickSpan"+uuid+"').html(data);");
				sb.append("}");
				sb.append("</script>");
				loopVars[0]=new StringModel(sb.toString(),new BeansWrapper());  
				body.render(env.getOut());  
			}
		}
	}
}
