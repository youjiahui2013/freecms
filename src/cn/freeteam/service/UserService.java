package cn.freeteam.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;


import cn.freeteam.base.BaseService;
import cn.freeteam.dao.UsersMapper;
import cn.freeteam.model.Users;
import cn.freeteam.model.UsersExample;
import cn.freeteam.model.UsersExample.Criteria;
import cn.freeteam.util.MD5;
import cn.freeteam.util.MybatisSessionFactory;


/**
 * 
 * <p>Title: UserService.java</p>
 * 
 * <p>Description: 用户服务</p>
 * 
 * <p>Date: Dec 16, 2011</p>
 * 
 * <p>Time: 4:49:39 PM</p>
 * 
 * <p>Copyright: 2011</p>
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
public class UserService extends BaseService{

	private UsersMapper usersMapper;
	
	public UserService(){
		initMapper("usersMapper");
	}
	/**
	 * 是否有此用户
	 * @param user
	 * @return
	 */
	public int have(Users user){
		return usersMapper.have(user);
	}
	
	/**
	 * 根据登录名查询用户对象
	 * freeteam 2011-12-16
	 * @param loginName
	 * @return
	 */
	public Users findByLoginName(String loginName){
		UsersExample example=new UsersExample();
		Criteria criteria=example.createCriteria();
		criteria.andLoginnameEqualTo(loginName);
		List<Users> users=usersMapper.selectByExample(example);
		if (users!=null && users.size()>0) {
			return users.get(0);
		}
		return null;
	}
	/**
	 * 查询所有用户
	 * @return
	 */
	public List<Users> findAll(){
		UsersExample example=new UsersExample();
		example.setOrderByClause(" loginName ");
		return usersMapper.selectByExample(example);
	}
	/**
	 * 根据参数查询
	 * @return
	 */
	public List<Users> find(Users user,boolean cache){
		UsersExample example=new UsersExample();
		Criteria criteria=example.createCriteria();
		if (user.getIsmail()!=null && user.getIsmail().trim().length()>0) {
			criteria.andIsmailEqualTo(user.getIsmail().trim());
		}
		example.setOrderByClause(" loginName ");
		if (cache) {
			return usersMapper.selectByExampleCache(example);
		}else {
			return usersMapper.selectByExample(example);
		}
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Users findById(String id){
		return usersMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 保存
	 * freeteam 2011-12-17
	 * @param users
	 */
	public void save(Users users){
		usersMapper.insert(users);
		DBCommit();
	}
	/**
	 * 保存
	 * freeteam 2011-12-17
	 * @param users
	 */
	public void update(Users users){
		usersMapper.updateByPrimaryKey(users);
		DBCommit();
	}
	/**
	 * 检查登录信息是否正确
	 * @param loginname
	 * @param pwd
	 * @return
	 */
	public String checkLogin(HttpSession session,Users user){
		UsersExample usersExample=new UsersExample();
		Criteria criteria= usersExample.createCriteria();
		criteria.andLoginnameEqualTo(user.getLoginname());
		criteria.andPwdEqualTo(MD5.MD5(user.getPwd()));
		List list=usersMapper.selectByExample(usersExample);
		String msg="";
		if (list!=null && list.size()>0) {
			user=(Users)list.get(0);
			//是否为无效
			if ("1".equals(user.getIsok())) {
				
				//修改上次登录时间
				user.setLastlogintime(user.getLastestlogintime());
				user.setLastestlogintime(new Date());
				usersMapper.updateLastLoginTime(user);
				MybatisSessionFactory.getSession().commit();
				session.setAttribute("loginAdmin", user);
				//设置cdfinder文件目录
				session.setAttribute("currentFolder", "/"+user.getLoginname()+"/");
			}else{
				msg="此用户已禁用!";
			}
		}else{
			msg="用户名或密码错误!";
		}
		return msg;
	}
	//setter and getter

	public UsersMapper getUsersMapper() {
		return usersMapper;
	}

	public void setUsersMapper(UsersMapper usersMapper) {
		this.usersMapper = usersMapper;
	}
}
