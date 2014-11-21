package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.model.Unit;
import cn.freeteam.service.UnitService;
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
 * <p>Title: UnitListDirective.java</p>
 * 
 * <p>Description: 单位标签
 * 参数
 * parid    空字符:所有;"par":一级单位;"parid":此id下单位;
 * filter	空字符:所有;"mail":可以接收互动信件的单位
 * cache		是否使用缓存，默认为false
 * 
 * 返回值
 * unit			单位对象
 * index		索引
 *
<@unitList parid="" filter="mail" cache="true";unit,index>
${index+1} ${unit.name}
</@unitList>
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
public class UnitListDirective extends BaseDirective implements TemplateDirectiveModel{
	private UnitService unitService;

	public UnitListDirective() {
		init("unitService");
	}
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		String filter=getParam(params, "filter");
		String parid=getParam(params, "parid");// 空字符:所有;"par":一级单位;"parid":此id下单位;
		
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 ) {
				//查询
				String ismail="";
				if ("mail".equals(filter)) {
					ismail="1";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				List<Unit> unitList=unitService.findByPar(
						 parid,"1",ismail,cache);
				if (unitList!=null && unitList.size()>0) {
					for (int i = 0; i < unitList.size(); i++) {
						loopVars[0]=new BeanModel(unitList.get(i),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
			}
		}
	}

	public UnitService getUnitService() {
		return unitService;
	}

	public void setUnitService(UnitService unitService) {
		this.unitService = unitService;
	}

}
