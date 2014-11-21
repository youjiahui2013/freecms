package cn.freeteam.cms.action;

import java.util.Date;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Question;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.service.QuestionService;
import cn.freeteam.cms.service.SensitiveService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: QuestionAction.java</p>
 * 
 * <p>Description: 网上调查</p>
 * 
 * <p>Date: Jan 22, 2013</p>
 * 
 * <p>Time: 8:58:23 PM</p>
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
	private String msg;
	private String pageFuncId;
	private SensitiveService sensitiveService;
	
	private QuestionService questionService;
	private List<Question> questionList;
	private Question question;
	private String order=" addtime desc";
	private String logContent;
	private String ids;
	
	public QuestionAction() {
		init("questionService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (question==null ){
			question=new Question();
		}
		if (!isAdminLogin() && !isSiteAdmin()) {
			question.setAdduser(getLoginAdmin().getId());
		}
		question.setSiteid(getManageSite().getId());
		questionList=questionService.find(question, order, currPage, pageSize,false);
		totalCount=questionService.count(question,false);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("question.name");
		pager.appendParam("question.selecttype");
		pager.appendParam("question.isok");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("question_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (question!=null && question.getId()!=null && question.getId().trim().length()>0) {
			question=questionService.findById(question.getId(),false);
		}
		return "edit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		String oper="添加";
		try {
			//敏感词处理
			init("sensitiveService");
			question.setName(sensitiveService.replace(question.getName()));
			question.setDetail(sensitiveService.replace(question.getDetail()));
			if (question!=null && question.getId()!=null) {
				Question  oldQuestion=questionService.findById(question.getId(),false);
				if (oldQuestion!=null) {
					oldQuestion.setName(question.getName());
					oldQuestion.setSelecttype(question.getSelecttype());
					oldQuestion.setIsok(question.getIsok());
					oldQuestion.setDetail(question.getDetail());
					oper="修改";
					questionService.update(oldQuestion);
				}
			}else {
				//添加
				question.setAdduser(getLoginAdmin().getId());
				question.setAddtime(new Date());
				question.setAdduser(getLoginAdmin().getId());
				question.setSiteid(getManageSite().getId());
				questionService.add(question);
			}
			logContent=oper+"网上调查("+question.getName()+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"网上调查("+question.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}

	/**
	 * 删除
	 * @return
	 */
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							question=questionService.findById(idArr[i],false);
							if (question!=null) {
								questionService.del(question.getId());
								sb.append(idArr[i]+";");
								logContent="删除网上调查("+question.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除网上调查("+question.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPageFuncId() {
		return pageFuncId;
	}

	public void setPageFuncId(String pageFuncId) {
		this.pageFuncId = pageFuncId;
	}

	public QuestionService getQuestionService() {
		return questionService;
	}

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}

	public List<Question> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<Question> questionList) {
		this.questionList = questionList;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public SensitiveService getSensitiveService() {
		return sensitiveService;
	}

	public void setSensitiveService(SensitiveService sensitiveService) {
		this.sensitiveService = sensitiveService;
	}

}
