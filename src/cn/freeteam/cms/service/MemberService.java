package cn.freeteam.cms.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.MemberMapper;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.model.MemberExample;
import cn.freeteam.cms.model.Membergroup;
import cn.freeteam.cms.model.MemberExample.Criteria;
import cn.freeteam.util.MD5;
import cn.freeteam.util.MybatisSessionFactory;

/**
 * 
 * <p>Title: MemberService.java</p>
 * 
 * <p>Description:会员相关服务 </p>
 * 
 * <p>Date: Feb 1, 2013</p>
 * 
 * <p>Time: 7:59:23 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @or freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class MemberService extends BaseService{

	private MemberMapper memberMapper;
	
	private MembergroupService membergroupService;
	private CreditruleService creditruleService;
	
	public MemberService() {
		initMapper("memberMapper");
	}

	/**
	 * 分页查询
	 */
	public List<Member> find(Member member,String order,int currPage,int pageSize){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(member, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return memberMapper.selectPageByExample(example);
	}
	/**
	 * 查询
	 */
	public List<Member> find(Member member,String order){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(member, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return memberMapper.selectByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Member member){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(member, criteria);
		return memberMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Member member,Criteria criteria){
		if (member!=null ) {
			if (member.getLoginname()!=null && member.getLoginname().trim().length()>0) {
				criteria.andLoginnameLike("%"+member.getLoginname().trim()+"%");
			}
			if (member.getName()!=null && member.getName().trim().length()>0) {
				criteria.andMNameLike("%"+member.getName().trim()+"%");
			}
			if (member.getGroupid()!=null && member.getGroupid().trim().length()>0) {
				criteria.andGroupidEqualTo(member.getGroupid().trim());
			}
			if (member.getGroupids()!=null && member.getGroupids().size()>0) {
				criteria.andGroupidIn(member.getGroupids());
			}
			if (member.getIsok()!=null && member.getIsok().trim().length()>0) {
				criteria.andIsokEqualTo(member.getIsok().trim());
			}
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Member findById(String id){
		return memberMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Member member){
		memberMapper.updateByPrimaryKeySelective(member);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Member member){
		member.setId(UUID.randomUUID().toString());
		memberMapper.insert(member);
		DBCommit();
		return member.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		memberMapper.deleteByPrimaryKey(id);
		DBCommit();
	}

	/**
	 * 判断会员是否存在
	 * @param member
	 * @return
	 */
	public boolean have(Member member){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		criteria.andLoginnameEqualTo(member.getLoginname().trim());
		return memberMapper.countByExample(example)>0;
	}
	/**
	 * 按登录名查询
	 * @param member
	 * @return
	 */
	public Member findByLoginname(Member member){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		criteria.andLoginnameEqualTo(member.getLoginname().trim());
		List<Member> list = memberMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 按登录名查询
	 * @param member
	 * @return
	 */
	public Member findByLoginname(String loginname){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		criteria.andLoginnameEqualTo(loginname.trim());
		List<Member> list = memberMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 启用/禁用
	 * @param id
	 * @param isok
	 */
	public void isok(String id,String isok){
		Member member=new Member();
		member.setId(id);
		member.setIsok(isok);
		memberMapper.updateByPrimaryKeySelective(member);
		DBCommit();
	}

	/**
	 * 检查登录信息是否正确
	 * @param loginname
	 * @param pwd
	 * @return
	 */
	public String checkLogin(HttpSession session,Member member){
		MemberExample example=new MemberExample();
		Criteria criteria=example.createCriteria();
		criteria.andLoginnameEqualTo(member.getLoginname());
		criteria.andPwdEqualTo(MD5.MD5(member.getPwd()));
		List list=memberMapper.selectByExample(example);
		String msg="";
		if (list!=null && list.size()>0) {
			member=(Member)list.get(0);
			//是否为无效
			if ("1".equals(member.getIsok())) {
				//修改上次登录时间
				member.setLastlogintime(new Date());
				//如果是经验会员则处理所属会员组
				if (0==member.getGrouptype()) {
					if (member.getExperience()!=null) {
						init("membergroupService");
						Membergroup membergroup=membergroupService.findByExperience(member.getExperience());
						if (membergroup!=null) {
							member.setGroupid(membergroup.getId());
						}else {
							member.setGroupid("");
						}
					}else {
						member.setGroupid("");
					}
				}
				//处理积分经验
				init("creditruleService");
				member=creditruleService.credit(member, "login");
				update(member);
				session.setAttribute("loginMember", member);
				if (member.getGroupid()!=null && member.getGroupid().trim().length()>0) {
					session.setAttribute("loginMembergroup", membergroupService.findById(member.getGroupid()));
				}
			}else{
				msg="此用户已禁用!";
			}
		}else{
			msg="用户名或密码错误!";
		}
		return msg;
	}
	public MemberMapper getMemberMapper() {
		return memberMapper;
	}

	public void setMemberMapper(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}

	public MembergroupService getMembergroupService() {
		return membergroupService;
	}

	public void setMembergroupService(MembergroupService membergroupService) {
		this.membergroupService = membergroupService;
	}

	public CreditruleService getCreditruleService() {
		return creditruleService;
	}

	public void setCreditruleService(CreditruleService creditruleService) {
		this.creditruleService = creditruleService;
	}
}
