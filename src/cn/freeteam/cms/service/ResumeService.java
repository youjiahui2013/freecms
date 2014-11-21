package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.ResumeMapper;
import cn.freeteam.cms.model.Resume;
import cn.freeteam.cms.model.ResumeExample;
import cn.freeteam.cms.model.ResumeExample.Criteria;
/**
 * 
 * <p>Title: ResumeService.java</p>
 * 
 * <p>Description: 简历相关服务</p>
 * 
 * <p>Date: Feb 4, 2013</p>
 * 
 * <p>Time: 7:50:54 PM</p>
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
public class ResumeService extends BaseService{

	private ResumeMapper resumeMapper;
	
	public ResumeService() {
		initMapper("resumeMapper");
	}
	

	/**
	 * 分页查询
	 */
	public List<Resume> find(Resume resume,String order,int currPage,int pageSize){
		ResumeExample example=new ResumeExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(resume, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return resumeMapper.selectPageByExample(example);
	}
	/**
	 * 统计
	 * @param resume
	 * @return
	 */
	public int count(Resume resume){
		ResumeExample example=new ResumeExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(resume, criteria);
		return resumeMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param resume
	 * @param criteria
	 */
	public void proSearchParam(Resume resume,Criteria criteria){
		if (resume!=null ) {
			if (StringUtils.isNotEmpty(resume.getSiteid())) {
				criteria.andSiteidEqualTo(resume.getSiteid());
			}
			if (StringUtils.isNotEmpty(resume.getJob())) {
				criteria.andJobLike("%"+resume.getJob()+"%");
			}
			if (StringUtils.isNotEmpty(resume.getMembername())) {
				criteria.andMembernameLike("%"+resume.getMembername()+"%");
			}
			if (StringUtils.isNotEmpty(resume.getReusername())) {
				criteria.andReusernameLike("%"+resume.getReusername()+"%");
			}
			if (StringUtils.isNotEmpty(resume.getName())) {
				criteria.andNameLike("%"+resume.getName()+"%");
			}
			if (StringUtils.isNotEmpty(resume.getMemberid())) {
				criteria.andMemberidEqualTo(resume.getMemberid());
			}
			if (StringUtils.isNotEmpty(resume.getState())) {
				criteria.andStateEqualTo(resume.getState());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Resume findById(String id){
		return resumeMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Resume resume){
		resumeMapper.updateByPrimaryKeySelective(resume);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Resume resume){
		resume.setId(UUID.randomUUID().toString());
		resumeMapper.insert(resume);
		DBCommit();
		return resume.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		resumeMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	

	public ResumeMapper getResumeMapper() {
		return resumeMapper;
	}

	public void setResumeMapper(ResumeMapper resumeMapper) {
		this.resumeMapper = resumeMapper;
	}
}
