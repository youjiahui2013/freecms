package cn.freeteam.cms.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.omg.PortableServer.POA;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.CreditruleMapper;
import cn.freeteam.cms.model.Creditlog;
import cn.freeteam.cms.model.Creditrule;
import cn.freeteam.cms.model.CreditruleExample;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.model.CreditruleExample.Criteria;
import cn.freeteam.util.DateUtil;
/**
 * 
 * <p>Title: CreditruleService.java</p>
 * 
 * <p>Description: 积分规则相关服务</p>
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
public class CreditruleService extends BaseService{

	private CreditruleMapper creditruleMapper;
	
	private MemberService memberService;
	private CreditlogService creditlogService;
	
	public CreditruleService() {
		initMapper("creditruleMapper");
	}

	/**
	 * 分页查询
	 */
	public List<Creditrule> find(Creditrule Creditrule,String order,int currPage,int pageSize){
		CreditruleExample example=new CreditruleExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Creditrule, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return creditruleMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 */
	public List<Creditrule> find(Creditrule Creditrule,String order,boolean cache){
		CreditruleExample example=new CreditruleExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Creditrule, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		if (cache) {
			return creditruleMapper.selectByExampleCache(example);
		}
		return creditruleMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Creditrule Creditrule){
		CreditruleExample example=new CreditruleExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Creditrule, criteria);
		return creditruleMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Creditrule Creditrule,Criteria criteria){
		if (Creditrule!=null ) {
			if (Creditrule.getCode()!=null && Creditrule.getCode().trim().length()>0) {
				criteria.andCodeLike("%"+Creditrule.getCode().trim()+"%");
			}
			if (Creditrule.getName()!=null && Creditrule.getName().trim().length()>0) {
				criteria.andNameLike("%"+Creditrule.getName().trim()+"%");
			}
			if (Creditrule.getRewardtype()!=null ) {
				criteria.andRewardtypeEqualTo(Creditrule.getRewardtype());
			}
			if (Creditrule.getCycletype()!=null ) {
				criteria.andCycletypeEqualTo(Creditrule.getCycletype());
			}
			if (Creditrule.getCode()!=null && Creditrule.getCode().trim().length()>0) {
				criteria.andCodeEqualTo(Creditrule.getCode().trim());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Creditrule findById(String id){
		return creditruleMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Creditrule Creditrule){
		creditruleMapper.updateByPrimaryKeySelective(Creditrule);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Creditrule Creditrule){
		Creditrule.setId(UUID.randomUUID().toString());
		creditruleMapper.insert(Creditrule);
		DBCommit();
		return Creditrule.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		creditruleMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	/**
	 * 积分经验处理
	 * @param member
	 * @param rulecode
	 */
	public Member credit(Member member,String rulecode){
		if (member!=null && rulecode!=null && rulecode.trim().length()>0) {
			Creditrule creditrule=findByCode(rulecode);
			if (creditrule!=null) {
				boolean pro=true;
				init("creditlogService");
				Creditlog creditlog=new Creditlog();
				creditlog.setCreditruleid(creditrule.getId());
				creditlog.setMemberid(member.getId());
				//判断是否需要处理
				if (Creditrule.CYCLETYPE_ONCE.equals(creditrule.getCycletype())) {
					//一次性
					//判断是否已经处理过
					if (creditlogService.count(creditlog)>0) {
						pro=false;
					}
				}
				else if (Creditrule.CYCLETYPE_EVERYDAY.equals(creditrule.getCycletype())) {
					//每天一次
					creditlog.setCredittimeToday(new Date());
					//判断是否已经处理过
					if (creditlogService.count(creditlog)>0) {
						pro=false;
					}
					//清空条件 
					creditlog.setCredittimeToday(null);
				}
				else if (Creditrule.CYCLETYPE_INTERVAL.equals(creditrule.getCycletype())) {
					//判断是否已经处理过
					List<Creditlog> list=creditlogService.find(creditlog, " credittime desc ", 1, 1);
					if (list!=null && list.size()>0) {
						Creditlog creditlog2=list.get(0);
						if (creditlog2!=null && 
								( DateUtil.differ(creditlog2.getCredittime(), new Date())/(1000*60)
										<creditrule.getCycletime() )) {
							pro=false;
						}
					}
				}
				//判断是否有最多处理次数限制
				if (!Creditrule.CYCLETYPE_UNLIMIT.equals(creditrule.getCycletype()) 
						&& creditrule.getRewardnum()!=null && creditrule.getRewardnum()>0 
						&& creditlogService.count(creditlog)>=creditrule.getRewardnum()) {
					pro=false;
				}
				if (pro) {
					if (1==creditrule.getRewardtype()) {
						//奖励
						member.setCredit(member.getCredit()+creditrule.getCredit());
						member.setExperience(member.getExperience()+creditrule.getExperience());
					}else {
						//惩罚
						member.setCredit(member.getCredit()-creditrule.getCredit());
						if (member.getCredit()<0) {
							member.setCredit(0);
						}
						member.setExperience(member.getExperience()-creditrule.getExperience());
						if (member.getExperience()<0) {
							member.setExperience(0);
						}
					}
					//更新
					init("memberService");
					memberService.update(member);
					//添加积分记录
					creditlog.setType(creditrule.getRewardtype());
					creditlog.setCredit(creditrule.getCredit());
					creditlog.setExperience(creditrule.getExperience());
					creditlog.setCredittime(new Date());
					creditlog.setMembername(member.getLoginname());
					creditlogService.add(creditlog);
				}
			}
		}
		return member;
	}
	/**
	 * 查询指定编码规则
	 * @param rulecode
	 * @return
	 */
	public Creditrule findByCode(String rulecode){
		Creditrule creditrule=new Creditrule();
		creditrule.setCode(rulecode);
		List<Creditrule> list=find(creditrule, "", true);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	public CreditruleMapper getCreditruleMapper() {
		return creditruleMapper;
	}

	public void setCreditruleMapper(CreditruleMapper creditruleMapper) {
		this.creditruleMapper = creditruleMapper;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public CreditlogService getCreditlogService() {
		return creditlogService;
	}

	public void setCreditlogService(CreditlogService creditlogService) {
		this.creditlogService = creditlogService;
	}
}
