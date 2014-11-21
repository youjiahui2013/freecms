package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreemarkerPager;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
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
 * <p>Title: InfoSignDirective.java</p>
 * 
 * <p>Description: 显示信息签收代码</p>
 * 参数 
 * infoid		信息id
 * show			显示签收内容，默认为"签收"
 * aAttr		签收链接显示a标签的属性
 * loadjs  		是否加载依赖的js
 * 
 * 返回值
 * js   生成的js
 * html 生成的显示内容
 * 
 * 此标签依赖的文件
<script type="text/javascript" src="${contextPath}js/jquery-1.5.1.min.js"></script>
<script type="text/javascript" src="${contextPath}js/weebox0.4/bgiframe.js"></script>
<script type="text/javascript" src="${contextPath}js/weebox0.4/weebox.js"></script>
<link type="text/css" rel="stylesheet" href="${contextPath}js/weebox0.4/weebox.css" />
 * 
 * 使用示例
<@infoSign infoid="${currInfo.id}" ;js,html>
${js}
${html}
</@infoSign>

 * <p>Date: Jan 15, 2013</p>
 * 
 * <p>Time: 7:57:45 PM</p>
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
public class InfoSignDirective extends BaseDirective implements TemplateDirectiveModel{

	private InfoService infoService;
	
	public InfoSignDirective(){
		init("infoService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {

		//获取参数
		//信息id
		String infoid=getParam(params, "infoid");
		//签收链接显示a标签的属性
		String aAttr=getParam(params, "aAttr");
		//显示签收内容，默认为签收
		String show=getParam(params, "show","签收");
		//是否加载引用的js
		String loadjs=getParam(params, "loadjs");
		
		//查询信息
		Info info=infoService.findById(infoid);
		if (info!=null && "1".equals(info.getIssign())) {
			Writer out =env.getOut();
			if (body!=null) {
				//设置循环变量
				if (loopVars!=null && loopVars.length>0 && infoid.trim().length()>0) {
					String contextPath=env.getDataModel().get("contextPath").toString();
					StringBuilder sb=new StringBuilder();
					if ("true".equals(loadjs)) {
						//导入js
						sb.append("<script src='"+contextPath+"js/jquery-1.5.1.min.js'></script>");
						sb.append("<script src='"+contextPath+"js/weebox0.4/bgiframe.js'></script>");
						sb.append("<script src='"+contextPath+"js/weebox0.4/weebox.js'></script>");
						//导入样式
						sb.append("<link type='text/css' rel='stylesheet' href='"+contextPath+"js/weebox0.4/weebox.css'/>");
					}
					//生成唯一标识
					String uuid=UUID.randomUUID().toString().replace("-", "");
					sb.append("<script>");
					//签收函数
					sb.append("function infosignFunc"+uuid+"(){");
					sb.append("$.weeboxs.open('"+contextPath+"info_sign.do?info.id="+infoid+"&date='+new Date(), {title:'信息签收', contentType:'ajax',height:420,width:450});");
					sb.append("}");
					sb.append("</script>");
					//生成显示内容
					StringBuilder html=new StringBuilder();
					html.append("<a id='infosign"+uuid+"' "+aAttr+" href='#' onclick='infosignFunc"+uuid+"()'>"+show+"</a>");
					loopVars[0]=new StringModel(sb.toString(),new BeansWrapper());  
					if(loopVars.length>1){
						loopVars[1]=new StringModel(html.toString(),new BeansWrapper());  
					}
					body.render(env.getOut());  
				}
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