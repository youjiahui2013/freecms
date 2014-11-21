package cn.freeteam.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.InfoSignMapper;
import cn.freeteam.cms.model.InfoSign;
import cn.freeteam.cms.model.InfoSignExample;
import cn.freeteam.cms.model.InfoSignExample.Criteria;
/**
 * 
 * <p>Title: InfoSignService.java</p>
 * 
 * <p>Description: 信息签收服务</p>
 * 
 * <p>Date: Jan 15, 2013</p>
 * 
 * <p>Time: 7:13:12 PM</p>
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
public class InfoSignService extends BaseService{

	private InfoSignMapper infoSignMapper;
	
	public InfoSignService() {
		initMapper("infoSignMapper");
	}
	/**
	 * 用户签收信息改变
	 * @param infoid
	 * @param signusers
	 */
	public void infoedit(String infoid,String[] signusers){
		if (signusers!=null && signusers.length>0) {
			//检查那些删除了，并删除
			InfoSignExample example=new InfoSignExample();
			Criteria criteria=example.createCriteria();
			criteria.andInfoidEqualTo(infoid);
			List<String> userList=new ArrayList<String>();
			for (int i = 0; i < signusers.length; i++) {
				userList.add(signusers[i]);
			}
			criteria.andUseridNotIn(userList);
			infoSignMapper.deleteByExample(example);
			DBCommit();
			//检查那些新增加，并增加
			for (int i = 0; i < signusers.length; i++) {
				if (countByInfoUser(infoid, signusers[i])==0) {
					InfoSign infoSign=new InfoSign();
					infoSign.setInfoid(infoid);
					infoSign.setUserid(signusers[i]);
					save(infoSign);
				}
			}
		}
	}
	/**
	 * 根据用户id和信息id统计
	 * @param infoid
	 * @param userid
	 * @return
	 */
	public int countByInfoUser(String infoid,String userid){
		InfoSignExample example=new InfoSignExample();
		Criteria criteria=example.createCriteria();
		criteria.andInfoidEqualTo(infoid);
		criteria.andUseridEqualTo(userid);
		return infoSignMapper.countByExample(example);
	}
	/**
	 * 保存
	 * @param infoSign
	 * @return
	 */
	public String  save(InfoSign infoSign){
		if (infoSign!=null) {
			infoSign.setId(UUID.randomUUID().toString());
			infoSignMapper.insert(infoSign);
			DBCommit();
			return infoSign.getId();
		}
		return "";
	}
	/**
	 * 根据信息id查询
	 * @param infoid
	 * @return
	 */
	public List<InfoSign> findByInfo(String infoid){
		InfoSignExample example=new InfoSignExample();
		Criteria criteria=example.createCriteria();
		criteria.andInfoidEqualTo(infoid);
		return infoSignMapper.selectByExample(example);
	}
	/**
	 * 根据信息id查询签收情况
	 * @param infoid
	 * @return
	 */
	public List<InfoSign> findSignByInfo(String infoid){
		InfoSignExample example=new InfoSignExample();
		Criteria criteria=example.createCriteria();
		criteria.andInfoidEqualTo(infoid);
		example.setOrderByClause(" users.loginname ");
		return infoSignMapper.selectSignByExample(example);
	}
	/**
	 * 判断指定信息是否有指定签收用户
	 * @param userid
	 * @param infoid
	 * @return
	 */
	public InfoSign findByUserInfo(String userid,String infoid){
		InfoSignExample example=new InfoSignExample();
		Criteria criteria=example.createCriteria();
		criteria.andInfoidEqualTo(infoid);
		criteria.andUseridEqualTo(userid);
		List<InfoSign> list=infoSignMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 编辑
	 * @param infoid
	 * @param userid
	 * @param request
	 */
	public void update(InfoSign infoSign){
		if (infoSign!=null) {
			infoSignMapper.updateByPrimaryKey(infoSign);
			DBCommit();
		}
	}
	public InfoSignMapper getInfoSignMapper() {
		return infoSignMapper;
	}

	public void setInfoSignMapper(InfoSignMapper infoSignMapper) {
		this.infoSignMapper = infoSignMapper;
	}
}
