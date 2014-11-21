package cn.freeteam.cms.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.model.RoleSite;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.HtmlquartzService;
import cn.freeteam.cms.service.RoleChannelService;
import cn.freeteam.cms.service.RoleSiteService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.TempletChannelService;
import cn.freeteam.cms.service.TempletLinkService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.model.Roles;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.MybatisSessionFactory;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;
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
	private UserService userService;
	private ChannelService channelService;
	private TempletService templetService;
	private RoleSiteService roleSiteService;
	private RoleChannelService roleChannelService;
	private HtmlquartzService htmlquartzService;
	private TempletLinkService templetLinkService;
	private TempletChannelService templetChannelService;
	
	private List<Site> siteList;
	public List<Channel> channelList;
	
	private String root;
	private String onclick;
	private Site site;
	private String oldSourcepath;
	private File logo;
	private String logoFileName;
	private String oldLogo;
	private String type;
	StringBuffer channelTreeContent;
	private List<Integer> hours;
	private List<Integer> mins;
	
	private String wasUser;
	private String operUser;
	private String msg;
	private String result;
	private Users user;
	private Templet templet;
	private String logContent;
	private String sites;
	private Roles role;
	private Htmlquartz htmlquartz;
	private String manageSiteChecked;
	private String[] channelTree;
	private boolean haveSiteRole;
	private RoleSite roleSite;
	
	
	public String[] getChannelTree() {
		return channelTree;
	}
	public void setChannelTree(String[] channelTree) {
		this.channelTree = channelTree;
	}
	public SiteAction(){
		init("siteService","userService","channelService","templetService",
				"roleSiteService","roleChannelService");
	}
	
	/**
	 * 建站向导
	 * @return
	 */
	public String guide(){
		return "guide";
	}
	/**
	 * 建站向导 保存站点
	 * @return
	 */
	public String guideSite(){
		//添加
		if (siteService.haveSourcePath( site.getSourcepath())) {
			write("<script>alert('此源文件目录已存在');history.back();</script>", "GBK");
			return null;
		}
		//创建源文件目录
		FileUtil.mkdir(getHttpRequest().getRealPath("/")+"site/"+site.getSourcepath());
		siteService.insert(site);
		OperLogUtil.log(getLoginName(), "添加站点 "+site.getName(), getHttpRequest());
		return "guideTemplet";
	}
	/**
	 * 建站向导 选择模板
	 * @return
	 */
	public String guideTemplet(){
		if (site!=null && StringUtils.isNotEmpty(site.getId())) {
			site=siteService.findById(site.getId());
			if (site!=null) {
				if ("0".equals(type)) {
					//选择模板
					site.setIndextemplet(templet.getId());
				}else {
					//创建新模板
					init("templetService");
					templet.setState("1");
					templet.setAdduser(getLoginAdmin().getId());
					site.setIndextemplet(templetService.add(templet));
					String realPath=getHttpRequest().getRealPath("/");
					try {
						FileUtil.copyDirectiory(realPath+"/templet/default", realPath+"/templet/"+templet.getId());
					} catch (IOException e) {
						e.printStackTrace();
						showMessage=e.getMessage();
						return showMessage(showMessage, forwardUrl, forwardSeconds);
					}
				}
				//创建源文件目录
				FileUtil.mkdir(getHttpRequest().getRealPath("/")+"site/"+site.getSourcepath());
				boolean isinit=false;
				if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
					templet=templetService.findById(site.getIndextemplet());
					if (templet!=null) {
						//复制模板文件夹下resources文件夹到此站点
						try {
							FileUtil.copyDirectiory(
									getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/resources", 
									getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/resources");
							//判断模板是否有初始化数据
							init("templetChannelService");
							if (templetChannelService.count(templet.getId())>0) {
								isinit=true;
							}else {
								init("templetLinkService");
								if (templetLinkService.count(templet.getId())>0) {
									isinit=true;
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				siteService.update(site);
				OperLogUtil.log(getLoginName(), "添加站点 "+site.getName(), getHttpRequest());
				if (isinit) {
					return "guideInit";
				}
				return guideCompleted();
			}else {
				showMessage="没有找到此站点";
				return showMessage(showMessage, forwardUrl, forwardSeconds);
			}
		}else {
			showMessage="没有传递站点id参数";
			return showMessage(showMessage, forwardUrl, forwardSeconds);
		}
	}

	/**
	 * 建站向导 初始化站点
	 * @return
	 */
	public String guideInit(){
		try {
			if (site.getId()!=null && site.getId().trim().length()>0 && 
					site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
				site=siteService.findById(site.getId());
				templet=templetService.findById(site.getIndextemplet());
				if (site!=null && templet!=null) {
					init("templetChannelService");
					templetChannelService.importSiteChannels(templet, site);
					init("templetLinkService");
					templetLinkService.importSiteLinks(templet, site);
				}
			}
			return guideCompleted();
		} catch (Exception e) {
			e.printStackTrace();
			showMessage="站点初始化失败:"+e.getMessage();
			return showMessage(showMessage, forwardUrl, forwardSeconds);
		}
	}
	/**
	 * 建站向导 创建栏目
	 * @return
	 */
	public String guideCompleted(){
		if (site.getId()!=null && site.getId().trim().length()>0){
			site=siteService.findById(site.getId());
		}
		return "guideCompleted";
	}
	/**
	 * 多项选择站点
	 * @return
	 */
	public String siteCheck(){
		if (isAdminLogin()) {
			//查询一级站点
			siteList=siteService.selectByParId( "");
		}else {
			//非管理员只查询有权限的站点
			siteList=siteService.selectByRoles(getLoginRoleIdsSql());
		}
		if (siteList!=null && siteList.size()>0) {
			if (role!=null) {
				for (int i = 0; i < siteList.size(); i++) {
					if (roleSiteService.have(siteList.get(i).getId(), role.getId())) {
						siteList.get(i).setHaveSiteRole("1");
					}
				}
			}
		}
		return "siteCheck";
	}
	/**
	 * 授权页面
	 * @return
	 */
	public String auth(){
		// 如果是管理员登录则提取一级站点
		if (isAdminLogin()) {
			siteList=siteService.selectByParId( "");
		}else {
			//普通用户只提取有自己有权限的站点
			siteList=siteService.selectByRoles(getLoginRoleIdsSql());
		}
		//设置、
		if (siteList!=null && siteList.size()>0) {
			if (role!=null) {
				for (int i = 0; i < siteList.size(); i++) {
					if (roleSiteService.have(siteList.get(i).getId(), role.getId())) {
						siteList.get(i).setHaveSiteRole(" <font color='blue'>★</font>");
					}
				}
			}
		}
		return "auth";
	}
	/**
	 * 单个站点授权页面
	 * @return
	 */
	public String authPage(){
		if (site!=null && site.getId().trim().length()>0
				&& role!=null && role.getId().trim().length()>0) {
			site=siteService.findById(site.getId());
			//判断是否有站点管理权限
			roleSite=roleSiteService.findBySiteRole(site.getId(), role.getId());
			channelTreeContent=new StringBuffer();
			channelService.createTree(channelTreeContent,(isAdminLogin()?"admin":getLoginRoleIdsSql()), site.getId(),role.getId(), "par", null, null, "checkbox");
		}
		return "authPage";
	}
	/**
	 * 单个站点授权页面处理
	 * @return
	 */
	public String authPageDo(){
		if (site!=null && site.getId().trim().length()>0
				&& role!=null && role.getId().trim().length()>0) {
			//先清除原来的权限
			roleSiteService.del(site.getId(), role.getId());
			roleChannelService.delByRole( role.getId());
			//添加新的权限
			if ("1".equals(manageSiteChecked)) {
				roleSiteService.save(site.getId(), role.getId(),(roleSite!=null&&roleSite.getSiteadmin()!=null)?roleSite.getSiteadmin():"");
				if (channelTree!=null && channelTree.length>0) {
					for (int i = 0; i < channelTree.length; i++) {
						roleChannelService.save(role.getId(), channelTree[i]);
					}
				}
			}
		}
		write("<script>alert('操作成功');location.href='site_authPage.do?site.id="+site.getId()+"&role.id="+role.getId()+"';</script>", "GBK");
		return null;
	}
	/**
	 * 查询站点列表（树结构使用）
	 * @return
	 */
	public String site(){
		if (isAdminLogin()) {
			//提取一级站点
			siteList=siteService.selectByParId( "");
		}else {
			//普通用户只提取有自己有权限的站点
			siteList=siteService.selectByRoles(getLoginRoleIdsSql());
		}
		if ("select".equals(type)) {
			return "siteSelect";
		}
		else if ("selectPar".equals(type)) {
			return "siteSelectPar";
		}
		else if ("siteSelectPage".equals(type)) {
			return "siteSelectPage";
		}
		return "site";
	}
	/**
	 * 查询栏目 生成json数据
	 * @return
	 */
	public String getChannel(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
			if (isAdminLogin() ) {
				channelList=channelService.findByPar(site.getId(), "par");
			}else {
				channelList=channelService.findByRoles(site.getId(),  getLoginRoleIdsSql());
			}
			if (channelList!=null && channelList.size()>0) {
				StringBuffer sb=new StringBuffer();
				sb.append("[");
				for (int i = 0; i < channelList.size(); i++) {
					if (i>0) {
						sb.append(",");
					}
					sb.append("{id:'"+channelList.get(i).getId()+"',name:'"+channelList.get(i).getName()+"'}");
				}
				sb.append("]");
				write(sb.toString(), "UTF-8");
			}
		}
		return null;
	}
	/**
	 * 查询子站点
	 * @return
	 */
	public String authSon(){	
		try {
			List<Site> list=null; 
			//提取子站点
			list=siteService.selectByParId(root);
			//生成树
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("[");
			if (list!=null && list.size()>0) {
				for (int i = 0; i <list.size() ; i++) {
					if (site!=null && site.getId()!=null && site.getId().trim().length()>0 && site.getId().equals(list.get(i).getId())) {
						continue;
					}
					if (!"[".equals(stringBuilder.toString())) {
						stringBuilder.append(",");
					}
					stringBuilder.append("{ \"text\": \"<a  onclick=");
					if (onclick!=null && onclick.trim().length()>0) {
						stringBuilder.append(onclick);
					}else {
						stringBuilder.append("showDetail");
					}
					stringBuilder.append("('");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("','"+list.get(i).getName().replaceAll(" ", "")+"','"+(site!=null && site.getId()!=null?site.getId():"")+"')>");
					stringBuilder.append(list.get(i).getName());
					//判断是否有权限
					if (role!=null && role.getId().trim().length()>0 && roleSiteService.have(list.get(i).getId(), role.getId())) {
						stringBuilder.append(" ★");
					}
					stringBuilder.append("\", \"hasChildren\": ");
					if (siteService.hasChildren(list.get(i).getId())) {
						stringBuilder.append("true");
					}else {
						stringBuilder.append("false");
					}
					stringBuilder.append(",\"id\":\"");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("\" }");
				}
			}
			stringBuilder.append("]");
			ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 查询子站点
	 * @return
	 */
	public String son(){	
		try {
			List<Site> list=null; 
			//提取子站点
			list=siteService.selectByParId(root);
			//生成树
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("[");
			if (list!=null && list.size()>0) {
				for (int i = 0; i <list.size() ; i++) {
					if (site!=null && site.getId()!=null && site.getId().trim().length()>0 && site.getId().equals(list.get(i).getId())) {
						continue;
					}
					if (!"[".equals(stringBuilder.toString())) {
						stringBuilder.append(",");
					}
					stringBuilder.append("{ \"text\": \"<a  onclick=");
					if (onclick!=null && onclick.trim().length()>0) {
						stringBuilder.append(onclick);
					}else {
						stringBuilder.append("showDetail");
					}
					stringBuilder.append("('");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("','"+list.get(i).getName().replaceAll(" ", "")+"','"+(site!=null && site.getId()!=null?site.getId():"")+"')><b>");
					stringBuilder.append(list.get(i).getName());
					stringBuilder.append("\", \"hasChildren\": ");
					if (siteService.hasChildren(list.get(i).getId())) {
						stringBuilder.append("true");
					}else {
						stringBuilder.append("false");
					}
					stringBuilder.append(",\"id\":\"");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("\" }");
				}
			}
			stringBuilder.append("]");
			ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 查询子站点
	 * @return
	 */
	public String checkSon(){	
		try {
			List<Site> list=null; 
			//提取子站点
			list=siteService.selectByParId(root);
			//生成树
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("[");
			if (list!=null && list.size()>0) {
				for (int i = 0; i <list.size() ; i++) {
					if (site!=null && site.getId()!=null && site.getId().trim().length()>0 && site.getId().equals(list.get(i).getId())) {
						continue;
					}
					if (!"[".equals(stringBuilder.toString())) {
						stringBuilder.append(",");
					}
					stringBuilder.append("{ \"text\": \"<input onclick='siteCheck(this)' type=checkbox ");
					if (sites!=null && sites.trim().length()>0 && sites.indexOf(list.get(i).getId()+";")>-1) {
						stringBuilder.append("checked");
					}
					stringBuilder.append(" name=sites value="+list.get(i).getId()+" show="+list.get(i).getName()+">");
					stringBuilder.append(list.get(i).getName());
					stringBuilder.append("\", \"hasChildren\": ");
					if (siteService.hasChildren(list.get(i).getId())) {
						stringBuilder.append("true");
					}else {
						stringBuilder.append("false");
					}
					stringBuilder.append(",\"id\":\"");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("\" }");
				}
			}
			stringBuilder.append("]");
			ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 改变所属站点
	 * @return
	 */
	public String sitePar(){
		try {
			if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
				if (site.getParid()!=null && site.getParid().trim().length()>0) {
					if ("root".equals(site.getParid())) {
						//改变为一级站点
						site=siteService.findById(site.getId());
						if (site!=null) {
							site.setParid("");
							siteService.update(site);
							OperLogUtil.log(getLoginName(), "改变站点 "+site.getName()+" 为 一级站点", getHttpRequest());
							write("<script>alert('操作成功');parent.location.reload();</script>", "UTF-8");
						}
					}else {
						Site parSite=siteService.findById(site.getParid());
						if (parSite!=null) {
							site=siteService.findById(site.getId());
							if (site!=null) {
								site.setParid(parSite.getId());
								siteService.update(site);
								OperLogUtil.log(getLoginName(), "改变站点 "+site.getName()+" 的所属站点为 "+parSite.getName(), getHttpRequest());
								write("<script>alert('操作成功');parent.location.reload();</script>", "UTF-8");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			write(e.toString(), "UTF-8");
		}
		return null;
	}
	/**
	 * 删除
	 * @return
	 */
	public String del(){
		try {
			if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
				site=siteService.findById(site.getId());
				siteService.del(site.getId(),getHttpRequest());
				OperLogUtil.log(getLoginName(), "删除站点 "+site.getName(), getHttpRequest());
				write("<script>alert('操作成功');parent.location.reload();</script>", "utf-8");
			}
		} catch (Exception e) {
			write(e.toString(), "utf-8");
		}
		return null;
	}
	/**
	 * 添加页面
	 * @return
	 */
	public String edit(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
			site=siteService.findById(site.getId());
			//设置模板名称
			if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
				templet=templetService.findById(site.getIndextemplet());
				if (templet!=null) {
					site.setIndextempletName(templet.getName());
				}
			}
			init("htmlquartzService");
			htmlquartz=htmlquartzService.findBySiteid(site.getId());
		}
		return "edit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		try {
			if (site.getName()!=null) {
				site.setName(site.getName().replace("'", "‘").replace("\"", "“"));
			}
			if (site.getId()!=null && site.getId().trim().length()>0) {
				//更新
				Site oldSite=siteService.findById(site.getId());
				//如果原来有和现在的logo不同则删除原来的logo文件 
				if (!oldLogo.equals(oldSite.getLogo())) {
					if(oldSite.getLogo()!=null &&  oldSite.getLogo().trim().length()>0){
						FileUtil.del(getHttpRequest().getRealPath("/").replace("\\", "/")+oldSite.getLogo().trim());
					}
				}else {
					site.setLogo(oldLogo);
				}
				if (logo!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/").replace("\\", "/");
					String ext=FileUtil.getExt(logoFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
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
					FileUtil.copy(logo, targetFile);

					//生成访问地址
					site.setLogo("/upload/"+site.getId()+"/"+id+ext);
				}
				if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0
						&& !site.getIndextemplet().equals(oldSite.getIndextemplet())) {
					templet=templetService.findById(site.getIndextemplet());
					if (templet!=null) {
						//复制模板文件夹下resources文件夹到此站点
						try {
							FileUtil.copyDirectiory(
									getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/resources", 
									getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/resources");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				siteService.update(site);
				//处理静态化调度
				init("htmlquartzService");
				if (htmlquartzService.findBySiteid(site.getId())!=null) {
					htmlquartzService.update(htmlquartz);
				}else {
					htmlquartz.setSiteid(site.getId());
					htmlquartzService.insert(htmlquartz);
				}
				siteService.updateHtmlSiteJob(getServletContext(), site, htmlquartz);
				OperLogUtil.log(getLoginName(), "更新站点 "+site.getName(), getHttpRequest());
			}else {
				//添加
				if (siteService.haveSourcePath( site.getSourcepath())) {
					write("<script>alert('此源文件目录已存在');history.back();</script>", "GBK");
					return null;
				}
				if (logo!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/").replace("\\", "/");
					String ext=FileUtil.getExt(logoFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
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
					FileUtil.copy(logo, targetFile);
					//生成访问地址
					site.setLogo("/upload/"+site.getId()+"/"+id+ext);
				}
				//创建源文件目录
				FileUtil.mkdir(getHttpRequest().getRealPath("/")+"site/"+site.getSourcepath());
				boolean isinit=false;
				if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
					templet=templetService.findById(site.getIndextemplet());
					if (templet!=null) {
						//复制模板文件夹下resources文件夹到此站点
						try {
							FileUtil.copyDirectiory(
									getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/resources", 
									getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/resources");
							//判断模板是否有初始化数据
							init("templetChannelService");
							if (templetChannelService.count(templet.getId())>0) {
								isinit=true;
							}else {
								init("templetLinkService");
								if (templetLinkService.count(templet.getId())>0) {
									isinit=true;
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				siteService.insert(site);
				//处理静态化调度
				init("htmlquartzService");
				htmlquartz.setSiteid(site.getId());
				htmlquartzService.insert(htmlquartz);
				siteService.updateHtmlSiteJob(getServletContext(), site, htmlquartz);
				OperLogUtil.log(getLoginName(), "添加站点 "+site.getName(), getHttpRequest());
				if (isinit) {
					return "init";
				}
			}
			write("<script>alert('操作成功');location.href='site_edit.do?site.id="+site.getId()+"';</script>", "GBK");
		} catch (Exception e) {
			DBProException(e);
			write(e.toString(), "GBK");
		}
		
		return null;
	}
	/**
	 * 初始化站点
	 * @return
	 */
	public String init(){
		try {
			if (site.getId()!=null && site.getId().trim().length()>0 && 
					site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
				site=siteService.findById(site.getId());
				templet=templetService.findById(site.getIndextemplet());
				if (site!=null && templet!=null) {
					init("templetChannelService");
					templetChannelService.importSiteChannels(templet, site);
					init("templetLinkService");
					templetLinkService.importSiteLinks(templet, site);
				}
			}
			showMessage="站点初始化成功";
		} catch (Exception e) {
			e.printStackTrace();
			showMessage="站点初始化失败:"+e.getMessage();
		}finally{
			return showMessage(showMessage, forwardUrl, forwardSeconds);
		}
	}
	/**
	 * 设置页面
	 * @return
	 */
	public String config(){
		site=getManageSite();
		if (site!=null) {
			//设置模板名称
			if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0) {
				templet=templetService.findById(site.getIndextemplet());
				if (templet!=null) {
					site.setIndextempletName(templet.getName());
				}
			}
			init("htmlquartzService");
			htmlquartz=htmlquartzService.findBySiteid(site.getId());
		}
		return "config";
	}
	/**
	 * 设置页面处理
	 * @return
	 */
	public String configDo(){
		try {
			if (site.getName()!=null) {
				site.setName(site.getName().replace("'", "‘").replace("\"", "“"));
			}
			if (site.getId()!=null && site.getId().trim().length()>0) {
				//更新
				Site oldSite=siteService.findById(site.getId());
				//如果原来有和现在的logo不同则删除原来的logo文件 
				if (!oldLogo.equals(oldSite.getLogo())) {
					if(oldSite.getLogo()!=null &&  oldSite.getLogo().trim().length()>0){
						FileUtil.del(getHttpRequest().getRealPath("/")+oldSite.getLogo().trim());
					}
				}else {
					site.setLogo(oldLogo);
				}
				if (logo!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(logoFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
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
					FileUtil.copy(logo, targetFile);

					//生成访问地址
					site.setLogo("/upload/"+site.getId()+"/"+id+ext);
				}
				if (site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0
						&& !site.getIndextemplet().equals(oldSite.getIndextemplet())) {
					templet=templetService.findById(site.getIndextemplet());
					if (templet!=null) {
						//复制模板文件夹下resources文件夹到此站点
						try {
							FileUtil.copyDirectiory(
									getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/resources", 
									getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/resources");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				siteService.update(site);
				//处理静态化调度
				init("htmlquartzService");
				if (htmlquartzService.findBySiteid(site.getId())!=null) {
					htmlquartzService.update(htmlquartz);
				}else {
					htmlquartz.setSiteid(site.getId());
					htmlquartzService.insert(htmlquartz);
				}
				siteService.updateHtmlSiteJob(getServletContext(), site, htmlquartz);
				getHttpSession().setAttribute("manageSite", site);
				OperLogUtil.log(getLoginName(), "站点设置 "+site.getName(), getHttpRequest());
			}
			write("<script>alert('操作成功');location.href='site_config.do?pageFuncId="+pageFuncId+"';</script>", "GBK");
		} catch (Exception e) {
			DBProException(e);
			write(e.toString(), "GBK");
		}
		
		return null;
	}
	/**
	 * 处理ajax上传logo
	 * @return
	 */
	public String editLogo(){
		
		if (logo!=null) {
			try {
				//生成目标文件
				String root=getHttpRequest().getRealPath("/");
				String ext=FileUtil.getExt(logoFileName).toLowerCase();
				if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
					write("{error:'logo只能上传jpg,jpeg,gif,png格式的图片!',msg:''}", "GBK");
					return null;
				}
				String id=UUID.randomUUID().toString();
				File targetFile=new File(root+"\\upload\\"+id+ext);
				File targetFolder=new File(root+"\\upload");
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				if (!targetFile.exists()) {
					try {
						targetFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//复制到目标文件
				FileUtil.copy(logo, targetFile);
				//生成访问地址
				write("{msg:'/upload/"+id+ext+"',error:''}", "GBK");
			} catch (Exception e) {
				e.printStackTrace();
				write("{msg:'',error:'上传LOGO失败!'}", "GBK");
			}
		}else {
			write("{msg:'',error:''}", "GBK");
		}
		return null;
	}
	/**
	 * 同步模板资源
	 * @return
	 */
	public String syncRes(){
		if (site!=null && site.getIndextemplet()!=null && site.getIndextemplet().trim().length()>0
				&& site.getId()!=null && site.getId().trim().length()>0) {
			templet=templetService.findById(site.getIndextemplet());
			site=siteService.findById(site.getId());
			if (site!=null && templet!=null) {
				//复制模板文件夹下resources文件夹到此站点
				try {
					FileUtil.copyDirectiory(
							getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/resources", 
							getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/resources");
					write("操作成功", "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
					write("操作失败:"+e.getMessage(), "UTF-8");
				}
			}else {
				write("操作失败:没有传递正确的参数", "UTF-8");
			}
		}else {
			write("操作失败:没有传递正确的参数", "UTF-8");
		}
		return null;
	}
	
	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}


	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getOldSourcepath() {
		return oldSourcepath;
	}

	public void setOldSourcepath(String oldSourcepath) {
		this.oldSourcepath = oldSourcepath;
	}

	public File getLogo() {
		return logo;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public String getLogoFileName() {
		return logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}

	public String getOldLogo() {
		return oldLogo;
	}

	public void setOldLogo(String oldLogo) {
		this.oldLogo = oldLogo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getWasUser() {
		return wasUser;
	}
	public void setWasUser(String wasUser) {
		this.wasUser = wasUser;
	}
	public String getOperUser() {
		return operUser;
	}
	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}
	public List<Site> getSiteList() {
		return siteList;
	}
	public void setSiteList(List<Site> siteList) {
		this.siteList = siteList;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public List<Channel> getChannelList() {
		return channelList;
	}
	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}
	public ChannelService getChannelService() {
		return channelService;
	}
	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
	public String getSites() {
		return sites;
	}
	public void setSites(String sites) {
		this.sites = sites;
	}
	public Templet getTemplet() {
		return templet;
	}
	public void setTemplet(Templet templet) {
		this.templet = templet;
	}
	public TempletService getTempletService() {
		return templetService;
	}
	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}
	public Roles getRole() {
		return role;
	}
	public void setRole(Roles role) {
		this.role = role;
	}
	public StringBuffer getChannelTreeContent() {
		return channelTreeContent;
	}
	public void setChannelTreeContent(StringBuffer channelTreeContent) {
		this.channelTreeContent = channelTreeContent;
	}
	public RoleSiteService getRoleSiteService() {
		return roleSiteService;
	}
	public void setRoleSiteService(RoleSiteService roleSiteService) {
		this.roleSiteService = roleSiteService;
	}
	public RoleChannelService getRoleChannelService() {
		return roleChannelService;
	}
	public void setRoleChannelService(RoleChannelService roleChannelService) {
		this.roleChannelService = roleChannelService;
	}
	public boolean isHaveSiteRole() {
		return haveSiteRole;
	}
	public void setHaveSiteRole(boolean haveSiteRole) {
		this.haveSiteRole = haveSiteRole;
	}
	public RoleSite getRoleSite() {
		return roleSite;
	}
	public void setRoleSite(RoleSite roleSite) {
		this.roleSite = roleSite;
	}
	public String getManageSiteChecked() {
		return manageSiteChecked;
	}
	public void setManageSiteChecked(String manageSiteChecked) {
		this.manageSiteChecked = manageSiteChecked;
	}
	public HtmlquartzService getHtmlquartzService() {
		return htmlquartzService;
	}
	public void setHtmlquartzService(HtmlquartzService htmlquartzService) {
		this.htmlquartzService = htmlquartzService;
	}
	public Htmlquartz getHtmlquartz() {
		return htmlquartz;
	}
	public void setHtmlquartz(Htmlquartz htmlquartz) {
		this.htmlquartz = htmlquartz;
	}
	public List<Integer> getHours() {
		hours=new ArrayList<Integer>();
		for (int i = 0; i < 24; i++) {
			hours.add(i);
		}
		return hours;
	}
	public void setHours(List<Integer> hours) {
		this.hours = hours;
	}
	public List<Integer> getMins() {
		mins=new ArrayList<Integer>();
		for (int i = 0; i < 60; i++) {
			mins.add(i);
		}
		return mins;
	}
	public void setMins(List<Integer> mins) {
		this.mins = mins;
	}
	public TempletChannelService getTempletChannelService() {
		return templetChannelService;
	}
	public void setTempletChannelService(TempletChannelService templetChannelService) {
		this.templetChannelService = templetChannelService;
	}
	public TempletLinkService getTempletLinkService() {
		return templetLinkService;
	}
	public void setTempletLinkService(TempletLinkService templetLinkService) {
		this.templetLinkService = templetLinkService;
	}

}
