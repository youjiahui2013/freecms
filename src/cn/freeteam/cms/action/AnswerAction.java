package cn.freeteam.cms.action;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Answer;
import cn.freeteam.cms.service.AnswerService;
import cn.freeteam.cms.service.SensitiveService;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;

/**
 * 
 * <p>Title: AnswerAction.java</p>
 * 
 * <p>Description: 网上调查选项</p>
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
public class AnswerAction extends BaseAction{

	private AnswerService answerService;
	private SensitiveService sensitiveService;
	private String result;
	private String msg;
	private Answer answer;
	public AnswerAction() {
		init("answerService");
	}

	/**
	 * 添加
	 * @return
	 */
	public String add(){
		String id="";
		try {
			//敏感词处理
			init("sensitiveService");
			answer.setName(sensitiveService.replace(answer.getName()));
			result="0";
			msg="添加调查选项 "+answer.getName()+" ";
			answer.setSelectnum(0);
			answerService.add(answer);
			result="1";
			msg+="成功";
			id="<属性>"+answer.getId();
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg+id);
		return null;
	}
	/**
	 * 更新
	 * @return
	 */
	public String update(){
		try {
			//敏感词处理
			init("sensitiveService");
			answer.setName(sensitiveService.replace(answer.getName()));
			result="0";
			msg="修改调查选项 "+answer.getName()+" ";
			//获取老数据
			Answer old=answerService.findById(answer.getId(),false);
			if (old!=null) {
				old.setIsok(answer.getIsok());
				old.setIsselect(answer.getIsselect());
				old.setName(answer.getName());
				old.setOrdernum(answer.getOrdernum());
				answerService.update(old);
				result="1";
				msg+="成功";
			}else {
				msg="此调查选项不存在!";
				ResponseUtil.writeUTF(getHttpResponse(),result+msg);
				return null;
			}
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg);
		return null;
	}
	/**
	 * 删除
	 * @return
	 */
	public String del(){
		String id="";
		try {
			result="0";
			msg="删除调查选项 "+answer.getName()+" ";
			answerService.del(answer.getId());
			result="1";
			msg+="成功";
			id="<属性>"+answer.getId();
		} catch (Exception e) {
			DBProException(e);
			result="0";
			msg+="失败:"+e.toString();
		}
		OperLogUtil.log(getLoginName(), msg, getHttpRequest());
		ResponseUtil.writeUTF(getHttpResponse(),result+msg+id);
		return null;
	}
	public AnswerService getAnswerService() {
		return answerService;
	}

	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}
	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public Answer getAnswer() {
		return answer;
	}


	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public SensitiveService getSensitiveService() {
		return sensitiveService;
	}

	public void setSensitiveService(SensitiveService sensitiveService) {
		this.sensitiveService = sensitiveService;
	}


}
