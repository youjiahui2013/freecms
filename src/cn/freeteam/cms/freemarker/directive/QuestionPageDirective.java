package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Question;
import cn.freeteam.cms.service.QuestionService;
import cn.freeteam.cms.util.FreemarkerPager;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: QuestionPageDirective.java</p>
 * 
 * <p>Description: 网上调查列表</p>
 * 参数
 * id			网上调查id
 * name			名称
 * selecttype	选择类型 空字符串表示所有(默认) 0单选 1多选
 * isok			有效 空字符串表示所有(默认) 0无效 1有效
 * cache		是否使用缓存，默认为false
 * order		排序 1时间倒序(默认) 2时间正序
 * num			数量
 * page			当前第几页，默认1		
 * action		分页跳转页面
 * siteid		站点id
 * 
 * 
 * 返回值
 * questionList		网上调查对象列表
 * pager			分页对象
 * 
 * 示例
<@questionPage  num='1' page='${page!1}' action='${contextPath}templet_pro.do?siteid=${site.id}&templetPath=question.html';questionList,pager>

<ul>
	<#list questionList as question>
	<li>
		${question.name}
	</li>
	</#list>
</ul>
${pager.formPageStr}
</@questionPage>
 * <p>Date: Jan 23, 2013</p>
 * 
 * <p>Time: 6:53:26 PM</p>
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
public class QuestionPageDirective extends BaseDirective implements TemplateDirectiveModel{

	private QuestionService questionService;
	
	public QuestionPageDirective() {
		init("questionService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//查询网上调查
				//显示数量
				int num=getParamInt(params, "num", 10);
				//当前第几页
				int page=getParamInt(params, "page", 1);
				String id=getParam(params, "id");
				String siteid=getParam(params, "siteid");
				String order=getParam(params, "order");
				String orderSql=" addtime desc ";
				if ("2".equals(order)) {
					orderSql=" addtime ";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				Question question=new Question();
				question.setId(id);
				question.setSiteid(siteid);
				question.setName(getParam(params, "name"));
				question.setSelecttype(getParam(params, "selecttype"));
				question.setIsok(getParam(params, "isok"));
				List<Question> questionList=questionService.find(question, orderSql, page, num, cache);
				int count=questionService.count(question, cache);
				FreemarkerPager pager=new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setAction(getParam(params, "action"));
				loopVars[0]=new ArrayModel(questionList.toArray(),new BeansWrapper()); 
				if(loopVars.length>1){
					loopVars[1]=new BeanModel(pager,new BeansWrapper()); 
				}
				body.render(env.getOut());  
			}
		}
	}

	public QuestionService getQuestionService() {
		return questionService;
	}

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}


}