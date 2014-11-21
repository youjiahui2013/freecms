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
 * <p>Title: LinkClassDirective.java</p>
 * 
 * <p>Description: 链接分类标签
 * 参数
 * siteid		站点id
 * classid		分类id
 * pagemark	分类页面标识
 * type			类型
 * 				1 下拉
 * 				2 图片
 * 				3 文字
 * 
 * 
 * 返回值
 * linkClass	分类对象
 * index        索引
 * 
 * </p>
 * 
 * <p>Date: May 16, 2012</p>
 * 
 * <p>Time: 12:49:01 PM</p>
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
public class LinkClassDirective extends BaseDirective implements TemplateDirectiveModel{
	private LinkService linkService;
	
	public LinkClassDirective(){
		init("linkService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String siteid=getParam(params, "siteid");
		String type=getParam(params, "type");
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && siteid.trim().length()>0) {
				//查询
				Link link=new Link();
				link.setIsClass("1");
				link.setSite(siteid);
				link.setType(type);
				link.setIsok("1");
				link.setId(getParam(params, "classid"));
				link.setPagemark(getParam(params, "pagemark"));
				List<Link> linkList=linkService.findAll(link, " ordernum ");
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
