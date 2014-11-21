package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
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
 * <p>Title: UserListDirective.java</p>
 * 
 * <p>Description: 用户列表标签
 * 参数
 * filter	空字符:所有;"mail":可以接收互动信件的用户
 * cache		是否使用缓存，默认为false
 * 
 * 返回值
 * user			用户对象
 * index		索引
 *
<@userList filter="mail" cache="true";user,index>
${index+1} ${user.name}
</@userList>
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
public class UserListDirective extends BaseDirective implements TemplateDirectiveModel{
	private UserService userService;

	public UserListDirective() {
		init("userService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String filter=getParam(params, "filter");
		
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				String ismail="";
				if ("mail".equals(filter)) {
					ismail="1";
				}
				Users user=new Users();
				user.setIsmail(ismail);
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				List<Users> userList=userService.find(user,cache);
				if (userList!=null && userList.size()>0) {
					for (int i = 0; i < userList.size(); i++) {
						loopVars[0]=new BeanModel(userList.get(i),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
			}
		}
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}