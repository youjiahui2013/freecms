package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.service.MailService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 
 * <p>Title: MailQueryDirective.java</p>
 * 
 * <p>Description: 根据查询码查询信件处理结果</p>
 * 参数
 * querycode 查询码
 * cache		是否使用缓存，默认为false
 * 
 * 返回值
 * mail	mail对象
 * 
 * 示例
<@mailQuery querycode="" ;mail>
${mail.recontent}
</@mailQuery>
 * 
 * <p>Date: Jan 18, 2013</p>
 * 
 * <p>Time: 7:23:23 PM</p>
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
public class MailQueryDirective extends BaseDirective implements TemplateDirectiveModel{

	private MailService mailService;
	
	public MailQueryDirective() {
		init("mailService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		String querycode=getParam(params, "querycode");
		if (querycode.trim().length()>0) {
			if (body!=null) {
				//设置循环变量
				if (loopVars!=null && loopVars.length>0) {
					Mail mail=mailService.findByQuerycode(querycode,"true".equals(getParam(params, "cache"))?true:false);
					if (mail!=null) {
						loopVars[0]=new BeanModel(mail,new BeansWrapper());  
					}else {
						loopVars[0]=new BeanModel(new Mail(),new BeansWrapper());  
					}
					body.render(env.getOut());  
				}
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