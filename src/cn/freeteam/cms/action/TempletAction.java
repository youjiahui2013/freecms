package cn.freeteam.cms.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletChannel;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.TempletChannelService;
import cn.freeteam.cms.service.TempletLinkService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.model.Roles;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.HtmlCode;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;
import cn.freeteam.util.ResponseUtil;
import cn.freeteam.util.ZipTools;


/**
 * 
 * <p>Title: TempletAction.java</p>
 * 
 * <p>Description: 关于模板的操作</p>
 * 
 * <p>Date: Feb 12, 2012</p>
 * 
 * <p>Time: 8:59:49 PM</p>
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
public class TempletAction extends BaseAction{

	private TempletService templetService;
	private TempletChannelService templetChannelService;
	private TempletLinkService templetLinkService;
	private SiteService siteService;
	
	private List<Templet> templetList;
	private List<TempletChannel> templetChanneList;
	private List<File> sonFiles;
	
	private Templet templet;
	
	private String order="ordernum";
	private String logContent;
	private String ids;
	private String root;
	private String onclick;
	private String rootHasSon;
	private String filePaths;
	private String currFolder;
	private File currFile;
	private String fileName;
	private String fileExt;
	private String fileContent;
	private String msg;
	private Site site;
	private File uploadFile;
	private String uploadFileFileName;
	private String type;
	private String inputid;
	
	
	
	public TempletAction(){
		init("templetService","siteService");
	}
	/**
	 * 处理ajax上传文件
	 * @return
	 */
	public String uploadFile(){
		if (uploadFile!=null) {
			try {
				root=URLDecoder.decode(root,"utf-8");
				//生成目标文件
				String ext=FileUtil.getExt(uploadFileFileName).toLowerCase();
				String allowExts=",html,css,js,bmp,gif,jpeg,jpg,png swf,flv,7z,aiff,asf,avi,bmp,csv,doc,docx,fla,flv,gif,gz,gzip,jpeg,jpg,mid,mov,mp3,mp4,mpc,mpeg,mpg,ods,odt,pdf,png,ppt,pptx,pxd,qt,ram,rar,rm,rmi,rmvb,rtf,sdc,sitd,swf,sxc,sxw,tar,tgz,tif,tiff,txt,vsd,wav,wma,wmv,xls,xlsx,zip";
				if (allowExts.indexOf(ext.replace(".", ""))<0) {
					write("{error:'只能上传"+allowExts+"格式的文件!',msg:''}", "GBK");
					return null;
				}
				File targetFile=new File(root+"/"+uploadFileFileName);
				if (!targetFile.exists()) {
					try {
						targetFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					write("{msg:'',error:'此文件已存在'}", "GBK");
					return null;
				}
				//复制到目标文件
				FileUtil.copy(uploadFile, targetFile);
				//生成访问地址
				write("{msg:'"+(targetFile.getPath().replace(getHttpRequest().getRealPath("/"), "").replace("\\", "/"))+"',error:''}", "GBK");
			} catch (Exception e) {
				e.printStackTrace();
				write("{msg:'',error:'上传失败!'}", "GBK");
			}
		}else {
			write("{msg:'',error:''}", "GBK");
		}
		return null;
	}
	/**
	 * 选择模板文件
	 * @return
	 */
	public String selectFile(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
			//如果有site.id参数则查询站点的模板
			site = siteService.findById(site.getId());
			if (site!=null) {
				if (templet==null) {
					templet=new Templet();
				}
				templet.setId(site.getIndextemplet());
			}
		}
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
			//判断根目录有无子文件夹
			if (FileUtil.hasSonFolder(getHttpRequest().getRealPath("/")+"/templet/"+templet.getId())) {
				rootHasSon="1";
			}
		}
		return "selectFile";
	}
	/**
	 * 选择模板
	 * @return
	 */
	public String select(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0) {
			//站点编辑时
			site=siteService.findById(site.getId());
			if (site!=null) {
				if (templet==null) {
					templet=new Templet();
				}
				/*if (!isAdminLogin()) {
					templet.setAdduser(getLoginAdmin().getId());
				}*/
				templet.setUsesites(site.getId());
				templet.setNoDel("1");
				templetList=templetService.findAll(templet, order);
			}
		}else {
			//添加新站点时
			if (templet==null) {
				templet=new Templet();
			}
			/*if (!isAdminLogin()) {
				templet.setAdduser(getLoginAdmin().getId());
			}*/
			templet.setUsesites("newSite");
			templet.setNoDel("1");
			templetList=templetService.findAll(templet, order);
		}
		return "select";
	}
	/**
	 * 文件夹删除 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String folderDel() throws UnsupportedEncodingException{
		if (filePaths!=null && filePaths.trim().length()>0) {
			try {
				FileUtil.del(URLDecoder.decode(filePaths,"utf-8"));
				logContent="删除模板文件夹("+URLDecoder.decode(filePaths,"utf-8").replace(getHttpRequest().getRealPath("/"), "")+")成功!";
				msg="succ";
			} catch (Exception e) {
				DBProException(e);
				msg="操作失败";
				logContent="删除模板文件夹("+URLDecoder.decode(filePaths,"utf-8").replace(getHttpRequest().getRealPath("/"), "")+")失败:"+e.toString()+"!";
			}
			OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
			write(msg, "GBK");
		}
		return  null;
	}
	/**
	 * 文件夹编辑处理
	 * @return
	 */
	public String folderEditDo(){
		String oper="创建";
		try {
			if (filePaths!=null && filePaths.trim().length()>0) {
				oper="重命名";
				currFile=new File(filePaths);
				if (currFile.exists()) {
					//存在则更新
					//判断是否重命名
					if (!currFile.getName().equals(fileName)) {
						//判断文件是否存在，如存在则不能保存
						String string=currFile.getPath().replace("\\", "/").substring(0,currFile.getPath().replace("\\", "/").lastIndexOf("/")+1);
						File newFile=new File(currFile.getPath().replace("\\", "/").substring(0,currFile.getPath().replace("\\", "/").lastIndexOf("/")+1)+"/"+fileName);
						if (newFile.exists()) {
							write("此文件夹名已存在", "UTF-8");
							return null;
						}
						//重命名需要删除原来名称的文件
						currFile.renameTo(newFile);
					}
				}
			}else {
				//添加
				if (currFolder!=null && currFolder.trim().length()>0 
						&& fileName!=null && fileName.trim().length()>0) {
					//判断文件是否存在，如存在则不能保存
					currFile=new File(URLDecoder.decode(currFolder)+"/"+fileName);
					if (currFile.exists()) {
						write("此文件夹名已存在", "UTF-8");
						return null;
					}
					currFile.mkdir();
				}
			}
			logContent=oper+"模板文件夹("+(currFile!=null?currFile.getPath().replace("\\", "/").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), ""):"")+")成功!";
			write("succ", "GBK");
		} catch (Exception e) {
			DBProException(e);
			write("操作失败", "UTF-8");
			logContent=oper+"模板文件夹("+(currFile!=null?currFile.getPath().replace("\\", "/").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), ""):"")+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}
	/**
	 * 文件夹名称编辑页面
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String folderEdit() throws UnsupportedEncodingException{
		if (filePaths!=null && filePaths.trim().length()>0) {
			currFile=new File(URLDecoder.decode(filePaths,"UTF-8"));
			if (currFile!=null) {
				fileName=currFile.getName();
			}
		}
		return "folderEdit";
	}
	/**
	 * 文件编辑页面
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String fileEdit() throws UnsupportedEncodingException{
		if (filePaths!=null && filePaths.trim().length()>0) {
			currFile=new File(URLDecoder.decode(filePaths,"UTF-8"));
			if (currFile!=null) {
				fileName=currFile.getName().substring(0,currFile.getName().lastIndexOf("."));
				fileExt=currFile.getName().substring(currFile.getName().lastIndexOf("."));
				fileContent=HtmlCode.encode(FileUtil.readFile(currFile));
			}
		}
		return "fileEdit";
	}
	/**
	 * 文件编辑处理
	 * @return
	 */
	public String fileEditDo(){
		String oper="添加";
		try {
			if (filePaths!=null && filePaths.trim().length()>0) {
				oper="修改";
				currFile=new File(filePaths);
				if (currFile.exists()) {
					//存在则更新
					//判断是否重命名
					if (!currFile.getName().equals(fileName+fileExt)) {
						//判断文件是否存在，如存在则不能保存
						File newFile=new File(URLDecoder.decode(currFolder)+"/"+fileName+fileExt);
						if (newFile.exists()) {
							write("<script>alert('此文件名已存在');history.back();</script>", "GBK");
							return null;
						}
						//重命名需要删除原来名称的文件
						//FileUtil.deleteFile(currFile);
						
						currFile.renameTo(newFile);
						currFile=new File(URLDecoder.decode(currFolder)+"/"+fileName+fileExt);
					}
				}
				FileUtil.writeFile(currFile, fileContent);
			}else {
				//添加
				if (currFolder!=null && currFolder.trim().length()>0 
						&& fileName!=null && fileName.trim().length()>0) {
					//判断文件是否存在，如存在则不能保存
					currFile=new File(URLDecoder.decode(currFolder)+"/"+fileName+fileExt);
					if (currFile.exists()) {
						write("<script>alert('此文件名已存在');history.back();</script>", "GBK");
						return null;
					}
					//保存文件
					FileUtil.writeFile(currFile, fileContent);
				}
			}
			logContent=oper+"模板文件("+(currFile!=null?currFile.getPath().replace("\\", "/").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), ""):"")+")成功!";
			write("<script>alert('操作成功');location.href='templet_fileSon.do?root="+URLEncoder.encode((currFile!=null?currFile.getPath().replace("\\", "/").substring(0, currFile.getPath().replace("\\", "/").lastIndexOf("/")):""),"utf-8")+"';</script>", "GBK");
		} catch (Exception e) {
			DBProException(e);
			write("<script>alert('操作失败');history.back();</script>", "GBK");
			logContent=oper+"模板文件("+(currFile!=null?currFile.getPath().replace("\\", "/").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), ""):"")+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}
	/**
	 * 文件管理页面
	 * @return
	 */
	public String fileManage(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
			//判断根目录有无子文件夹
			if (FileUtil.hasSonFolder(getHttpRequest().getRealPath("/")+"/templet/"+templet.getId())) {
				rootHasSon="1";
			}
		}
		return "fileManage";
	}
	/**
	 * 查询子文件夹
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String folderSon() throws UnsupportedEncodingException{
		if (root!=null && root.trim().length()>0) {
			root=URLDecoder.decode(root,"utf-8");
			List<File> sonFiles=FileUtil.getFolders(root);
			//生成树
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("[");
			if (sonFiles!=null && sonFiles.size()>0) {
				for (int i = 0; i < sonFiles.size(); i++) {
					if (sonFiles.get(i).isDirectory()) {
						if (!"[".equals(stringBuilder.toString())) {
							stringBuilder.append(",");
						}
						stringBuilder.append("{ \"text\": \"<a  onclick=");
						if (onclick!=null && onclick.trim().length()>0) {
							stringBuilder.append(onclick);
						}else {
							stringBuilder.append("showOne");
						}
						stringBuilder.append("('");
						stringBuilder.append(URLEncoder.encode(sonFiles.get(i).getPath().replace("\\", "/"), "UTF-8"));
						stringBuilder.append("')>");
						stringBuilder.append(sonFiles.get(i).getName());
						stringBuilder.append("</a>\", \"hasChildren\": ");
						if (FileUtil.hasSonFolder(sonFiles.get(i))) {
							stringBuilder.append("true");
						}else {
							stringBuilder.append("false");
						}
						stringBuilder.append(",\"id\":\"");
						stringBuilder.append(URLEncoder.encode(sonFiles.get(i).getPath().replace("\\", "/"), "UTF-8"));
						stringBuilder.append("\" }");
					}
				}
			}
			stringBuilder.append("]");
			write(stringBuilder.toString(), "UTF-8");
		}
		return null;
	}
	/**
	 * 获取目录下所有文件
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String fileSon() throws UnsupportedEncodingException{
		if (root!=null && root.trim().length()>0) {
			root=URLDecoder.decode(root,"utf-8");
			String realPath=getHttpRequest().getRealPath("/").replace("\\", "/");
			currFolder=root.replace(realPath+"templet/", "");
			if (currFolder.indexOf("/")>-1) {
				currFolder=currFolder.substring(currFolder.indexOf("/"));
			}else {
				currFolder="根目录";
			}
			sonFiles=FileUtil.getFiles(root);
		}
		if ("select".equals(type)) {
			return "fileSonSelect";
		}
		return "fileSon";
	}
	/**
	 * 删除文件
	 * @throws UnsupportedEncodingException 
	 */
	public String fileDel() throws UnsupportedEncodingException {
		if (filePaths!=null && filePaths.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] filePathArr=filePaths.split(";");
			if (filePathArr!=null && filePathArr.length>0) {
				for (int i = 0; i < filePathArr.length; i++) {
					if (filePathArr[i].trim().length()>0) {
						try {
							FileUtil.del(URLDecoder.decode(filePathArr[i],"utf-8"));
							sb.append(URLEncoder.encode(filePathArr[i],"utf-8")+";");
							logContent="删除模板文件("+URLDecoder.decode(filePathArr[i],"utf-8").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), "")+")成功!";
						} catch (Exception e) {
							DBProException(e);
							logContent="删除模板文件("+URLDecoder.decode(filePathArr[i],"utf-8").replace(getHttpRequest().getRealPath("/").replace("\\", "/"), "")+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "GBK");
		}
		return null;
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (templet==null) {
			templet=new Templet();
		}
		templet.setNoDel("1");
		//普通用户只查看自己添加的
		if (!isAdminLogin()) {
			templet.setAdduser(getLoginAdmin().getId());
		}
		if (StringUtils.isEmpty(order)) {
			order="ordernum";
		}
		templetList=templetService.find(templet, order, currPage, pageSize);
		totalCount=templetService.count(templet);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("templet.name");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("templet_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
		}
		return "edit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		String oper="添加";
		try {
			if (templet!=null && templet.getId()!=null) {
				Templet  oldTemplet=templetService.findById(templet.getId());
				if (oldTemplet!=null) {
					oldTemplet.setName(templet.getName());
					oldTemplet.setOrdernum(templet.getOrdernum());
					oldTemplet.setState(templet.getState());
					oldTemplet.setUsesitenames(templet.getUsesitenames());
					oldTemplet.setUsesites(templet.getUsesites());
					oper="修改";
					templetService.update(oldTemplet);
				}
			}else {
				//添加
				templet.setAdduser(getLoginAdmin().getId());
				templetService.add(templet);
				//生成模板目录
				String realPath=getHttpRequest().getRealPath("/");
				if (uploadFile!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(uploadFileFileName).toLowerCase();
					if (!".zip".equals(ext)) {
						msg="<script>alert('请上传格式为zip的压缩文件!');history.back();</script>";
						return "msg";
					}
					File targetFile=new File(realPath+"/templet/"+templet.getId()+ext);
					File folder=new File(realPath+"/templet/"+templet.getId()+"/");
					if (!folder.exists()) {
						folder.mkdirs();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(uploadFile, targetFile);
					//解压
					ZipTools.unZip(realPath+"/templet/"+templet.getId()+ext, realPath+"/templet/"+templet.getId()+"/");
					//判断是否有初始化数据
					//栏目数据
					init("templetChannelService");
					templetChannelService.importChannels(templet, getHttpRequest());
					//链接分类数据
					init("templetLinkService");
					templetLinkService.importLinks(templet, getHttpRequest());
				}else {
					FileUtil.copyDirectiory(realPath+"/templet/default", realPath+"/templet/"+templet.getId());
				}
			}
			logContent=oper+"模板("+templet.getName()+")成功!";
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"模板("+templet.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		if ("修改".equals(oper)) {
			write("succ", "GBK");
			return null;
		}else {
			return showMessage("添加模板成功", "templet_list.do?pageFuncId="+pageFuncId, 3);
		}
	}

	/**
	 * 删除
	 * @return
	 */
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							templet=templetService.findById(idArr[i]);
							if (templet!=null) {
								templet.setState(Templet.STATE_DEL);
								templetService.update(templet);
								sb.append(idArr[i]+";");
								logContent="删除模板("+templet.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除模板("+templet.getName()+")失败:"+e.toString()+"!";
						}
						OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
					}
				}
			}
			write(sb.toString(), "GBK");
		}
		return null;
	}

	/**
	 * 初始化数据页面
	 * @return
	 */
	public String data(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
			init("templetChannelService");
			//栏目管理页面
			//获取一级栏目 
			templetChanneList=templetChannelService.findByPar(templet.getId(), "par");
			//设置是否有子栏目
			if (templetChanneList!=null && templetChanneList.size()>0) {
				for (int i = 0; i < templetChanneList.size(); i++) {
					if (templetChannelService.hasChildren(templetChanneList.get(i).getId())) {
						templetChanneList.get(i).setHasChildren("1");
					}
				}
			}
		}
		return "data";
	}
	/**
	 * 导出
	 * @return
	 */
	public String export(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
			if (templet!=null) {
				//生成zip文件
				try {
					//生成模板栏目xml文件
					init("templetChannelService");
					templetChannelService.createXML(templet, getHttpRequest());
					//生成模板链接xml文件
					init("templetLinkService");
					templetLinkService.createXML(templet, getHttpRequest());
					ZipTools.zip(getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/", 
							getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+".zip");
					msg="<script>history.back();window.open('../../templet/"+templet.getId()+".zip');</script>";
					return "msg";
				} catch (Exception e) {
					e.printStackTrace();
					showMessage="导出失败:"+e.getMessage();
				}
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	//set and get
	public TempletService getTempletService() {
		return templetService;
	}

	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}


	public List<Templet> getTempletList() {
		return templetList;
	}


	public void setTempletList(List<Templet> templetList) {
		this.templetList = templetList;
	}


	public Templet getTemplet() {
		return templet;
	}


	public void setTemplet(Templet templet) {
		this.templet = templet;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	public String getIds() {
		return ids;
	}


	public void setIds(String ids) {
		this.ids = ids;
	}


	public List<File> getSonFiles() {
		return sonFiles;
	}

	public void setSonFiles(List<File> sonFiles) {
		this.sonFiles = sonFiles;
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

	public String getRootHasSon() {
		return rootHasSon;
	}

	public void setRootHasSon(String rootHasSon) {
		this.rootHasSon = rootHasSon;
	}

	public String getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String filePaths) {
		this.filePaths = filePaths;
	}

	public String getCurrFolder() {
		return currFolder;
	}

	public void setCurrFolder(String currFolder) {
		this.currFolder = currFolder;
	}

	public File getCurrFile() {
		return currFile;
	}

	public void setCurrFile(File currFile) {
		this.currFile = currFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public SiteService getSiteService() {
		return siteService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public String getFileExt() {
		return fileExt;
	}
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
	public File getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}
	public String getUploadFileFileName() {
		return uploadFileFileName;
	}
	public void setUploadFileFileName(String uploadFileFileName) {
		this.uploadFileFileName = uploadFileFileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInputid() {
		return inputid;
	}
	public void setInputid(String inputid) {
		this.inputid = inputid;
	}
	public TempletChannelService getTempletChannelService() {
		return templetChannelService;
	}
	public void setTempletChannelService(TempletChannelService templetChannelService) {
		this.templetChannelService = templetChannelService;
	}
	public List<TempletChannel> getTempletChanneList() {
		return templetChanneList;
	}
	public void setTempletChanneList(List<TempletChannel> templetChanneList) {
		this.templetChanneList = templetChanneList;
	}
	public TempletLinkService getTempletLinkService() {
		return templetLinkService;
	}
	public void setTempletLinkService(TempletLinkService templetLinkService) {
		this.templetLinkService = templetLinkService;
	}
}
