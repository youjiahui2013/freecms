package cn.freeteam.cms.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.HtmlquartzService;
import cn.freeteam.cms.service.RoleChannelService;
import cn.freeteam.cms.service.SensitiveService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;


/**
 * 
 * <p>Title: ChannelAction.java</p>
 * 
 * <p>Description: 关于栏目的操作</p>
 * 
 * <p>Date: Jan 23, 2012</p>
 * 
 * <p>Time: 11:33:00 AM</p>
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
public class ChannelAction extends BaseAction{

	private SiteService siteService;
	private ChannelService channelService;
	private UserService userService;
	private RoleChannelService roleChannelService;
	private HtmlquartzService htmlquartzService;
	private SensitiveService sensitiveService;
	
	private Site site;
	private Channel channel;
	private Htmlquartz htmlquartz;
	private File img;
	private String imgFileName;
	private String oldImg;
	private String root;
	private String onclick;
	private List<Integer> hours;
	private List<Integer> mins;

	private String wasUser;
	private String operUser;
	private String msg;
	private String result;
	private Users user;
	private String logContent;
	private List<Channel> channelList;
	private List<Site> siteList;
	private String auth;
	private String noShowSite;

	private String htmlChannel;
	private String htmlChannelPar;
	private String htmlIndex;
	public String getHtmlChannel() {
		return htmlChannel;
	}

	public void setHtmlChannel(String htmlChannel) {
		this.htmlChannel = htmlChannel;
	}

	public String getHtmlChannelPar() {
		return htmlChannelPar;
	}

	public void setHtmlChannelPar(String htmlChannelPar) {
		this.htmlChannelPar = htmlChannelPar;
	}

	public String getHtmlIndex() {
		return htmlIndex;
	}

	public void setHtmlIndex(String htmlIndex) {
		this.htmlIndex = htmlIndex;
	}

	public ChannelAction(){
		init("siteService","channelService","userService","roleChannelService");
	}

	/**
	 * 栏目管理页面
	 * @return
	 */
	public String channel(){
		if (channel!=null && channel.getSite()!=null && channel.getSite().trim().length()>0) {
			site=siteService.findById(channel.getSite().trim());
		}else {
			site=getManageSite();
		}
		if (site!=null) {
			//获取当前管理站点
			if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
				if (!channel.getId().equals("select")) {
					channel=channelService.findById(channel.getId());
				}
				channelList=channelService.findByPar(site.getId(), "par");
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
				return "channelSelect";
			}else {
				//栏目管理页面
				//获取一级栏目 
				channelList=channelService.findByPar(site.getId(), "par");
				//设置是否有子栏目
				if (channelList!=null && channelList.size()>0) {
					for (int i = 0; i < channelList.size(); i++) {
						if (channelService.hasChildren(channelList.get(i).getId())) {
							channelList.get(i).setHasChildren("1");
						}
						if (!isAdminLogin() && !isSiteAdmin()) {
							//如果是普通管理员则设置是否有权限管理
							if (roleChannelService.haves(getLoginRoleIdsSql(), channelList.get(i).getId())) {
								channelList.get(i).setHaveChannelRole("1");
							}
						}
					}
				}
			}
		}
		return "channel";
	}
	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
			site=siteService.findById(site.getId());
		}
		if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
			channel=channelService.findById(channel.getId());
			init("htmlquartzService");
			htmlquartz=htmlquartzService.findByChannelid(channel.getId());
			site=siteService.findById(channel.getSite());
		}
		return "edit";
	}
	/**
	 * 删除
	 * @return
	 */
	public String del(){
		try {
			if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
				channelService.del(channel.getId(),getHttpRequest());
				OperLogUtil.log(getLoginName(), "删除栏目 "+channel.getName(), getHttpRequest());
				write("<script>alert('操作成功');parent.location.reload();</script>", "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			write(e.toString(), "UTF-8");
		}
		return null;
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		try {
			site=siteService.findById(site.getId());
			//敏感词处理
			init("sensitiveService");
			channel.setName(sensitiveService.replace(channel.getName()));
			channel.setDescription(sensitiveService.replace(channel.getDescription()));
			if (channel.getId()!=null && channel.getId().trim().length()>0) {
				//更新
				Channel oldChannel=channelService.findById(channel.getId());
				//如果原来有和现在的pagemark不同则判断新的pagemark是否存在
				if (channel.getPagemark()!=null && channel.getPagemark().trim().length()>0 && 
						!channel.getPagemark().equals(oldChannel.getPagemark())) {
					if (channelService.hasPagemark(channel.getSite(), channel.getPagemark())) {
						write("<script>alert('此页面标识已存在!');history.back();</script>", "GBK");
						return null;
					}
					//修改栏目静态文件目录
					String folder="";
					File folderFile=null;
					if (oldChannel.getPagemark()!=null && oldChannel.getPagemark().trim().length()>0) {
						folder=oldChannel.getPagemark().trim();
						folderFile=new File(getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/"+folder);
					}
					if ((folderFile==null || !folderFile.exists()) && oldChannel.getIndexnum()>0) {
						folder=String.valueOf(oldChannel.getIndexnum());
						folderFile=new File(getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/"+folder);
					}
					if (folderFile==null || !folderFile.exists()) {
						folder=oldChannel.getId();
						folderFile=new File(getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/"+folder);
					}
					//判断目录是否存在
					if (folderFile.exists()) {
						//修改目录名
						folderFile.renameTo(new File(getHttpRequest().getRealPath("/")
								+"/site/"+site.getSourcepath()+"/"+channel.getPagemark().trim()));
					}
				}
				//如果原来有pagemark，现在删除了
				if (oldChannel.getPagemark()!=null && oldChannel.getPagemark().trim().length()>0
						&& (channel.getPagemark()==null || channel.getPagemark().trim().length()==0)) {

					//修改栏目静态文件目录
					String folder="";
					File folderFile=null;
					folder=oldChannel.getPagemark().trim();
					folderFile=new File(getHttpRequest().getRealPath("/")+"/site/"+site.getSourcepath()+"/"+folder);
					//判断目录是否存在
					if (folderFile.exists()) {
						//修改目录名
						String rename="";
						if (oldChannel.getIndexnum()>0) {
							rename=String.valueOf(oldChannel.getIndexnum());
						}else {
							rename=oldChannel.getId();
						}
						folderFile.renameTo(new File(getHttpRequest().getRealPath("/")
								+"/site/"+site.getSourcepath()+"/"+rename));
					}
				}
				//如果原来有和现在的logo不同则删除原来的logo文件 
				if (!oldImg.equals(oldChannel.getImg()) && oldChannel.getImg()!=null && oldChannel.getImg().trim().length()>0) {
					FileUtil.del(getHttpRequest().getRealPath("/")+oldChannel.getImg().trim());
				}else {
					channel.setImg(oldImg);
				}
				if (img!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(imgFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
					String id=UUID.randomUUID().toString();
					File targetFile=new File(root+"/upload/"+site.getId()+"/"+id+ext);
					File folder=new File(root+"/upload/"+site.getId()+"/");
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(img, targetFile);
					//生成访问地址
					channel.setImg("/upload/"+site.getId()+"/"+id+ext);
				}
				channelService.update(channel);
				//处理静态化调度
				init("htmlquartzService");
				if (htmlquartzService.findByChannelid(channel.getId())!=null) {
					htmlquartzService.update(htmlquartz);
				}else {
					htmlquartz.setSiteid(site.getId());
					htmlquartz.setChannelid(channel.getId());
					htmlquartzService.insert(htmlquartz);
				}
				channelService.updateHtmlChannelJob(getServletContext(), site, channel, htmlquartz);
				OperLogUtil.log(getLoginName(), "更新栏目 "+channel.getName(), getHttpRequest());
			}else {
				//添加
				//判断页面标识是否存在
				if (channel.getPagemark()!=null && channel.getPagemark().trim().length()>0 && 
						channelService.hasPagemark(channel.getSite(), channel.getPagemark())) {
					write("<script>alert('此页面标识已存在!');history.back();</script>", "GBK");
					return null;
				}
				if (img!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(imgFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
					String id=UUID.randomUUID().toString();
					File targetFile=new File(root+"/upload/"+site.getId()+"/"+id+ext);
					File folder=new File(root+"/upload/"+site.getId()+"/");
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(img, targetFile);
					//生成访问地址
					channel.setImg("/upload/"+site.getId()+"/"+id+ext);
				}
				channelService.insert(channel);
				//处理静态化调度
				init("htmlquartzService");
				htmlquartz.setSiteid(site.getId());
				htmlquartz.setChannelid(channel.getId());
				htmlquartzService.insert(htmlquartz);
				channelService.updateHtmlChannelJob(getServletContext(), site, channel, htmlquartz);
				OperLogUtil.log(getLoginName(), "添加栏目 "+channel.getName(), getHttpRequest());
			}
			return "makehtml";
		} catch (Exception e) {
			DBProException(e);
			write("<script>alert('"+e.toString()+"');history.back();</script>", "GBK");
		}
		
		return null;
	}

	

	/**
	 * 树结构
	 * @return
	 */
	public String son(){		
		List<Channel> list=null; 
		list=channelService.findByPar("", root);
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0 && channel.getId().equals(list.get(i).getId())) {
					continue;
				}
				if (!"[".equals(stringBuilder.toString())) {
					stringBuilder.append(",");
				}
				stringBuilder.append("{ \"text\": \"");
				//是否生成onclick事件
				boolean click=true;
				if ("1".equals(auth) 
						&& !isAdminLogin() 
						&& !isSiteAdmin() 
						&& !roleChannelService.haves(getLoginRoleIdsSql(), list.get(i).getId())) {
					//如果没有权限则不生成onclick事件
					click=false;
				}
				if (click) {
					stringBuilder.append("<a  onclick=");
					if (onclick!=null && onclick.trim().length()>0) {
						stringBuilder.append(onclick);
					}else {
						stringBuilder.append("showOne");
					}
					stringBuilder.append("('");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("','");
					stringBuilder.append(list.get(i).getName().replaceAll(" ", ""));
					stringBuilder.append("')><b>");
				}
				stringBuilder.append(list.get(i).getName());
				if (click) {
					stringBuilder.append("</b>");
				}
				stringBuilder.append("\", \"hasChildren\": ");
				if (channelService.hasChildren(list.get(i).getId())) {
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
		return null;
	}
	/**
	 * 树结构复选框
	 * @return
	 */
	public String sonCheck(){		
		List<Channel> list=null; 
		list=channelService.findByPar("", root);
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0 && channel.getId().equals(list.get(i).getId())) {
					continue;
				}
				if (!"[".equals(stringBuilder.toString())) {
					stringBuilder.append(",");
				}
				stringBuilder.append("{ \"text\": \"");
				//是否生成onclick事件
				boolean click=true;
				if ("1".equals(auth) 
						&& !isAdminLogin() 
						&& !isSiteAdmin() 
						&& !roleChannelService.haves(getLoginRoleIdsSql(), list.get(i).getId())) {
					//如果没有权限则不生成onclick事件
					click=false;
				}
				if (click) {
					stringBuilder.append("<input ");
					stringBuilder.append(" type=checkbox name=channels onclick=");
					if (onclick!=null && onclick.trim().length()>0) {
						stringBuilder.append(onclick);
					}else {
						stringBuilder.append("check");
					}
					stringBuilder.append("('");
					stringBuilder.append(list.get(i).getId());
					stringBuilder.append("','"+list.get(i).getName().replaceAll(" ", "")+"',this) value="+list.get(i).getId());
					stringBuilder.append("><b>");
				}
				stringBuilder.append(list.get(i).getName());
				stringBuilder.append("\", \"hasChildren\": ");
				if (channelService.hasChildren(list.get(i).getId())) {
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
		return null;
	}
	/**
	 * 改变所属栏目
	 * @return
	 */
	public String par(){
		try {
			if (channel!=null) {
				if (channel.getId()!=null && channel.getId().trim().length()>0
					&& channel.getParid()!=null && channel.getParid().trim().length()>0) {
					//改为栏目
					Channel parChannel=channelService.findById(channel.getParid());
					if (parChannel!=null) {
						channel=channelService.findById(channel.getId());
						if (channel!=null) {
							channel.setParid(parChannel.getId());
							channelService.update(channel);
							OperLogUtil.log(getLoginName(), "改变栏目 "+channel.getName()+" 的所属栏目为 "+parChannel.getName(), getHttpRequest());
							write("操作成功", "UTF-8");
						}
					}
				}else if (channel.getSite()!=null && channel.getSite().trim().length()>0) {
					//改为站点
					Channel oldchannel=channelService.findById(channel.getId());
					site=siteService.findById(channel.getSite());
					if (oldchannel!=null && site!=null) {
						oldchannel.setParid("");
						oldchannel.setSite(channel.getSite());
						channelService.update(oldchannel);
						OperLogUtil.log(getLoginName(), "改变栏目 "+oldchannel.getName()+" 的所属栏目为 "+site.getName()+" 站点的一级栏目", getHttpRequest());
						write("操作成功", "UTF-8");
					}
				}
			}
		} catch (Exception e) {
			write(e.toString(), "UTF-8");
		}
		return null;
	}
	

	/**
	 * 静态化处理
	 * @return
	 */
	public String makehtml(){
		if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
			channel=channelService.findById(channel.getId());
			site=siteService.findById(channel.getSite());
			try {
				if ("1".equals(htmlChannel)) {
					//本栏目静态化
					channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannelPar)) {
					//本栏目的父栏目静态化
					List<Channel> channeList = channelService.findPath(channel.getId());
					if (channeList!=null && channeList.size()>0) {
						for (int i = 0; i < channeList.size(); i++) {
							if (!channeList.get(i).getId().equals(channel.getId())) {
								channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
							}
						}
					}
				}
				if ("1".equals(htmlIndex)) {
					//首页静态化
					siteService.html(channel.getSite(), getServletContext(), getBasePath(), getHttpRequest(), getLoginName());
				}
				showMessage="静态化处理成功!";
			} catch (Exception e) {
				e.printStackTrace();
				showMessage="静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
			}
		}
		return showMessage(showMessage, "", 0);
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


	public Channel getChannel() {
		return channel;
	}


	public void setChannel(Channel channel) {
		this.channel = channel;
	}


	public ChannelService getChannelService() {
		return channelService;
	}


	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}

	public File getImg() {
		return img;
	}

	public void setImg(File img) {
		this.img = img;
	}

	public String getImgFileName() {
		return imgFileName;
	}

	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}

	public String getOldImg() {
		return oldImg;
	}

	public void setOldImg(String oldImg) {
		this.oldImg = oldImg;
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

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public RoleChannelService getRoleChannelService() {
		return roleChannelService;
	}

	public void setRoleChannelService(RoleChannelService roleChannelService) {
		this.roleChannelService = roleChannelService;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getNoShowSite() {
		return noShowSite;
	}

	public void setNoShowSite(String noShowSite) {
		this.noShowSite = noShowSite;
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

	public SensitiveService getSensitiveService() {
		return sensitiveService;
	}

	public void setSensitiveService(SensitiveService sensitiveService) {
		this.sensitiveService = sensitiveService;
	}
}
