package cn.freeteam.action;

import java.util.Enumeration;
import java.util.List;

import cn.freeteam.base.BaseAction;
import cn.freeteam.model.Config;
import cn.freeteam.service.ConfigService;
/**
 * 
 * <p>Title: ConfigAction.java</p>
 * 
 * <p>Description: 系统配置相关操作</p>
 * 
 * <p>Date: Jan 14, 2013</p>
 * 
 * <p>Time: 8:26:09 PM</p>
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
public class ConfigAction extends BaseAction{

	private ConfigService configService;
	
	private List<Config> configList;
	private String msg;
	private String pageFuncId;
	public ConfigAction() {
		init("configService");
	}
	/**
	 * 配置页面
	 * @return
	 */
	public String config(){
		//提取所有系统配置项目
		configList=configService.find();
		return "config";
	}
	/**
	 * 配置处理
	 * @return
	 */
	public String configDo(){
		Enumeration<String> paramNames = getHttpRequest().getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			//更新配置项
			configService.update(paramName, getHttpRequest().getParameter(paramName));
		}
		//更新application中的配置
		setConfig();
		msg="<script>alert('操作成功');location.href='config_config.do?pageFuncId="+pageFuncId+"';</script>";
		return "msg";
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}
	public List<Config> getConfigList() {
		return configList;
	}
	public void setConfigList(List<Config> configList) {
		this.configList = configList;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getPageFuncId() {
		return pageFuncId;
	}
	public void setPageFuncId(String pageFuncId) {
		this.pageFuncId = pageFuncId;
	}
}
