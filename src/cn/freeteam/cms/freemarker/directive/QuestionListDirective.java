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
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: QuestionListDirective.java</p>
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
 * nameLen		名称显示长度
 * siteid		站点id
 * 
 * 
 * 返回值
 * question			网上调查对象
 * index			索引
 * 
 * 示例
<@questionList ;question,index>
${index} : ${question.name}<br>
</@questionList>
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
public class QuestionListDirective extends BaseDirective implements TemplateDirectiveModel{

	private QuestionService questionService;
	
	public QuestionListDirective() {
		init("questionService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//查询网上调查
				String id=getParam(params, "id");
				String siteid=getParam(params, "siteid");
				String order=getParam(params, "order");
				String orderSql=" addtime desc ";
				//标题长度
				int nameLen=getParamInt(params, "nameLen",0);
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
				List<Question> questionList=questionService.find(question, orderSql, 1, getParamInt(params, "num", 1), cache);
				if (questionList!=null && questionList.size()>0) {
					for (int i = 0; i < questionList.size(); i++) {
						if (nameLen>0 && questionList.get(i).getName().length()>nameLen) {
							questionList.get(i).setName(questionList.get(i).getName().substring(0, nameLen));
						}
						loopVars[0]=new BeanModel(questionList.get(i),new BeansWrapper());  
						if(loopVars.length>1){
							loopVars[1]=new SimpleNumber(i);
						}
						body.render(env.getOut());  
					}
				}
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