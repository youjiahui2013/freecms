package cn.freeteam.cms.action.web;

import javax.servlet.http.Cookie;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Question;
import cn.freeteam.cms.service.AnswerService;
import cn.freeteam.cms.service.QuestionService;
import cn.freeteam.util.EscapeUnescape;

/**
 * 
 * <p>Title: QuestionAction.java</p>
 * 
 * <p>Description: 网上调查</p>
 * 
 * <p>Date: Jan 22, 2013</p>
 * 
 * <p>Time: 8:27:51 PM</p>
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
public class QuestionAction extends BaseAction{

	private AnswerService answerService;
	private QuestionService questionService;
	private String answerval;
	private Question question;
	private String msg;
	public QuestionAction(){
		init("questionService","answerService");
	}
	/**
	 * 投票处理
	 * @return
	 */
	public String answer(){
		//判断是否已投票
		boolean isanswer=true;
		Cookie[] cookies = getHttpRequest().getCookies();
        for(Cookie c :cookies ){
            if (("Question"+question.getId()).equals(c.getName())) {
				msg="您已经投过票了";
				isanswer=false;
			}
        }
        if (isanswer) {
    		if (answerval!=null && answerval.trim().length()>0) {
    			String[] vals=answerval.split(",");
    			if (vals!=null && vals.length>0) {
    				for (int i = 0; i < vals.length; i++) {
    					answerService.selectnum(vals[i]);
    				}
    				msg="投票成功";
    				Cookie cookie=new Cookie("Question"+question.getId(),"true");
    				cookie.setMaxAge(1000*60*60*24*365);//有效时间为一年
    				getHttpResponse().addCookie(cookie);
    			}else {
    				msg="请选择答案";
    			}
    		}else{
    			msg="请选择答案";
    		}
		}
		write(msg, "UTF-8");
		return null;
	}
	
	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}
	public QuestionService getQuestionService() {
		return questionService;
	}
	public String getAnswerval() {
		return answerval;
	}
	public void setAnswerval(String answerval) {
		this.answerval = answerval;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public AnswerService getAnswerService() {
		return answerService;
	}
	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
