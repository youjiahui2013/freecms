package cn.freeteam.cms.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.InfoImg;
import cn.freeteam.cms.model.InfoSign;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoImgService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.InfoSignService;
import cn.freeteam.cms.service.RoleChannelService;
import cn.freeteam.cms.service.SensitiveService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.model.Roles;
import cn.freeteam.model.Users;
import cn.freeteam.service.UserService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.HtmlCode;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;


/**
 * 
 * <p>Title: InfoAction.java</p>
 * 
 * <p>Description: 信息相关操作</p>
 * 
 * <p>Date: Feb 1, 2012</p>
 * 
 * <p>Time: 3:19:27 PM</p>
 * 
 * <p>Copyright: 2012</p>
 * 
 * <p>Company:  freeteam</p>
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
public class InfoAction extends BaseAction{
	
	private SiteService siteService;
	private ChannelService channelService;
	private InfoService infoService;
	private RoleChannelService roleChannelService;
	private UserService userService;
	private InfoSignService infoSignService;
	private SensitiveService sensitiveService;
	private InfoImgService infoImgService;
	
	private List<Site> siteList;
	private List<Channel> channelList;
	private List<Info> infoList;
	private List<Users> userList;
	private List<InfoSign> infosignList;
	private List<InfoImg> infoImgList;
	
	
	private Info info;
	private Site site;
	private Channel channel;
	private String oldchannelid;
	private String tochannelid;
	private File videoUpload;
	private String videoUploadFileName;
	private String delOldimgs;
	
	private String order=" addtime desc ";
	private String listPageFuncId;
	private String ids;
	private String logContent;
	private String type;
	private String[] signusers;
	private String msg;
	private String channelname;
	private String sitename;
	

	private String htmlChannel;
	private String htmlChannelOld;
	private String htmlChannelPar;
	private String htmlIndex;
	
	public InfoAction(){
		init("siteService","channelService","infoService",
				"roleChannelService","userService");
	}
	/**
	 * 信息列表
	 * @return
	 */
	public String list(){
		if (info==null) {
			info=new Info();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		if (info.getChannel()!=null && info.getChannel().length()>0) {
			channel=channelService.findById(info.getChannel());
		}
		infoList=infoService.find(info, order, currPage, pageSize);
		totalCount=infoService.count(info);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("info.channel");
		pager.appendParam("info.issign");
		pager.appendParam("info.iscomment");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("info_list.do");
		pageStr=pager.getOutStr();
		return "list";
	}
	/**
	 * 管理页面
	 * @return
	 */
	public String info(){

		site=getManageSite();
		if (site!=null) {
			//提取站点信息和此站点的一级栏目 
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
		}
		return "info";
	}
	/**
	 * 信息提取
	 * @return
	 */
	public String extract(){
		if (info==null) {
			info=new Info();
		}
		if (order.trim().length()==0) {
			order=" addtime desc ";
		}
		infoList=infoService.find(info, order, currPage, pageSize);
		totalCount=infoService.count(info);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("info.channel");
		pager.appendParam("info.site");
		pager.appendParam("channelname");
		pager.appendParam("sitename");
		pager.appendParam("info.searchKey");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStr("info_extract.do");
		pageStr=pager.getOutStr();
		return "extract";
	}
	/**
	 * 信息提取处理
	 * @return
	 */
	public String extractDo(){
		if (info!=null && info.getChannel()!=null && info.getChannel().trim().length()>0
				&& ids!=null && ids.trim().length()>0) {
			channel=channelService.findById(info.getChannel().trim());
			if (channel!=null) {
				try {
					String[] idArr=ids.split(";");
					if (idArr!=null && idArr.length>0) {
						init("infoImgService","infoSignService");
						for (int i = 0; i < idArr.length; i++) {
							if (idArr[i].trim().length()>0) {
								info=infoService.findById(idArr[i].trim());
								if (info!=null) {
									//复制到新栏目
									if (info!=null) {
										info.setChannel(channel.getId());
										info.setId("");
										info.setSite(channel.getSite());
										infoService.insert(info);

										//处理图片集
										List<InfoImg> infoImgList = infoImgService.findByInfoid(idArr[i]);
										if (infoImgList!=null && infoImgList.size()>0) {
											for (int j = 0; j < infoImgList.size(); j++) {
												infoImgList.get(j).setId("");
												infoImgList.get(j).setInfoid(info.getId());
												infoImgService.add(infoImgList.get(j));
											}
										}
										//处理签收
										List<InfoSign> infoSignList = infoSignService.findByInfo(idArr[i]);
										if (infoSignList!=null && infoSignList.size()>0) {
											for (int j = 0; j < infoSignList.size(); j++) {
												infoSignList.get(j).setId("");
												infoSignList.get(j).setInfoid(info.getId());
												infoSignService.save(infoSignList.get(j));
											}
										}
										infoService.html(info.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
										logContent="提取信息 "+info.getTitle()+" 到 "+channel.getName()+" "+info.getTitle()+")成功!";
									}
								}
							}
						}
					}
					boolean ismakehtml=true;
					if (channel!=null) {
						site=siteService.findById(channel.getSite());
						if ("1".equals(channel.getHtmlchannel())) {
							//所属栏目静态化
							channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
							ismakehtml=false;
						}
						if ("1".equals(channel.getHtmlchannelold())) {
							//原所属栏目静态化
							if (oldchannelid!=null && oldchannelid.trim().length()>0 && !oldchannelid.equals(info.getChannel())) {
								Channel oldchannel=channelService.findById(oldchannelid);
								channelService.html(site, oldchannel, getServletContext(), getHttpRequest(), getLoginName(), 0);
								ismakehtml=false;
							}
						}
						if ("1".equals(channel.getHtmlparchannel())) {
							//所属栏目的父栏目静态化
							List<Channel> channeList = channelService.findPath(info.getChannel());
							if (channeList!=null && channeList.size()>0) {
								for (int i = 0; i < channeList.size(); i++) {
									if (!channeList.get(i).getId().equals(info.getChannel())) {
										channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
									}
								}
							}
							ismakehtml=false;
						}
						if ("1".equals(channel.getHtmlsite())) {
							//首页静态化
							siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
							ismakehtml=false;
						}
					}
					if (ismakehtml) {
						return "extractMakehtml";
					}else {
						showMessage="提取成功";
					}
				} catch (Exception e) {
					e.printStackTrace();
					showMessage="操作失败:"+e.getMessage();
					logContent="提取信息失败:"+e.getMessage();
				}
			}else {
				showMessage="没有找到要提取到的栏目";
			}
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		site=getManageSite();
		if (site!=null) {
			if (!isAdminLogin() && !isSiteAdmin()) {
				//普通用户只提取自己有权限的栏目
				channelList=channelService.findByRoles(site.getId(), getLoginRoleIdsSql());
			}
			//编辑
			if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
				info=infoService.findById(info.getId());
				channel=channelService.findById(info.getChannel());
				init("infoSignService");
				//查询签收用户
				infosignList=infoSignService.findByInfo(info.getId());
				//查询图片集
				init("infoImgService");
				InfoImg infoImg=new InfoImg();
				infoImg.setInfoid(info.getId());
				infoImgList=infoImgService.find(infoImg, " ordernum ");
			}
			//添加,传递参数channel.id
			if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
				channel=channelService.findById(channel.getId());
			}
			//查询所有用户，签收时使用
			userList=userService.findAll();
		}
		return "edit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		site=getManageSite();
		if (info!=null) {
			String oper="添加";
			try {
				//敏感词处理
				init("sensitiveService");
				info.setTitle(sensitiveService.replace(info.getTitle()));
				info.setShorttitle(sensitiveService.replace(info.getShorttitle()));
				info.setContent(sensitiveService.replace(info.getContent()));
				info.setDescription(sensitiveService.replace(info.getDescription()));
				if (videoUpload!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(videoUploadFileName).toLowerCase();
					if (!".flv".equals(ext)) {
						msg="<script>alert('只能上传flv格式的视频!');history.back();</script>";
						return "msg";
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
					FileUtil.copy(videoUpload, targetFile);

					//生成访问地址
					info.setVideo("/upload/"+site.getId()+"/"+id+ext);
				}
				if (info.getImg()==null || info.getImg().trim().length()==0) {
					//如果没有选择信息图片，则检查信息内容中是否有图片
					try {
						Parser parser=new Parser(info.getContent());
						NodeFilter filter = new TagNameFilter ("img");
			            NodeList nodes = parser.extractAllNodesThatMatch(filter);
			            Node eachNode = null;
			            ImageTag imageTag = null;
			            if (nodes != null && nodes.size()>0)
			            {
			            	//遍历所有的img节点
			                for (int i = 0; i < nodes.size(); i++) 
			                {
			    				if (info.getImg()==null || info.getImg().trim().length()==0) {
				                    eachNode = (Node)nodes.elementAt(i);
				                    if (eachNode instanceof ImageTag) 
				                    {
				                        imageTag = (ImageTag)eachNode;
				                        info.setImg(imageTag.getAttribute("src"));
				                    }
			    				}else {
									break;
								}
			                }
			            }
					} catch (ParserException e) {
						e.printStackTrace();
					}
				}
				//处理图片集
				Enumeration<String> paramNames = getHttpRequest().getParameterNames();
				String paramName,imgsid;
				List<InfoImg> infoImgList=new ArrayList<InfoImg>();
				List<InfoImg> oldInfoImgList=new ArrayList<InfoImg>();
				while (paramNames.hasMoreElements()) {
					paramName=paramNames.nextElement();
					if (paramName.startsWith("imgsurl")) {
						imgsid=paramName.replace("imgsurl", "");
						InfoImg infoImg=new InfoImg();
						infoImg.setImg(getHttpRequest().getParameter("imgsurl"+imgsid));
						infoImg.setContent(getHttpRequest().getParameter("imgscontent"+imgsid));
						try {
							infoImg.setOrdernum(Integer.parseInt(getHttpRequest().getParameter("imgsordernum"+imgsid)));
						} catch (Exception e) {
						}
						infoImg.setTitle(getHttpRequest().getParameter("imgstitle"+imgsid));
						infoImgList.add(infoImg);
						//如果没有选择信息图片，使用图片集中的第一张图片
	    				if (info.getImg()==null || info.getImg().trim().length()==0) {
	    					info.setImg(infoImg.getImg());
	    				}
					}
					if (paramName.startsWith("oldimgsid")) {
						//需要更新的图片
						imgsid=paramName.replace("oldimgsid", "");
						InfoImg infoImg=new InfoImg();
						infoImg.setId(imgsid);
						infoImg.setInfoid(info.getId());
						infoImg.setImg(getHttpRequest().getParameter("oldimgsurl"+imgsid));
						infoImg.setContent(getHttpRequest().getParameter("oldimgscontent"+imgsid));
						try {
							infoImg.setOrdernum(Integer.parseInt(getHttpRequest().getParameter("oldimgsordernum"+imgsid)));
						} catch (Exception e) {
						}
						infoImg.setTitle(getHttpRequest().getParameter("oldimgstitle"+imgsid));
						oldInfoImgList.add(infoImg);
					}
				}
				//如果没有摘要，则自动生成
				if (StringUtils.isEmpty(info.getDescription())) {
					info.setDescription(HtmlCode.replaceHtml(info.getContent()));
					if (info.getDescription().length()>500) {
						info.setDescription(info.getDescription().substring(0,500));
					}
				}
				info.setIsimgs("0");
				if (info.getId()!=null && info.getId().trim().length()>0) {
					//更新
					oper="更新";
					Info oldInfo=infoService.findById(info.getId());
					if (oldInfo!=null) {
						oldInfo.setAuthor(info.getAuthor());
						oldInfo.setAddtime(info.getAddtime());
						oldInfo.setAttchs(info.getAttchs());
						oldInfo.setChannel(info.getChannel());
						oldInfo.setContent(info.getContent());
						oldInfo.setDescription(info.getDescription());
						oldInfo.setImg(info.getImg());
						oldInfo.setIstop(info.getIstop());
						oldInfo.setIshot(info.getIshot());
						oldInfo.setShorttitle(info.getShorttitle());
						oldInfo.setSite(info.getSite());
						oldInfo.setSource(info.getSource());
						oldInfo.setTags(info.getTags());
						oldInfo.setTemplet(info.getTemplet());
						oldInfo.setTitle(info.getTitle());
						oldInfo.setTitleblod(info.getTitleblod());
						oldInfo.setTitlecolor(info.getTitlecolor());
						oldInfo.setTopendtime(info.getTopendtime());
						oldInfo.setUrl(info.getUrl());
						oldInfo.setIssign(info.getIssign());
						oldInfo.setIscomment(info.getIscomment());
						oldInfo.setVideo(info.getVideo());
						oldInfo.setOpenendtime(info.getOpenendtime());
						oldInfo.setOpentimetype(info.getOpentimetype());
						oldInfo.setOpentype(info.getOpentype());
						oldInfo.setIndexnum(info.getIndexnum());
						infoService.update(oldInfo);
						OperLogUtil.log(getLoginName(), oper+"信息("+oldInfo.getTitle()+")成功", getHttpRequest());
						init("infoImgService");
						//删除图片集
						if (StringUtils.isNotEmpty(delOldimgs)) {
							String dels[]=delOldimgs.split(";");
							if (dels!=null && dels.length>0) {
								for (int i = 0; i < dels.length; i++) {
									if (dels[i].trim().length()>0) {
										infoImgService.del(dels[i]);
									}
								}
							}
						}
						if (oldInfoImgList.size()>0) {
							for (int i = 0; i < oldInfoImgList.size(); i++) {
								infoImgService.update(oldInfoImgList.get(i));
							}
						}
						//更新图片集
					}
				}else{
					//添加
					if (info.getAddtime()==null) {
						info.setAddtime(new Date());
					}
					info.setAdduser(getLoginAdmin().getId());
					info.setClicknum(0);
					infoService.insert(info);
					OperLogUtil.log(getLoginName(), oper+"信息("+info.getTitle()+")成功", getHttpRequest());
				}
				//处理签收用户
				init("infoSignService");
				infoSignService.infoedit(info.getId(), signusers);
				//处理图片集
				init("infoImgService");
				if (infoImgList.size()>0) {
					for (int i = 0; i < infoImgList.size(); i++) {
						infoImgList.get(i).setInfoid(info.getId());
						infoImgService.add(infoImgList.get(i));
					}
				}
				
				//查询图片集
				InfoImg infoImg=new InfoImg();
				infoImg.setInfoid(info.getId());
				if (infoImgService.count(infoImg)>0) {
					info=infoService.findById(info.getId());
					info.setIsimgs("1");
					infoService.update(info);
				}
				//生成静态页面
				infoService.html(info.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
				//检查此信息所属栏目是否设置当此栏目中的信息变更后需要进行的静态化处理
				channel=channelService.findById(info.getChannel());
				boolean ismakehtml=true;
				if (channel!=null) {
					site=siteService.findById(info.getSite());
					if ("1".equals(channel.getHtmlchannel())) {
						//所属栏目静态化
						channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
						ismakehtml=false;
					}
					if ("1".equals(channel.getHtmlchannelold())) {
						//原所属栏目静态化
						if (oldchannelid!=null && oldchannelid.trim().length()>0 && !oldchannelid.equals(info.getChannel())) {
							Channel oldchannel=channelService.findById(oldchannelid);
							channelService.html(site, oldchannel, getServletContext(), getHttpRequest(), getLoginName(), 0);
							ismakehtml=false;
						}
					}
					if ("1".equals(channel.getHtmlparchannel())) {
						//所属栏目的父栏目静态化
						List<Channel> channeList = channelService.findPath(info.getChannel());
						if (channeList!=null && channeList.size()>0) {
							for (int i = 0; i < channeList.size(); i++) {
								if (!channeList.get(i).getId().equals(info.getChannel())) {
									channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
								}
							}
						}
						ismakehtml=false;
					}
					if ("1".equals(channel.getHtmlsite())) {
						//首页静态化
						siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
						ismakehtml=false;
					}
				}
				if (ismakehtml) {
					return "makehtml";
				}else {
					if ("channel".equals(type)) {
						write("<script>alert('操作成功');location.href='info_list.do?info.channel="+info.getChannel()+"&pageFuncId="+pageFuncId+"';</script>", "GBK");
					}else {
						write("<script>alert('操作成功');location.href='info_edit.do?pageFuncId="+pageFuncId+"';</script>", "GBK");
					}
				}
			} catch (Exception e) {
				DBProException(e);
				OperLogUtil.log(getLoginName(), oper+"信息("+info.getTitle()+")失败:"+e.toString(), getHttpRequest());
				showMessage="操作失败:"+e.toString();
				return showMessage(showMessage, forwardUrl, forwardSeconds);
			}
		}
		return null;
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
				try {
					for (int i = 0; i < idArr.length; i++) {
						if (idArr[i].trim().length()>0) {
							info=infoService.findById(idArr[i]);
							if (info!=null) {
								infoService.delhtml(idArr[i], getHttpRequest());
								infoService.del(idArr[i]);
								sb.append(idArr[i]+";");
								logContent="删除信息("+info.getTitle()+")成功!";
							}
							OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
						}
					}
					if (info!=null) {
						//检查此信息所属栏目是否设置当此栏目中的信息变更后需要进行的静态化处理
						channel=channelService.findById(info.getChannel());
						if (channel!=null) {
							site=siteService.findById(info.getSite());
							if ("1".equals(channel.getHtmlchannel())) {
								//所属栏目静态化
								channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
							}
							if ("1".equals(channel.getHtmlparchannel())) {
								//所属栏目的父栏目静态化
								List<Channel> channeList = channelService.findPath(info.getChannel());
								if (channeList!=null && channeList.size()>0) {
									for (int i = 0; i < channeList.size(); i++) {
										if (!channeList.get(i).getId().equals(info.getChannel())) {
											channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
										}
									}
								}
							}
							if ("1".equals(channel.getHtmlsite())) {
								//首页静态化
								siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
							}
						}
					}
				} catch (Exception e) {
					DBProException(e);
					logContent="删除信息("+info.getTitle()+")失败:"+e.toString()+"!";
					OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
				}
			}
			write(sb.toString(), "GBK");
		}
		return null;
	}

	/**
	 * 移动
	 * @return
	 */
	public String move(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0 &&
					oldchannelid!=null && tochannelid!=null && !oldchannelid.equals(tochannelid)) {
				Channel oldChannel=channelService.findById(oldchannelid);
				Channel toChannel=channelService.findById(tochannelid);
				channel=toChannel;
					if (oldChannel!=null && toChannel!=null) {
						try {
							for (int i = 0; i < idArr.length; i++) {
								if (idArr[i].trim().length()>0) {
									info=infoService.findById(idArr[i]);
									if (info!=null) {
										info.setChannel(tochannelid);
										infoService.update(info);
										infoService.html(info.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
										sb.append(idArr[i]+";");
										logContent="移动信息("+oldChannel.getName()+" >> "+toChannel.getName()+" "+info.getTitle()+")成功!";
									}
									OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
								}
							}
							//处理静态化
							boolean ismakehtml=true;
							site=siteService.findById(info.getSite());
							boolean ishtmlsite=false;
							Map<String,Channel> channelHtmled=new HashMap<String,Channel>();
							//原栏目处理
							if (oldChannel!=null) {
								if ("1".equals(oldChannel.getHtmlchannel())) {
									//所属栏目静态化
									channelHtmled.put(oldChannel.getId(),oldChannel);
								}
								if ("1".equals(oldChannel.getHtmlparchannel())) {
									//所属栏目的父栏目静态化
									List<Channel> channeList = channelService.findPath(oldchannelid);
									if (channeList!=null && channeList.size()>0) {
										for (int i = 0; i < channeList.size(); i++) {
											if (!channeList.get(i).getId().equals(oldchannelid)) {
												channelHtmled.put(channeList.get(i).getId(),channeList.get(i));
											}
										}
									}
								}
								if ("1".equals(oldChannel.getHtmlsite())) {
									ishtmlsite=true;
								}
							}
							//新栏目处理
							if (toChannel!=null) {
								if ("1".equals(toChannel.getHtmlchannel())) {
									//所属栏目静态化
									channelHtmled.put(toChannel.getId(),toChannel);
								}
								if ("1".equals(toChannel.getHtmlparchannel())) {
									//所属栏目的父栏目静态化
									List<Channel> channeList = channelService.findPath(tochannelid);
									if (channeList!=null && channeList.size()>0) {
										for (int i = 0; i < channeList.size(); i++) {
											if (!channeList.get(i).getId().equals(tochannelid)) {
												channelHtmled.put(channeList.get(i).getId(),channeList.get(i));
											}
										}
									}
								}
								if ("1".equals(toChannel.getHtmlsite())) {
									ishtmlsite=true;
								}
							}
							//处理需要静态化的栏目
							if (channelHtmled!=null && channelHtmled.size()>0) {
								Iterator<String>  channelHtmls=channelHtmled.keySet().iterator();
								while (channelHtmls.hasNext()) {
									Channel channel = channelHtmled.get(channelHtmls.next());
									if (channel!=null) {
										channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
									}
								}
								ismakehtml=false;
							}
							if (ishtmlsite) {
								//首页静态化
								siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
								ismakehtml=false;
							}
							if (ismakehtml) {
								return "moveMakehtml";
							}else {
								showMessage="移动信息成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="移动信息("+oldChannel.getName()+" >> "+toChannel.getName()+" "+info.getTitle()+")失败:"+e.toString()+"!";
							OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
							showMessage="移动信息失败:"+e.getMessage();
						}
					}
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	/**
	 * 复制
	 * @return
	 */
	public String copy(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0 &&
					oldchannelid!=null && tochannelid!=null && !oldchannelid.equals(tochannelid)) {
				Channel oldChannel=channelService.findById(oldchannelid);
				Channel toChannel=channelService.findById(tochannelid);
				channel=toChannel;
					if (oldChannel!=null && toChannel!=null) {
						try {
							init("infoImgService","infoSignService");
							for (int i = 0; i < idArr.length; i++) {
								if (idArr[i].trim().length()>0) {
									info=infoService.findById(idArr[i]);
									if (info!=null) {
										info.setChannel(tochannelid);
										info.setId("");
										infoService.insert(info);
										//处理图片集
										List<InfoImg> infoImgList = infoImgService.findByInfoid(idArr[i]);
										if (infoImgList!=null && infoImgList.size()>0) {
											for (int j = 0; j < infoImgList.size(); j++) {
												infoImgList.get(j).setId("");
												infoImgList.get(j).setInfoid(info.getId());
												infoImgService.add(infoImgList.get(j));
											}
										}
										//处理签收
										List<InfoSign> infoSignList = infoSignService.findByInfo(idArr[i]);
										if (infoSignList!=null && infoSignList.size()>0) {
											for (int j = 0; j < infoSignList.size(); j++) {
												infoSignList.get(j).setId("");
												infoSignList.get(j).setInfoid(info.getId());
												infoSignService.save(infoSignList.get(j));
											}
										}
										infoService.html(info.getId(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
										sb.append(idArr[i]+";");
										logContent="复制信息("+oldChannel.getName()+" >> "+toChannel.getName()+" "+info.getTitle()+")成功!";
									}
									OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
								}
							}
							//处理静态化
							boolean ismakehtml=true;
							site=siteService.findById(info.getSite());
							//新栏目处理
							if (toChannel!=null) {
								if ("1".equals(toChannel.getHtmlchannel())) {
									//所属栏目静态化
									channelService.html(site, toChannel, getServletContext(), getHttpRequest(), getLoginName(), 0);
									ismakehtml=false;
								}
								if ("1".equals(toChannel.getHtmlparchannel())) {
									//所属栏目的父栏目静态化
									List<Channel> channeList = channelService.findPath(tochannelid);
									if (channeList!=null && channeList.size()>0) {
										for (int i = 0; i < channeList.size(); i++) {
											if (!channeList.get(i).getId().equals(tochannelid)) {
												channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
											}
										}
									}
									ismakehtml=false;
								}
								if ("1".equals(toChannel.getHtmlsite())) {
									//首页静态化
									siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
									ismakehtml=false;
								}
							}
							if (ismakehtml) {
								return "copyMakehtml";
							}else {
								showMessage="复制信息成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="复制信息("+oldChannel.getName()+" >> "+toChannel.getName()+" "+info.getTitle()+")失败:"+e.toString()+"!";
							OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
							showMessage="复制信息失败:"+e.getMessage();
						}
					}
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	/**
	 * 静态化处理
	 * @return
	 */
	public String makehtml(){
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			info=infoService.findById(info.getId());
			channel=channelService.findById(info.getChannel());
			site=siteService.findById(info.getSite());
			try {
				if ("1".equals(htmlChannelOld)) {
					Channel oldchannel=channelService.findById(oldchannelid);
					//原所属栏目静态化
					channelService.html(site, oldchannel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannel)) {
					//所属栏目静态化
					channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannelPar)) {
					//所属栏目的父栏目静态化
					List<Channel> channeList = channelService.findPath(info.getChannel());
					if (channeList!=null && channeList.size()>0) {
						for (int i = 0; i < channeList.size(); i++) {
							if (!channeList.get(i).getId().equals(info.getChannel())) {
								channelService.html(site, channeList.get(i), getServletContext(), getHttpRequest(), getLoginName(), 0);
							}
						}
					}
				}
				if ("1".equals(htmlIndex)) {
					//首页静态化
					siteService.html(info.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
				}
				showMessage="静态化处理成功!";
			} catch (Exception e) {
				e.printStackTrace();
				showMessage="静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
			}
		}
		return showMessage(showMessage, "", 0);
	}

	/**
	 * 提取信息后静态化处理
	 * @return
	 */
	public String extractMakehtml(){
		if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
			channel=channelService.findById(channel.getId());
			site=siteService.findById(channel.getSite());
			try {
				if ("1".equals(htmlChannel)) {
					//所属栏目静态化
					channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannelPar)) {
					//所属栏目的父栏目静态化
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
					siteService.html(channel.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
				}
				showMessage="静态化处理成功!";
			} catch (Exception e) {
				e.printStackTrace();
				showMessage="静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
			}
		}
		return showMessage(showMessage, "", 0);
	}

	/**
	 * 复制信息后静态化处理
	 * @return
	 */
	public String copyMakehtml(){
		if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
			channel=channelService.findById(channel.getId());
			site=siteService.findById(channel.getSite());
			try {
				if ("1".equals(htmlChannel)) {
					//所属栏目静态化
					channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannelPar)) {
					//所属栏目的父栏目静态化
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
					siteService.html(channel.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
				}
				showMessage="静态化处理成功!";
			} catch (Exception e) {
				e.printStackTrace();
				showMessage="静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
			}
		}
		return showMessage(showMessage, "", 0);
	}

	/**
	 * 移动信息后静态化处理
	 * @return
	 */
	public String moveMakehtml(){
		if (channel!=null && channel.getId()!=null && channel.getId().trim().length()>0) {
			channel=channelService.findById(channel.getId());
			site=siteService.findById(channel.getSite());
			try {
				if ("1".equals(htmlChannelOld)) {
					Channel oldchannel=channelService.findById(oldchannelid);
					//原所属栏目静态化
					channelService.html(site, oldchannel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannel)) {
					//所属栏目静态化
					channelService.html(site, channel, getServletContext(), getHttpRequest(), getLoginName(), 0);
				}
				if ("1".equals(htmlChannelPar)) {
					//所属栏目的父栏目静态化
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
					siteService.html(channel.getSite(), getServletContext(), getHttpRequest().getContextPath()+"/", getHttpRequest(), getLoginName());
				}
				showMessage="静态化处理成功!";
			} catch (Exception e) {
				e.printStackTrace();
				showMessage="静态化处理失败，原因:"+e.getMessage().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
			}
		}
		return showMessage(showMessage, "", 0);
	}

	/**
	 * 静态化选择的信息
	 * @return
	 */
	public String html(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				try {
					for (int i = 0; i < idArr.length; i++) {
						if (idArr[i].trim().length()>0) {
							infoService.html(idArr[i].trim(), getServletContext(), getContextPath(), getHttpRequest(), getLoginName());
						}
					}
					showMessage="静态化成功";
					forwardSeconds=3;
				} catch (Exception e) {
					DBProException(e);
					showMessage="静态化失败:"+e.toString()+"!";
				}
			}
		}
		return showMessage(showMessage, forwardUrl, forwardSeconds);
	}
	
	//set and get
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}




	public SiteService getSiteService() {
		return siteService;
	}




	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public List<Site> getSiteList() {
		return siteList;
	}
	public void setSiteList(List<Site> siteList) {
		this.siteList = siteList;
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
	public InfoService getInfoService() {
		return infoService;
	}
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
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
	public List<Info> getInfoList() {
		return infoList;
	}
	public void setInfoList(List<Info> infoList) {
		this.infoList = infoList;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getListPageFuncId() {
		return listPageFuncId;
	}
	public void setListPageFuncId(String listPageFuncId) {
		this.listPageFuncId = listPageFuncId;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public RoleChannelService getRoleChannelService() {
		return roleChannelService;
	}
	public void setRoleChannelService(RoleChannelService roleChannelService) {
		this.roleChannelService = roleChannelService;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Users> getUserList() {
		return userList;
	}
	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public String[] getSignusers() {
		return signusers;
	}
	public void setSignusers(String[] signusers) {
		this.signusers = signusers;
	}
	public InfoSignService getInfoSignService() {
		return infoSignService;
	}
	public void setInfoSignService(InfoSignService infoSignService) {
		this.infoSignService = infoSignService;
	}
	public List<InfoSign> getInfosignList() {
		return infosignList;
	}
	public void setInfosignList(List<InfoSign> infosignList) {
		this.infosignList = infosignList;
	}
	public File getVideoUpload() {
		return videoUpload;
	}
	public void setVideoUpload(File videoUpload) {
		this.videoUpload = videoUpload;
	}
	public String getVideoUploadFileName() {
		return videoUploadFileName;
	}
	public void setVideoUploadFileName(String videoUploadFileName) {
		this.videoUploadFileName = videoUploadFileName;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
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
	public String getOldchannelid() {
		return oldchannelid;
	}
	public void setOldchannelid(String oldchannelid) {
		this.oldchannelid = oldchannelid;
	}
	public String getHtmlChannelOld() {
		return htmlChannelOld;
	}
	public void setHtmlChannelOld(String htmlChannelOld) {
		this.htmlChannelOld = htmlChannelOld;
	}
	public SensitiveService getSensitiveService() {
		return sensitiveService;
	}
	public void setSensitiveService(SensitiveService sensitiveService) {
		this.sensitiveService = sensitiveService;
	}
	public String getTochannelid() {
		return tochannelid;
	}
	public void setTochannelid(String tochannelid) {
		this.tochannelid = tochannelid;
	}
	public String getChannelname() {
		return channelname;
	}
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	public String getSitename() {
		return sitename;
	}
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}
	public InfoImgService getInfoImgService() {
		return infoImgService;
	}
	public void setInfoImgService(InfoImgService infoImgService) {
		this.infoImgService = infoImgService;
	}
	public List<InfoImg> getInfoImgList() {
		return infoImgList;
	}
	public void setInfoImgList(List<InfoImg> infoImgList) {
		this.infoImgList = infoImgList;
	}
	public String getDelOldimgs() {
		return delOldimgs;
	}
	public void setDelOldimgs(String delOldimgs) {
		this.delOldimgs = delOldimgs;
	}
}
