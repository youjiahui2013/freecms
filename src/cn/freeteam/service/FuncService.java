package cn.freeteam.service;

import java.util.ArrayList;
import java.util.List;


import cn.freeteam.base.BaseService;
import cn.freeteam.dao.FuncMapper;
import cn.freeteam.model.Func;
import cn.freeteam.model.FuncExample;
import cn.freeteam.model.FuncExample.Criteria;


/**
 * 
 * <p>Title: FuncService.java</p>
 * 
 * <p>Description: 功能菜单相关服务</p>
 * 
 * <p>Date: Dec 16, 2011</p>
 * 
 * <p>Time: 4:46:30 PM</p>
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
public class FuncService extends BaseService{

	private FuncMapper funcMapper;
	private Func func;
	
	public FuncService(){
		initMapper("funcMapper");
	}
	/**
	 * 判断是否有子菜单
	 * @param parId
	 * @return
	 */
	public boolean haveSon(String parId){
		FuncExample example=new FuncExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		return funcMapper.countByExample(example)>0;
	}
	/**
	 * 查询根菜单
	 * @return
	 */
	public List<Func> selectRoot(){
		return funcMapper.selectRoot();
	}
	/**
	 * 查询所有
	 * @return
	 */
	public List<Func> selectAll(){
		FuncExample example=new FuncExample();
		example.setOrderByClause(" ordernum ");
		return funcMapper.selectByExample(example);
	}
	/**
	 * 查询所有有权限的
	 * @return
	 */
	public List<Func> selectAllAuth(String userid){
		return funcMapper.selectAllAuth(userid);
	}
	/**
	 * 根据用户查询根菜单
	 * @return
	 */
	public List<Func> selectRootAuth(String userid){
		return funcMapper.selectRootAuth(userid);
	}

	/**
	 * 查询根菜单
	 * @return
	 */
	public List<Func> selectByParid(String par){
		return funcMapper.selectByParid(par);
	}

	/**
	 * 根据查询根菜单
	 * @return
	 */
	public List<Func> selectByParidAuth(String par,String userid){
		FuncExample example=new FuncExample();
		example.setParid(par);
		example.setUserid(userid);
		return funcMapper.selectByParidAuth(example);
	}
	/**
	 * 根据id查询菜单
	 * @return
	 */
	public Func selectById(String id){
		return funcMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param func
	 */
	public void update(Func func){
		funcMapper.updateByPrimaryKey(func);
		DBCommit();
	}
	/**
	 * 更新所属菜单
	 * @param func
	 */
	public void updatePar(Func func){
		funcMapper.updatePar(func);
		DBCommit();
	}
	/**
	 * 添加
	 * @param func
	 */
	public void insert(Func func){
		funcMapper.insert(func);
		DBCommit();
	}
	/**
	 * 递归删除
	 * @param id
	 */
	public void delete(String id){
		//先看是否有子菜单
		List<Func> sons=funcMapper.selectByParid(id);
		if (sons!=null && sons.size()>0) {
			//删除子菜单
			for (int i = 0; i < sons.size(); i++) {
				delete(sons.get(i).getId());
			}
		}
		//删除当前菜单
		funcMapper.deleteByPrimaryKey(id);
		DBCommit();
	}

	/**
	 * 根据菜单id获取所有父级菜单
	 * @param funcId
	 * @param funcList
	 * @return
	 */
	public List<Func> getPars(String funcId,List<Func> funcList){
		if (funcList==null) {
			funcList=new ArrayList<Func>();
		}
		//获取funcId的父级菜单
		func=funcMapper.selectParById(funcId);
		if (func!=null) {
			//添加到list
			funcList.add(func);
			//递归处理
			getPars(func.getId(), funcList);
		}
		return funcList;
	}
	/**
	 * 根据菜单id获取所有子级菜单
	 * @param funcId
	 * @param funcList
	 * @return
	 */
	public List<Func> getSons(String funcId,List<Func> funcList){
		if (funcList==null) {
			funcList=new ArrayList<Func>();
		}
		//获取funcId的子级菜单
		FuncExample example=new FuncExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(funcId);
		List<Func> sonList=funcMapper.selectByExample(example);
		if (sonList!=null && sonList.size()>0) {
			for (int i = 0; i < sonList.size(); i++) {
				//添加到list
				funcList.add(sonList.get(i));
				//递归处理
				getSons(sonList.get(i).getId(), funcList);
			}
		}
		return funcList;
	}
	//setter and getter

	public FuncMapper getFuncMapper() {
		return funcMapper;
	}

	public void setFuncMapper(FuncMapper funcMapper) {
		this.funcMapper = funcMapper;
	}
	public Func getFunc() {
		return func;
	}
	public void setFunc(Func func) {
		this.func = func;
	}
}
