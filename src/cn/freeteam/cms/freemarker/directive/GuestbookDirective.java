package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.service.GuestbookService;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 
 * <p>Title: GuestbookDirective.java</p>
 * 
 * <p>Description: 留言标签
 * 参数
 * id		
 * 
 * 返回值
 * guestbook			留言对象
 * 
 * 
 * 使用示例
<@guestbook id="1c5c3311-62c3-4548-8573-51ba6cd6eb66";guestbook>
${guestbook.title}
</@guestbook>
 * </p>
 * 
 * <p>Date: Jun 19, 2013</p>
 * 
 * <p>Time: 1:39:41 PM</p>
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
public class GuestbookDirective extends BaseDirective implements TemplateDirectiveModel{
	
	private GuestbookService guestbookService;
	
	public GuestbookDirective() {
		init("guestbookService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {

		//获取参数
		//id
		String id=getParam(params, "id");
		
		
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				Guestbook guestbook=guestbookService.findById(id);
				loopVars[0]=new BeanModel(guestbook,new BeansWrapper());  
				body.render(env.getOut()); 
			}
		}
		
	}

	public GuestbookService getGuestbookService() {
		return guestbookService;
	}

	public void setGuestbookService(GuestbookService guestbookService) {
		this.guestbookService = guestbookService;
	}
}