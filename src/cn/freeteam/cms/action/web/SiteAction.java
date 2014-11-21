package cn.freeteam.cms.action.web;

import java.io.IOException;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.SiteService;
import freemarker.template.TemplateException;



/**
 * 
 * <p>Title: SiteAction.java</p>
 * 
 * <p>Description:关于站点的相关操作 </p>
 * 
 * <p>Date: Jan 21, 2012</p>
 * 
 * <p>Time: 2:30:58 PM</p>
 * 
 * <p>Copyright: 2012</p>
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
public class SiteAction extends BaseAction{

	private SiteService siteService;
	private Site site;
	public SiteAction(){
		init("siteService");
	}
	/**
	 * 站点预览
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public String preview() {
		try {
			if (site.getId()!=null && site.getId().trim().length()>0){
				site=siteService.findById(site.getId());
				//生成首页
				siteService.html(site.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
				getHttpResponse().sendRedirect("/site/"+site.getSourcepath()+"/index.html");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showMessage="预览站点失败:"+e.getMessage();
			return showMessage(showMessage, forwardUrl, forwardSeconds);
		}
		return null;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
}
