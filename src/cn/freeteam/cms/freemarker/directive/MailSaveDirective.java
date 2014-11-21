package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.service.MailService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: MailSaveDirective.java</p>
 * 
 * <p>Description: 信件保存处理</p>
 * 
 * 返回值
 * msg	处理结果
 * 
 * 示例
<@mailSave ;msg>
${msg}
</@mailSave>
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
public class MailSaveDirective extends BaseDirective implements TemplateDirectiveModel{

	private MailService mailService;
	
	public MailSaveDirective() {
		init("mailService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		String msg="";
		boolean issave=false;
		//获取参数设置属性
		if(getData(env, "validatecode")!=null){
			//如果有验证码则先判断验证码是否正确
			if (!getData(env, "validatecode").equals(getData(env, "session_rand"))) {
				msg="验证码错误";
			}else {
				issave=true;
			}
		}
		if (issave) {
			Mail mail=new Mail();
			if(getData(env, "unitid")!=null){
				mail.setUnitid(getData(env, "unitid"));
			}
			if(getData(env, "userid")!=null){
				mail.setUserid(getData(env, "userid"));
			}
			if(getData(env, "mailtype")!=null){
				mail.setMailtype(getData(env, "mailtype"));
			}
			if(getData(env, "title")!=null){
				mail.setTitle(getData(env, "title"));
			}
			if(getData(env, "writer")!=null){
				mail.setWriter(getData(env, "writer"));
			}
			if(getData(env, "tel")!=null){
				mail.setTel(getData(env, "tel"));
			}
			if(getData(env, "address")!=null){
				mail.setAddress(getData(env, "address"));
			}
			if(getData(env, "email")!=null){
				mail.setEmail(getData(env, "email"));
			}
			if(getData(env, "isopen")!=null){
				mail.setIsopen(getData(env, "isopen"));
			}
			if(getData(env, "content")!=null){
				mail.setContent(getData(env, "content"));
			}
			if(getData(env, "request_remoteAddr")!=null){
				mail.setIp(getData(env, "request_remoteAddr"));
			}
			//生成查询码
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			mail.setQuerycode(sdf.format(new Date())+(int)(Math.random()*1000));
			mail.setAddtime(new Date());
			try {
				mailService.insert(mail);
				msg="感谢您的来信，我们会尽快回复，您可以通过查询码"+mail.getQuerycode()+"查询信件！";
			} catch (Exception e) {
				e.printStackTrace();
				msg=e.getMessage();
			}
		}
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				loopVars[0]=new StringModel(msg,new BeansWrapper());  
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