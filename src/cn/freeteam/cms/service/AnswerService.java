package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.AnswerMapper;
import cn.freeteam.cms.model.Answer;
import cn.freeteam.cms.model.AnswerExample;
import cn.freeteam.cms.model.AnswerExample.Criteria;

/**
 * 
 * <p>Title: AnswerService.java</p>
 * 
 * <p>Description:网上调查选项 </p>
 * 
 * <p>Date: Jan 22, 2013</p>
 * 
 * <p>Time: 8:07:05 PM</p>
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
public class AnswerService extends BaseService{

	private AnswerMapper answerMapper;
	public AnswerService() {
		initMapper("answerMapper");
	}
	/**
	 * 查询批定网上调查下选项
	 * @param questidid
	 * @return
	 */
	public List<Answer> findByQuestion(String questidid,String isok,boolean cache){
		AnswerExample example=new AnswerExample();
		Criteria criteria=example.createCriteria();
		criteria.andQuestionidEqualTo(questidid);
		if (isok!=null && isok.trim().length()>0) {
			if ("1".equals(isok)) {
				criteria.andIsokEqualTo("1");
			}else if ("0".equals(isok)) {
				criteria.andSql(" (isok is null or isok='0') ");
			}
		}
		example.setOrderByClause(" ordernum ");
		if (cache) {
			return answerMapper.selectByExampleCache(example);
		}else {
			return answerMapper.selectByExample(example);
		}
	}
	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Answer findById(String id,boolean cache){
		if (cache) {
			return answerMapper.selectByPrimaryKeyCache(id);
		}else {
			return answerMapper.selectByPrimaryKey(id);
		}
	}

	/**
	 * 更新
	 * @param answer
	 */
	public void update(Answer answer){
		answerMapper.updateByPrimaryKey(answer);
		DBCommit();
	}
	/**
	 * 选择
	 * @param id
	 */
	public void selectnum(String id){
		answerMapper.selectnum(id);
		DBCommit();
	}
	/**
	 * 添加
	 * @param answer
	 * @return
	 */
	public String add(Answer answer){
		answer.setId(UUID.randomUUID().toString());
		answerMapper.insert(answer);
		DBCommit();
		return answer.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		answerMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	/**
	 * 统计选择次数
	 * @param questidid
	 * @param isok
	 * @param cache
	 * @return
	 */
	public int countSelectnum(String questidid,String isok,boolean cache){
		AnswerExample example=new AnswerExample();
		Criteria criteria=example.createCriteria();
		criteria.andQuestionidEqualTo(questidid);
		if (isok!=null && isok.trim().length()>0) {
			if ("1".equals(isok)) {
				criteria.andIsokEqualTo("1");
			}else if ("0".equals(isok)) {
				criteria.andSql(" (isok is null or isok='0') ");
			}
		}
		example.setOrderByClause(" ordernum ");
		if (cache) {
			return answerMapper.countSelectnumByExample(example);
		}else {
			return answerMapper.countSelectnumByExampleCache(example);
		}
	}
	public AnswerMapper getAnswerMapper() {
		return answerMapper;
	}

	public void setAnswerMapper(AnswerMapper answerMapper) {
		this.answerMapper = answerMapper;
	}
}
