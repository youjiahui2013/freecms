package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.CommentMapper;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.CommentExample;
import cn.freeteam.cms.model.CommentExample.Criteria;

/**
 * 
 * <p>Title: CommentService.java</p>
 * 
 * <p>Description: 评论服务</p>
 * 
 * <p>Date: Feb 6, 2013</p>
 * 
 * <p>Time: 1:45:17 PM</p>
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
public class CommentService extends BaseService{

	private CommentMapper commentMapper;
	
	public CommentService() {
		initMapper("commentMapper");
	}

	/**
	 * 分页查询
	 */
	public List<Comment> find(Comment Comment,String order,int currPage,int pageSize,boolean iscache){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Comment, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		if (iscache) {
			return commentMapper.selectPageByExampleCache(example);
		}
		return commentMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 */
	public List<Comment> find(Comment Comment,String order){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Comment, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return commentMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Comment Comment,boolean iscache){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Comment, criteria);
		if (iscache) {
			return commentMapper.countByExampleCache(example);
		}
		return commentMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Comment Comment,Criteria criteria){
		if (Comment!=null ) {
			if (Comment.getMemberid()!=null && Comment.getMemberid().trim().length()>0) {
				criteria.andMemberidEqualTo(Comment.getMemberid().trim());
			}
			if (Comment.getMemberid()!=null && Comment.getMemberid().trim().length()>0) {
				criteria.andMemberidEqualTo(Comment.getMemberid().trim());
			}
			if (Comment.getSiteid()!=null && Comment.getSiteid().trim().length()>0) {
				criteria.andSiteidLike("%"+Comment.getSiteid().trim()+"%");
			}
			if (Comment.getIsanonymous()!=null && Comment.getIsanonymous().trim().length()>0) {
				criteria.andIsanonymousEqualTo(Comment.getIsanonymous().trim());
			}
			if (Comment.getObjtype()!=null && Comment.getObjtype().trim().length()>0) {
				criteria.andObjtypeEqualTo(Comment.getObjtype().trim());
			}
			if (Comment.getObjid()!=null && Comment.getObjid().trim().length()>0) {
				criteria.andObjidLike("%"+Comment.getObjid().trim()+"%");
			}
			if (Comment.getState()!=null && Comment.getState().trim().length()>0) {
				criteria.andStateEqualTo(Comment.getState().trim());
			}
			if (Comment.getContent()!=null && Comment.getContent().trim().length()>0) {
				criteria.andContentLike("%"+Comment.getContent().trim()+"%");
			}

			if (Comment.getStarttime()!=null) {
				criteria.andAddtimeGreaterThanOrEqualTo(Comment.getStarttime());
			}
			if (Comment.getEndtime()!=null) {
				criteria.andAddtimeLessThanOrEqualTo(Comment.getEndtime());
			}
			if (StringUtils.isNotEmpty(Comment.getSitename())) {
				criteria.andSitenameLike("%"+Comment.getSitename()+"%");
			}
			if (StringUtils.isNotEmpty(Comment.getCommentstate())) {
				criteria.andCommentstateEqualTo(Comment.getCommentstate());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Comment findById(String id){
		return commentMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Comment Comment){
		commentMapper.updateByPrimaryKeySelective(Comment);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Comment Comment){
		Comment.setId(UUID.randomUUID().toString());
		commentMapper.insert(Comment);
		DBCommit();
		return Comment.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		commentMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	
	
	
	

	public void state(String id){
		Comment comment=new Comment();
		comment.setId(id);
		comment.setState("1");
		commentMapper.updateByPrimaryKeySelective(comment);
		DBCommit();
	}
	
	
	
	


	/**
	 * 评论频率统计 年 
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateYear(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateYear(example);
	}
	/**
	 * 评论频率统计 年 
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateYear(Comment comment,int currPage,int pageSize){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return commentMapper.commentUpdateYearPage(example);
	}
	/**
	 * 评论频率统计 年
	 * @param comment
	 * @return
	 */
	public int commentUpdateYearCount(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateYearCount(example);
	}

	/**
	 * 评论频率合计 年
	 * @param comment
	 * @return
	 */
	public int commentUpdateYearSum(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateYearSum(example);
	}
	
	

	/**
	 * 评论频率统计 月
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateMonth(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateMonth(example);
	}
	/**
	 * 评论频率统计 月 
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateMonth(Comment comment,int currPage,int pageSize){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return commentMapper.commentUpdateMonthPage(example);
	}
	/**
	 * 评论频率统计 月
	 * @param comment
	 * @return
	 */
	public int commentUpdateMonthCount(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateMonthCount(example);
	}
	/**
	 * 评论频率合计 月
	 * @param comment
	 * @return
	 */
	public int commentUpdateMonthSum(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateMonthSum(example);
	}
	

	/**
	 * 评论频率统计 日
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateDay(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateDay(example);
	}
	/**
	 * 评论频率统计 日 
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateDay(Comment comment,int currPage,int pageSize){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return commentMapper.commentUpdateDayPage(example);
	}
	/**
	 * 评论频率统计 日
	 * @param comment
	 * @return
	 */
	public int commentUpdateDayCount(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateDayCount(example);
	}
	/**
	 * 评论频率合计 日
	 * @param comment
	 * @return
	 */
	public int commentUpdateDaySum(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateDaySum(example);
	}

	/**
	 * 评论频率合计 周
	 * @param comment
	 * @return
	 */
	public int commentUpdateWeekSum(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateWeekSum(example);
	}

	/**
	 * 评论频率统计 周
	 * @param comment
	 * @return
	 */
	public List<Comment> commentUpdateWeek(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.commentUpdateWeek(example);
	}

	/**
	 * 站点评论统计 
	 * @param info
	 * @return
	 */
	public List<Comment> sysSiteComment(Comment comment,int currPage,int pageSize){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return commentMapper.sysSiteCommentPage(example);
	}
	/**
	 * 站点评论统计 
	 * @param comment
	 * @return
	 */
	public List<Comment> sysSiteComment(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.sysSiteComment(example);
	}
	/**
	 * 站点评论统计 
	 * @param comment
	 * @return
	 */
	public int sysSiteCommentCount(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.sysSiteCommentCount(example);
	}
	/**
	 * 站点评论合计 
	 * @param comment
	 * @return
	 */
	public int sysSiteCommentSum(Comment comment){
		CommentExample example=new CommentExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(comment, criteria);
		return commentMapper.sysSiteCommentSum(example);
	}
	
	public CommentMapper getCommentMapper() {
		return commentMapper;
	}

	public void setCommentMapper(CommentMapper commentMapper) {
		this.commentMapper = commentMapper;
	}
}
