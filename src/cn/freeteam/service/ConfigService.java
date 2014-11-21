package cn.freeteam.service;

import java.util.List;

import cn.freeteam.base.BaseService;
import cn.freeteam.dao.ConfigMapper;
import cn.freeteam.model.Config;
import cn.freeteam.model.ConfigExample;
import cn.freeteam.model.ConfigExample.Criteria;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
/**
 * 
 * <p>Title: ConfigService.java</p>
 * 
 * <p>Description: 系统配置服务类</p>
 * 
 * <p>Date: Jan 14, 2013</p>
 * 
 * <p>Time: 8:27:11 PM</p>
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
public class ConfigService extends BaseService{

	private ConfigMapper configMapper;
	
	public ConfigService() {
		initMapper("configMapper");
	}
	/**
	 * 查询所有系统配置项目
	 * @return
	 */
	public List<Config> find(){
		ConfigExample example=new ConfigExample();
		example.setOrderByClause(" orderNum ");
		return configMapper.selectByExample(example);
	}
	/**
	 * 查询指定编码配置
	 * @return
	 */
	public Config findByCode(String code){
		ConfigExample example=new ConfigExample();
		Criteria criteria=example.createCriteria();
		criteria.andCodeEqualTo(code);
		List<Config> list=configMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 查询指定编码配置并以分隔符处理数组
	 * @return
	 */
	public String[] findArrayByCode(String code,String split){
		ConfigExample example=new ConfigExample();
		Criteria criteria=example.createCriteria();
		criteria.andCodeEqualTo(code);
		List<Config> list=configMapper.selectByExample(example);
		if (list!=null && list.size()>0) {
			Config config=list.get(0);
			if (config.getConfigvalue()!=null) {
				if (split.length()>0) {
					//有分隔符，以数组形式处理
					return config.getConfigvalue().split(split);
				}
			}
		}
		return null;
	}

	/**
	 * 更新配置项
	 * @param code
	 * @param configvalue
	 */
	public void update(String code,String configvalue){
		Config config=new Config();
		config.setCode(code);
		config.setConfigvalue(configvalue);
		configMapper.updateByCode(config);
		DBCommit();
	}
	public ConfigMapper getConfigMapper() {
		return configMapper;
	}

	public void setConfigMapper(ConfigMapper configMapper) {
		this.configMapper = configMapper;
	}
	
}
