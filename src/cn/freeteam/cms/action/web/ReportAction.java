package cn.freeteam.cms.action.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Report;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ReportService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.util.FileUtil;
import freemarker.template.TemplateModelException;

/**
 * 
 * <p>Title: ReportAction.java</p>
 * 
 * <p>Description:在线申报相关操作 </p>
 * 
 * <p>Date: Mar 17, 2013</p>
 * 
 * <p>Time: 7:58:07 PM</p>
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
public class ReportAction extends BaseAction{

	private ReportService reportService;
	private SiteService siteService;
	private Report report;
	private String validatecode;
	private String msg;
	private String siteid;
	private String templetPath;
	private File attch;
	private String attchFileName;
	
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
	public ReportAction() {
		init("reportService","siteService");
	}
	public String save() throws TemplateModelException, IOException{
		if (report!=null) {
			Site site=siteService.findById(siteid);
			if (site!=null && site.getIndextemplet()!=null 
					&& site.getIndextemplet().trim().length()>0) {
			    HttpSession session =getHttpSession();
				if (validatecode==null || !validatecode.equals(session.getAttribute("rand"))) {
					msg="验证码错误!";
				}else {
					boolean issave=true;
					report.setAddtime(new Date());
					report.setIp(getHttpRequest().getRemoteAddr());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
					report.setQuerycode(sdf.format(new Date())+(int)(Math.random()*1000));
					report.setState("0");
					if (attch!=null) {
						//生成目标文件
						String root=getHttpRequest().getRealPath("/");
						String ext=FileUtil.getExt(attchFileName).toLowerCase();
						if (getConfigVal("attchType").indexOf(ext.replace(".", ""))<0) {
							msg="<script>alert('只能上传"+getConfigVal("attchType")+"格式的附件!');history.back();</script>";
							issave=false;
						}else {
							String id=UUID.randomUUID().toString();
							File targetFile=new File(root+"/upload/"+site.getId()+"/"+id+ext);
							File folder=new File(root+"/upload/"+site.getId()+"/");
							if (!folder.exists()) {
								folder.mkdirs();
							}
							if (!targetFile.exists()) {
								targetFile.createNewFile();
							}
							//复制到目标文件
							FileUtil.copy(attch, targetFile);

							//生成访问地址
							report.setAttch("/upload/"+site.getId()+"/"+id+ext);
						}
					}
					if (issave) {
						reportService.insert(report);
						msg="感谢您的申报，我们会尽快回复，您可以通过查询码"+report.getQuerycode()+"查询申报信息！";
					}
				}
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				setData(data, site);
				data.put("msg", msg);
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+templetPath;
				getHttpResponse().setCharacterEncoding("UTF-8");
				FreeMarkerUtil.createWriter(getServletContext(), data, templetPath, getHttpResponse().getWriter());
			}
		}
		return null;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	public String getValidatecode() {
		return validatecode;
	}
	public void setValidatecode(String validatecode) {
		this.validatecode = validatecode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public File getAttch() {
		return attch;
	}
	public void setAttch(File attch) {
		this.attch = attch;
	}
	public String getAttchFileName() {
		return attchFileName;
	}
	public void setAttchFileName(String attchFileName) {
		this.attchFileName = attchFileName;
	}
}
