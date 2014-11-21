package cn.freeteam.cms.action;

import java.util.List;



import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletLink;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.TempletLinkService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.util.OperLogUtil;

/**
 * 
 * <p>Title: TempletLinkAction.java</p>
 * 
 * <p>Description: 模板链接相关操作</p>
 * 
 * <p>Date: May 8, 2013</p>
 * 
 * <p>Time: 8:43:09 PM</p>
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
public class TempletLinkAction extends BaseAction{

	private TempletLinkService templetLinkService;
	private Templet templet;
	private TempletLink templetLink;
	private List<TempletLink> templetLinkList;
	private TempletService templetService;
	private String order=" ordernum ";//默认按排序号
	private String logContent;
	private String ids;
	private Site site;
	private SiteService siteService;
	
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


	public String getIds() {
		return ids;
	}


	public void setIds(String ids) {
		this.ids = ids;
	}


	public Templet getTemplet() {
		return templet;
	}


	public void setTemplet(Templet templet) {
		this.templet = templet;
	}


	public TempletLink getTempletLink() {
		return templetLink;
	}


	public void setTempletLink(TempletLink templetLink) {
		this.templetLink = templetLink;
	}


	public List<TempletLink> getTempletLinkList() {
		return templetLinkList;
	}


	public void setTempletLinkList(List<TempletLink> templetLinkList) {
		this.templetLinkList = templetLinkList;
	}


	public TempletLinkAction() {
		init("templetLinkService","templetService");
	}
	

	/**
	 * 分类编辑页面
	 * @return
	 */
	public String clazzEdit(){
		if (templetLink!=null && templetLink.getId()!=null && templetLink.getId().trim().length()>0) {
			templetLink=templetLinkService.findById(templetLink.getId());
		}
		return "clazzEdit";
	}
	/**
	 * 编辑处理
	 * @return
	 */
	public String clazzEditDo(){
		String oper="添加";
		try {
			if (templetLink!=null && templetLink.getId()!=null) {
				TempletLink  oldlink=templetLinkService.findById(templetLink.getId());
				if (oldlink!=null) {
					//如果原来有和现在的pagemark不同则判断新的pagemark是否存在
					if (templetLink.getPagemark()!=null && templetLink.getPagemark().trim().length()>0&&
							oldlink.getPagemark()!=null && 
							!oldlink.getPagemark().equals(templetLink.getPagemark())) {
						if (templetLinkService.hasPagemark(templetLink.getTemplet(), templetLink.getType(),true,templetLink.getPagemark())) {
							write("msg此页面标识已存在", "UTF-8");
							return null;
						}
					}
					oldlink.setTemplet(templetLink.getTemplet());
					oldlink.setType(templetLink.getType());
					oldlink.setName(templetLink.getName());
					oldlink.setOrdernum(templetLink.getOrdernum());
					oldlink.setIsok(templetLink.getIsok());
					oldlink.setImg(templetLink.getImg());
					oldlink.setPagemark(templetLink.getPagemark());
					oper="修改";
					templetLinkService.update(oldlink);
				}
			}else {
				//添加
				//判断页面标识是否已存在
				if (templetLink.getPagemark()!=null && templetLink.getPagemark().trim().length()>0&&
						templetLinkService.hasPagemark(templetLink.getTemplet(), templetLink.getType(), true,templetLink.getPagemark())) {
					write("msg此页面标识已存在", "UTF-8");
					return null;
				}
				templetLinkService.add(templetLink);
			}
			logContent=oper+"模板链接分类("+templetLink.getName()+")成功!";
			write("succ"+templetLink.getTemplet(), "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"模板链接分类("+templetLink.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
		return null;
	}
	/**
	 * 删除
	 * @return
	 */
	public String clazzDel(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							templetLink=templetLinkService.findById(idArr[i]);
							if (templetLink!=null) {
								templetLinkService.delClass(idArr[i]);
								sb.append(idArr[i]+";");
								logContent="删除模板链接分类("+templetLink.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除模板链接分类("+templetLink.getName()+")失败:"+e.toString()+"!";
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
	 * 链接类别
	 * @return
	 */
	public String clazz(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
			if (templet!=null) {
				if (templetLink==null ){
					templetLink=new TempletLink();
				}
				if(templetLink.getTemplet()==null || templetLink.getTemplet().trim().length()==0) {
					templetLink.setTemplet(templet.getId());
				}
				//只有选择模板才查询
				if (templetLink!=null && templetLink.getTemplet()!=null && templetLink.getTemplet().trim().length()>0) {
					templetLink.setIsClass("1");
					if (order.trim().length()==0) {
						order=" ordernum ";
					}
					templetLinkList=templetLinkService.findAll(templetLink, order);
				}
			}
		}
		return "class";
	}
	/**
	 * 从站点导入
	 * @return
	 */
	public String importSite(){
		if (site!=null && site.getId()!=null && site.getId().trim().length()>0
				&& templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			try {
				init("siteService");
				site=siteService.findById(site.getId());
				templet=templetService.findById(templet.getId());
				templetLinkService.importSite(templet, site);
				showMessage="导入成功";
			} catch (Exception e) {
				showMessage="导入失败:"+e.getMessage();
			}
		}
		return showMessage(showMessage, "templetLink_clazz.do?templet.id="+templet.getId(), 3);
	}
	public TempletLinkService getTempletLinkService() {
		return templetLinkService;
	}

	public void setTempletLinkService(TempletLinkService templetLinkService) {
		this.templetLinkService = templetLinkService;
	}


	public TempletService getTempletService() {
		return templetService;
	}


	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	public String getLogContent() {
		return logContent;
	}


	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
}
