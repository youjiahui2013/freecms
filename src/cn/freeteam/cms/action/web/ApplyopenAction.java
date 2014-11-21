package cn.freeteam.cms.action.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Applyopen;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ApplyopenService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import freemarker.template.TemplateModelException;
/**
 * 
 * <p>Title: ApplyopenAction.java</p>
 * 
 * <p>Description:依申请公开前台处理 </p>
 * 
 * <p>Date: Mar 21, 2013</p>
 * 
 * <p>Time: 2:58:17 PM</p>
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
public class ApplyopenAction extends BaseAction{

	private ApplyopenService applyopenService;
	private SiteService siteService;
	private String msg;
	private Applyopen applyopen;
	private String siteid;
	private String templetPath;
	public ApplyopenAction() {
		init("applyopenService","siteService");
	}
	public String save() throws TemplateModelException, IOException{
		if (applyopen!=null) {
			Site site=siteService.findById(siteid);
			if (site!=null && site.getIndextemplet()!=null 
					&& site.getIndextemplet().trim().length()>0) {
				applyopen.setAddtime(new Date());
				applyopen.setIp(getHttpRequest().getRemoteAddr());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				applyopen.setQuerycode(sdf.format(new Date())+(int)(Math.random()*1000));
				applyopen.setState("0");
				applyopenService.insert(applyopen);
				msg="感谢您的申请，我们会尽快回复，您可以通过查询码"+applyopen.getQuerycode()+"查询申请信息！";
			}
			//生成静态页面
			Map<String,Object> data=new HashMap<String,Object>();
			setData(data, site);
			data.put("msg", msg);
			templetPath="templet/"+site.getIndextemplet().trim()+"/"+templetPath;
			getHttpResponse().setCharacterEncoding("UTF-8");
			FreeMarkerUtil.createWriter(getServletContext(), data, templetPath, getHttpResponse().getWriter());
		}
		return null;
	}
	public SiteService getSiteService() {
		return siteService;
	}


	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public Applyopen getApplyopen() {
		return applyopen;
	}


	public void setApplyopen(Applyopen applyopen) {
		this.applyopen = applyopen;
	}


	

	public ApplyopenService getApplyopenService() {
		return applyopenService;
	}

	public void setApplyopenService(ApplyopenService applyopenService) {
		this.applyopenService = applyopenService;
	}
	public String getSiteid() {
		return siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public String getTempletPath() {
		return templetPath;
	}
	public void setTempletPath(String templetPath) {
		this.templetPath = templetPath;
	}
}
