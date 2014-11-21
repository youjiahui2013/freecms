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
 * 
 * <p>Title: AjaxLoadDirective.java</p>
 * 
 * <p>Description: 通过ajax加载页面</p>
 * 
 * 参数
 * targetid	把页面加载到指定id元素下
 * url		页面地址
 * param	参数，使用json数据
 * method	获取方法，get(默认)或post
 * loadjs  		是否加载依赖的js
 * 
 * 返回值
 * code		生成的ajax代码
 * 
 * 此标签依赖的文件
<script type="text/javascript" src="${contextPath}js/jquery-1.5.1.min.js"></script>
 * 示例
 <div id="ajaxLoadDiv">
 <@ajaxLoad targetid="ajaxLoadDiv" url="${contextPath}test.jsp" param="id:'1',name:'姓名'" method="post";code>
 ${code}
 </@ajaxLoad>
 </div>
 * 
 * <p>Date: Jan 10, 2013</p>
 * 
 * <p>Time: 8:20:19 PM</p>
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
public class AjaxLoadDirective extends BaseDirective implements TemplateDirectiveModel{
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//把页面加载到指定id元素下
		String targetid=getParam(params, "targetid");
		//页面地址
		String url=getParam(params, "url");
		//参数，使用json数据
		String param=getParam(params, "param");
		//获取方法，get(默认)或post
		String method=getParam(params, "method","get");
		//是否加载引用的js
		String loadjs=getParam(params, "loadjs");
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && targetid.trim().length()>0 && url.trim().length()>0) {
				String contextPath=env.getDataModel().get("contextPath").toString();
				StringBuilder sb=new StringBuilder();
				if ("true".equals(loadjs)) {
					//导入js
					sb.append("<script src='"+contextPath+"js/jquery-1.5.1.min.js'></script>");
				}
				//生成唯一标识
				String uuid=UUID.randomUUID().toString().replace("-", "");
				//生成显示点击量的span,默认显示loading
				sb.append("<img src='"+contextPath+"js/images/ajax-loader.gif'/>");
				sb.append("<script>");
				//执行ajax操作
				sb.append("$."+method+"('"+url+"',{"+param.replaceAll("'", "\"")+"},ajaxLoadComplete"+uuid+");");
				//回调函数
				sb.append("function ajaxLoadComplete"+uuid+"(data){");
				//显示点击量
				sb.append("$('#"+targetid+"').html(data);");
				sb.append("}");
				sb.append("</script>");
				loopVars[0]=new StringModel(sb.toString(),new BeansWrapper());  
				body.render(env.getOut());  
			}
		}
	}
}