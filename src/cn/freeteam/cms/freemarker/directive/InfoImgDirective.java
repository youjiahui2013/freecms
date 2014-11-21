package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.InfoImg;
import cn.freeteam.cms.service.InfoImgService;
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
 * <p>Title: InfoImgDirective.java</p>
 * 
 * <p>Description: 信息图片集标签
 * 参数
 * infoid		 信息id
 * 
 * 返回值
 * infoImgList			信息图片集对象列表
 * 
 * 
 * 使用示例
 * </p>
 * 
 * <p>Date: May 14, 2012</p>
 * 
 * <p>Time: 1:45:03 PM</p>
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
public class InfoImgDirective extends BaseDirective implements TemplateDirectiveModel{

	private InfoImgService infoImgService;
	

	public InfoImgDirective(){
		init("infoImgService");
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//信息id
		String infoid=getParam(params, "infoid");
		Writer out =env.getOut();
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && infoid.trim().length()>0) {
				//查询信息图片集
				InfoImg infoImg=new InfoImg();
				infoImg.setInfoid(infoid);
				List<InfoImg> infoImgList=infoImgService.find(infoImg, " ordernum ");
				if (infoImgList!=null && infoImgList.size()>0) {
					loopVars[0]=new ArrayModel(infoImgList.toArray(),new BeansWrapper()); 
					body.render(env.getOut());  
				}
			}
		}
	}

	public InfoImgService getInfoImgService() {
		return infoImgService;
	}

	public void setInfoImgService(InfoImgService infoImgService) {
		this.infoImgService = infoImgService;
	}
}