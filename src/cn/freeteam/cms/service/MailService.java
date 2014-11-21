package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.MailMapper;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.model.MailExample;
import cn.freeteam.cms.model.MailExample.Criteria;

public class MailService extends BaseService{

	private MailMapper mailMapper;
	
	public MailService() {
		initMapper("mailMapper");
	}
	

	/**
	 * 添加
	 * @param mail
	 * @return
	 */
	public String insert(Mail mail){
		mail.setId(UUID.randomUUID().toString());
		mailMapper.insert(mail);
		DBCommit();
		return mail.getId();
	}

	/**
	 * 分页查询
	 */
	public List<Mail> find(Mail mail,String order,int currPage,int pageSize,boolean cache){
		MailExample example=new MailExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(mail, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		if (cache) {
			return mailMapper.selectPageByExampleCache(example);
		}else {
			return mailMapper.selectPageByExample(example);
		}
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Mail findById(String id){
		return mailMapper.selectByPrimaryKey(id);
	}
	/**
	 * 根据querycode查询
	 * @param id
	 * @return
	 */
	public Mail findByQuerycode(String querycode,boolean cache){
		if (cache) {
			return mailMapper.selectByQuerycodeCache(querycode);
		}else {
			return mailMapper.selectByQuerycode(querycode);
		}
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Mail mail,boolean cache){
		MailExample example=new MailExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(mail, criteria);
		if (cache) {
			return mailMapper.countByExampleCache(example);
		}else {
			return mailMapper.countByExample(example);
		}
	}
	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Mail mail,Criteria criteria){
		if (mail!=null ) {
			if ("unit".equals(mail.getType())) {
				//部门信件
				criteria.andSql(" (unitid is not null and unitid != '') ");
			}else if ("user".equals(mail.getType())) {
				//个人信件
				criteria.andSql(" (userid is not null and userid != '') ");
			}else if ("other".equals(mail.getType())) {
				//其他信件
				criteria.andSql(" ((unitid is null or unitid = '') and (userid is null or userid = '')) ");
			}
			if (mail.getQuerycode()!=null && mail.getQuerycode().trim().length()>0) {
				criteria.andQuerycodeLike("%"+mail.getQuerycode().trim()+"%");
			}
			if (mail.getTitle()!=null && mail.getTitle().trim().length()>0) {
				criteria.andTitleLike("%"+mail.getTitle().trim()+"%");
			}
			if (mail.getWriter()!=null && mail.getWriter().trim().length()>0) {
				criteria.andWriterLike("%"+mail.getWriter().trim()+"%");
			}
			if (mail.getMailtype()!=null && mail.getMailtype().trim().length()>0) {
				criteria.andMailtypeEqualTo(mail.getMailtype().trim());
			}
			if (mail.getUserid()!=null && mail.getUserid().trim().length()>0) {
				criteria.andUseridEqualTo(mail.getUserid().trim());
			}
			if (mail.getUnitid()!=null && mail.getUnitid().trim().length()>0) {
				criteria.andUnitidEqualTo(mail.getUnitid().trim());
			}
			if (mail.getUnitids()!=null && mail.getUnitids().trim().length()>0) {
				criteria.andSql(" unitid in ("+mail.getUnitids()+") ");
			}
			if (mail.getState()!=null && mail.getState().trim().length()>0) {
				if ("1".equals(mail.getState())) {
					criteria.andStateEqualTo("1");
				}else if ("0".equals(mail.getState())) {
					criteria.andSql(" (state is null or state='0') ");
				}
			}
			if (mail.getIsopen()!=null && mail.getIsopen().trim().length()>0) {
				if ("1".equals(mail.getIsopen())) {
					criteria.andIsopenEqualTo("1");
				}else if ("0".equals(mail.getIsopen())) {
					criteria.andSql(" (isopen is null or isopen='0') ");
				}
			}
			
		}
	}
	/**
	 * 更新
	 * @param templet
	 */
	public void update(Mail mail){
		mailMapper.updateByPrimaryKeySelective(mail);
		DBCommit();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		mailMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	public MailMapper getMailMapper() {
		return mailMapper;
	}

	public void setMailMapper(MailMapper mailMapper) {
		this.mailMapper = mailMapper;
	}
}
