package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Answer;
import cn.freeteam.cms.model.Question;
import cn.freeteam.cms.service.AnswerService;
import cn.freeteam.cms.service.QuestionService;
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
 * <p>Title: QuestionOneDirective.java</p>
 * 
 * <p>Description: 提取指定id的网上调查</p>
 * 参数
 * id	网上调查id
 * cache		是否使用缓存，默认为false
 * 
 * 
 * 返回值
 * question		网上调查对象
 * answerList	选项对象列表
 * 
 * 示例
<@questionOne id="03d86aaa-0b64-44a4-a1ff-e154591a8379" ; question,answerList>
${question.name!""}
<table>
<tr><td>选项</td><td>选择次数</td><td>占比</td></tr>
	<#list answerList as answer>
	<tr><td>${answer.name!""}</td><td>${answer.selectnum!0}</td><td>${((answer.selectnum!0)/(question.selectnum!1))?string.percent}</td>
	</tr>
	</#list>
</table>
</@questionOne>
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
public class QuestionOneDirective extends BaseDirective implements TemplateDirectiveModel{

	private QuestionService questionService;
	private AnswerService answerService;
	
	public QuestionOneDirective() {
		init("questionService","answerService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//查询网上调查
				String id=getParam(params, "id");
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				Question question=questionService.findById(id, cache);
				if (question!=null) {
					//设置总选择次数
					question.setSelectnum(answerService.countSelectnum(id, "1", cache));
					loopVars[0]=new BeanModel(question,new BeansWrapper());  
					//查询选项
					List<Answer> answerList=answerService.findByQuestion(id, "1", cache);
					if(loopVars.length>1){
						loopVars[1]=new ArrayModel(answerList.toArray(),new BeansWrapper()); 
					}
				}else {
					loopVars[0]=new BeanModel(new Question(),new BeansWrapper());  
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

	public AnswerService getAnswerService() {
		return answerService;
	}

	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}

}