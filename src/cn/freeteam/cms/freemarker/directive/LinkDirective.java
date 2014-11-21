package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Link;
import cn.freeteam.cms.service.LinkService;


import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: LinkDirective.java</p>
 * 
 * <p>Description: 链接标签
 * 参数
 * classId		分类id
 * pagemark		页面标识，多个之间使用,
 * classPagemark	分类页面标识，多个之间使用,
 * siteid		站点id
 * type			类型
 * 				1 下拉
 * 				2 图片
 * 				3 文字
 * num			数量
 * 
 * 返回值
 * link			链接对象
 * index        索引
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
public class LinkDirective extends BaseDirective implements TemplateDirectiveModel{
private LinkService linkService;
	
	public LinkDirective(){
		init("linkService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String classId=getParam(params, "classId");
		String classPagemark=getParam(params, "classPagemark");
		String pagemark=getParam(params, "pagemark");
		String siteid=getParam(params, "siteid");
		String type=getParam(params, "type");
		int num=getParamInt(params, "num", 1000);
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				Link link=new Link();
				link.setParid(classId);
				link.setPagemarks(pagemark);
				link.setClassPagemarks(classPagemark);
				link.setSite(siteid);
				link.setType(type);
				link.setIsok("1");
				List<Link> linkList=linkService.find(link, " ordernum ", 1, num);
				if (linkList!=null && linkList.size()>0) {
					for (int i = 0; i < linkList.size(); i++) {
						loopVars[0]=new BeanModel(linkList.get(i),new BeansWrapper());   
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
			}
		}
	}

	public LinkService getLinkService() {
		return linkService;
	}

	public void setLinkService(LinkService linkService) {
		this.linkService = linkService;
	}
}
