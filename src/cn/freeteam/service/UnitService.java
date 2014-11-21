package cn.freeteam.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.freeteam.base.BaseService;
import cn.freeteam.dao.UnitMapper;
import cn.freeteam.model.Unit;
import cn.freeteam.model.UnitExample;
import cn.freeteam.model.UnitExample.Criteria;
import cn.freeteam.util.MybatisSessionFactory;
import cn.freeteam.util.XMLConfigBuilder;



public class UnitService extends BaseService{


	private UnitMapper mapper;
	public UnitService(){
		initMapper();
	}
	public void initMapper() {
		initMapper("mapper");
	}
	/**
	 * 查询是否有子数据
	 * @param parId
	 * @return
	 */
	public boolean hasChildren(String parId){
		UnitExample example=new UnitExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		return mapper.countByExample(example)>0;
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Unit findById(String id){
		if (id!=null && id.trim().length()>0) {
			return mapper.selectByPrimaryKey(id);
		}
		return null;
	}
	/**
	 * 根据用户查询用户所属有效单位
	 * @param user
	 * @return
	 */
	public List<Unit> findByUser(String user){
		if (user!=null && user.trim().length()>0) {
			UnitExample example=new UnitExample();
			example.setUserId(user);
			initMapper();
			return mapper.findByUser(example);
		}
		return null;
	}
	/**
	 * 查询所有单位，顶级单位和下级单位
	 * @param parid 上级单位id
	 * @return list
	 */
	public List<Unit> selectUnit(@Param("parid")String parid)
	{
		initMapper();
		return mapper.selectUnit(parid);
	}
	/**
	 * 根据查询
	 * @param isok
	 * @return
	 */
	public List<Unit> findByUser(String isok,String user){
		UnitExample example=new UnitExample();
		Criteria criteria=example.createCriteria();
		if (isok!=null && isok.trim().length()>0) {
			criteria.andIsokEqualTo(isok);
		}
		if (user!=null && user.trim().length()>0) {
			criteria.andSql(" id in (select unit from freecms_unit_user where users='"+user+"')");
		}
		initMapper();
		return mapper.selectByExample(example);
	}
	/**
	 * 调用unit_del存储过程
	 * @param roleId
	 */
	public void callUnitDelPro(String unitId){
		try {
			Connection con = MybatisSessionFactory.getSession().getConnection();
			CallableStatement cstmt = con.prepareCall("{call unit_del(?)}");
			cstmt.setString(1, unitId);
			cstmt.execute();
		} catch (Exception e) {
			if (e.getMessage().indexOf("No data")<0) {
				System.out.println("调用unit_del存储过程时出错");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 调用unit_update存储过程
	 * @param roleId
	 */
	public void callUnitUpdatePro(String unitId){
		try {
			Connection con = MybatisSessionFactory.getSession().getConnection();
			CallableStatement cstmt = con.prepareCall("{call unit_update(?)}");
			cstmt.setString(1, unitId);
			cstmt.execute();
		} catch (Exception e) {
			if (e.getMessage().indexOf("No data")<0) {
				System.out.println("调用unit_update存储过程时出错");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 根据父id查询
	 * @param parid
	 * @return
	 */
	public List<Unit> findByPar(String parid,String isok,String ismail,boolean cache){
		UnitExample example=new UnitExample();
		Criteria criteria=example.createCriteria();
		if (parid!=null && parid.trim().length()>0 && !"par".equals(parid)) {
			criteria.andParidEqualTo(parid.trim());
		}
		if ("par".equals(parid)) {
			criteria.andSql(" (parid is null or parid = '') ");
		}
		if (isok!=null && isok.trim().length()>0) {
			criteria.andIsokEqualTo(isok.trim());
		}
		if (ismail!=null && ismail.trim().length()>0) {
			criteria.andIsmailEqualTo(ismail.trim());
		}
		example.setOrderByClause(" orderNum ");
		if (cache) {
			return mapper.selectByExampleCache(example);
		}else {
			return mapper.selectByExample(example);
		}
	}
	public UnitMapper getMapper() {
		return mapper;
	}
	public void setMapper(UnitMapper mapper) {
		this.mapper = mapper;
	}
}
