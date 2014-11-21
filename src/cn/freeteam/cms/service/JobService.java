package cn.freeteam.cms.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.JobMapper;
import cn.freeteam.cms.model.Job;
import cn.freeteam.cms.model.JobExample;
import cn.freeteam.cms.model.JobExample.Criteria;

public class JobService extends BaseService{

	private JobMapper jobMapper;
	
	public JobService() {
		initMapper("jobMapper");
	}
	

	/**
	 * 分页查询
	 */
	public List<Job> find(Job job,String order,int currPage,int pageSize,boolean cache){
		JobExample example=new JobExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(job, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		if (cache) {
			return jobMapper.selectPageByExampleCache(example);
		}
		return jobMapper.selectPageByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Job job,boolean cache){
		JobExample example=new JobExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(job, criteria);
		if (cache) {
			return jobMapper.countByExampleCache(example);
		}
		return jobMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Job job,Criteria criteria){
		if (job!=null ) {
			if (StringUtils.isNotEmpty(job.getSiteid())) {
				criteria.andSiteidEqualTo(job.getSiteid());
			}
			if (StringUtils.isNotEmpty(job.getName())) {
				criteria.andNameLike("%"+job.getName().trim()+"%");
			}
			if (StringUtils.isNotEmpty(job.getUnitname())) {
				criteria.andUnitnameLike("%"+job.getUnitname().trim()+"%");
			}
			if (StringUtils.isNotEmpty(job.getAddress())) {
				criteria.andAddressLike("%"+job.getAddress().trim()+"%");
			}
			if ("1".equals(job.getIsend())) {
				criteria.andEndtimeLessThan(new Date());
			}
			if ("0".equals(job.getIsend())) {
				criteria.andEndtimeGreaterThanOrEqualTo(new Date());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Job findById(String id,boolean cache){
		if (cache) {
			return jobMapper.selectByPrimaryKeyCache(id);
		}
		return jobMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Job Job){
		jobMapper.updateByPrimaryKeySelective(Job);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Job job){
		job.setId(UUID.randomUUID().toString());
		jobMapper.insert(job);
		DBCommit();
		return job.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		jobMapper.deleteByPrimaryKey(id);
		DBCommit();
	}

	public JobMapper getJobMapper() {
		return jobMapper;
	}

	public void setJobMapper(JobMapper jobMapper) {
		this.jobMapper = jobMapper;
	}
	
}
