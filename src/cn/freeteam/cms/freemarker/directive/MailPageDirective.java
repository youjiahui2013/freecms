package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.service.MailService;
import cn.freeteam.cms.util.FreemarkerPager;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: MailPageDirective.java</p>
 * 
 * <p>Description: 信件列表 
 * 
 * 参数
 * num  		显示数量
 * querycode 	查询码
 * title		标题
 * writer		写信人
 * type			类型 unit部门信件 user个人信件 other 其他信件 空字符串表示所有(默认)
 * mailtype		信件类型 
 * state		办理状态 空字符串表示所有(默认) 1已办结 0办理中
 * isopen		是否公开 空字符串表示所有(默认) 1公开 0不公开
 * userid		指定用户id
 * unitid		指定部门id
 * order 		显示顺序
 * 				1.发表时间降序(默认)
 * 				2.发表时间升序
 * 				3.回复时间降序
 * 				4.回复时间升序
 * cache		是否使用缓存，默认为false
 * page			当前第几页，默认1		
 * action		分页跳转页面
 * titleLen		标题显示长度
 * 				
 * 
 * 返回值
 * mailList		信件对象列表
 * pager		分页对象
 * 
 * 示例
<@mailPage  num='1' page='${page}' action='${contextPath}templet_pro.do?siteid=${site.id}&templetPath=mail.html';mailList,pager>

<ul>
	<#list mailList as mail>
	<li>
		分页:${mail.title}
	</li>
	</#list>
</ul>
${pager.formPageStr}
</@mailPage>
 * </p>
 * <p>Date: Jan 21, 2013</p>
 * 
 * <p>Time: 9:43:36 PM</p>
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
public class MailPageDirective extends BaseDirective implements TemplateDirectiveModel{

	private MailService mailService;
	
	public MailPageDirective() {
		init("mailService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//显示数量
				int num=getParamInt(params, "num", 10);
				//标题长度
				int titleLen=getParamInt(params, "titleLen",0);
				//当前第几页
				int page=getParamInt(params, "page", 1);
				//排序
				String order=getParam(params, "order","1");
				Mail mail=new Mail();
				mail.setQuerycode(getParam(params, "querycode"));
				mail.setTitle(getParam(params, "title"));
				mail.setWriter(getParam(params, "writer"));
				mail.setType(getParam(params, "type"));
				mail.setMailtype(getParam(params, "mailtype"));
				mail.setState(getParam(params, "state"));
				mail.setIsopen(getParam(params, "isopen"));
				mail.setUnitid(getParam(params, "unitid"));
				mail.setUserid(getParam(params, "userid"));
				String orderSql="";
				if ("1".equals(order)) {
					//发布时间降序(默认)
					orderSql=" addtime desc";
				}
				else if ("2".equals(order)) {
					//发布时间升序
					orderSql=" addtime";
				}
				else if ("3".equals(order)) {
					//回复时间倒序
					orderSql=" retime desc";
				}
				else if ("4".equals(order)) {
					//回复时间升序
					orderSql=" retime";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				int count=mailService.count(mail, cache);
				FreemarkerPager pager=new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setAction(getParam(params, "action"));
				List<Mail> list=mailService.find(mail, orderSql, page, num,cache);
				if (list!=null && list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						if (titleLen>0 && list.get(i).getTitle().length()>titleLen) {
							//判断标题长度
							list.get(i).setTitle(list.get(i).getTitle().substring(0, titleLen));
						}
					}
				}
				loopVars[0]=new ArrayModel(list.toArray(),new BeansWrapper()); 
				if(loopVars.length>1){
					loopVars[1]=new BeanModel(pager,new BeansWrapper()); 
				}
				body.render(env.getOut());  
			}
		}
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
}