package cn.freeteam.cms.action;

import java.util.List;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Link;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.service.LinkService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.HtmlCode;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.Pager;


/**
 * 
 * <p>Title: LinkAction.java</p>
 * 
 * <p>Description:关于链接的相关操作 </p>
 * 
 * <p>Date: May 15, 2012</p>
 * 
 * <p>Time: 3:06:57 PM</p>
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
public class LinkAction extends BaseAction{
	private LinkService linkService;
	private SiteService siteService;
	
	
	private List<Site> siteList;
	private List<Link> linkList;
	
	private Link link;
	private Link linkClass;
	private String order=" ordernum ";//默认按排序号
	private Site site;
	private String logContent;
	private String ids;

	public LinkAction(){
		init("linkService","siteService");
	}
	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		site=getManageSite();
		if (site!=null) {
			if (link!=null && link.getId()!=null && link.getId().trim().length()>0) {
				link=linkService.findById(link.getId());
				if (link!=null) {
					linkClass=linkService.findById(link.getParid());
				}
			}
			//提取第一个站点的链接分类
			Link classLink=new Link();
			classLink.setSite(site.getId());
			classLink.setIsClass("1");
			classLink.setType(link.getType());
			linkList=linkService.findAll(classLink, order);
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
			link.setUrl(HtmlCode.url(link.getUrl()));
			if (link!=null && link.getId()!=null) {
				Link  oldlink=linkService.findById(link.getId());
				if (oldlink!=null) {
					//如果原来有和现在的pagemark不同则判断新的pagemark是否存在
					if (link.getPagemark()!=null && link.getPagemark().trim().length()>0&&
							oldlink.getPagemark()!=null && 
							!oldlink.getPagemark().equals(link.getPagemark())) {
						if (linkService.hasPagemark(link.getSite(), link.getType(),false,link.getPagemark())) {
							write("msg此页面标识已存在", "UTF-8");
							return null;
						}
					}
					oldlink.setParid(link.getParid());
					oldlink.setUrl(link.getUrl());
					oldlink.setSite(link.getSite());
					oldlink.setName(link.getName());
					oldlink.setOrdernum(link.getOrdernum());
					oldlink.setIsok(link.getIsok());
					oldlink.setImg(link.getImg());
					oldlink.setPagemark(link.getPagemark());
					oper="修改";
					linkService.update(oldlink);
				}
			}else {
				//添加
				//判断页面标识是否已存在
				if (link.getPagemark()!=null && link.getPagemark().trim().length()>0&&
						linkService.hasPagemark(link.getSite(), link.getType(), false,link.getPagemark())) {
					write("msg此页面标识已存在", "UTF-8");
					return null;
				}
				linkService.add(link);
			}
			logContent=oper+"链接("+link.getName()+")成功!";
			write("succ"+link.getSite(), "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"链接("+link.getName()+")失败:"+e.toString()+"!";
		}
		OperLogUtil.log(getLoginName(), logContent, getHttpRequest());
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
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							link=linkService.findById(idArr[i]);
							if (link!=null) {
								linkService.del(idArr[i]);
								sb.append(idArr[i]+";");
								logContent="删除链接("+link.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除链接("+link.getName()+")失败:"+e.toString()+"!";
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
	 * 分类编辑页面
	 * @return
	 */
	public String clazzEdit(){
		site=getManageSite();
		if (site!=null) {
			if (link!=null && link.getId()!=null && link.getId().trim().length()>0) {
				link=linkService.findById(link.getId());
			}
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
			if (link!=null && link.getId()!=null) {
				Link  oldlink=linkService.findById(link.getId());
				if (oldlink!=null) {
					//如果原来有和现在的pagemark不同则判断新的pagemark是否存在
					if (link.getPagemark()!=null && link.getPagemark().trim().length()>0&&
							oldlink.getPagemark()!=null && 
							!oldlink.getPagemark().equals(link.getPagemark())) {
						if (linkService.hasPagemark(link.getSite(), link.getType(),true,link.getPagemark())) {
							write("msg此页面标识已存在", "UTF-8");
							return null;
						}
					}
					oldlink.setSite(link.getSite());
					oldlink.setName(link.getName());
					oldlink.setOrdernum(link.getOrdernum());
					oldlink.setIsok(link.getIsok());
					oldlink.setImg(link.getImg());
					oldlink.setPagemark(link.getPagemark());
					oper="修改";
					linkService.update(oldlink);
				}
			}else {
				//添加
				//判断页面标识是否已存在
				if (link.getPagemark()!=null && link.getPagemark().trim().length()>0&&
						linkService.hasPagemark(link.getSite(), link.getType(), true,link.getPagemark())) {
					write("msg此页面标识已存在", "UTF-8");
					return null;
				}
				linkService.add(link);
			}
			logContent=oper+"链接分类("+link.getName()+")成功!";
			write("succ"+link.getSite(), "GBK");
		} catch (Exception e) {
			DBProException(e);
			logContent=oper+"链接分类("+link.getName()+")失败:"+e.toString()+"!";
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
							link=linkService.findById(idArr[i]);
							if (link!=null) {
								linkService.delClass(idArr[i]);
								sb.append(idArr[i]+";");
								logContent="删除链接分类("+link.getName()+")成功!";
							}
						} catch (Exception e) {
							DBProException(e);
							logContent="删除链接分类("+link.getName()+")失败:"+e.toString()+"!";
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
	 * 选择站点下所有分类
	 * @return
	 */
	public String clazzSelect(){
		//只有选择站点才查询
		if (link!=null && link.getSite()!=null && link.getSite().trim().length()>0) {
			link.setIsClass("1");
			linkList=linkService.findAll(link, order);
			if (linkList!=null && linkList.size()>0) {
				StringBuffer sb=new StringBuffer();
				sb.append("[");
				for (int i = 0; i < linkList.size(); i++) {
					if (i>0) {
						sb.append(",");
					}
					sb.append("{id:'"+linkList.get(i).getId()+"',name:'"+linkList.get(i).getName()+"'}");
				}
				sb.append("]");
				write(sb.toString(), "UTF-8");
			}
		}
		return null;
	}
	/**
	 * 链接类别
	 * @return
	 */
	public String clazz(){
		site=getManageSite();
		if (site!=null) {
			if (link==null ){
				link=new Link();
			}
			if(link.getSite()==null || link.getSite().trim().length()==0) {
				link.setSite(site.getId());
			}
			//只有选择站点才查询
			if (link!=null && link.getSite()!=null && link.getSite().trim().length()>0) {
				link.setIsClass("1");
				if (order.trim().length()==0) {
					order=" ordernum ";
				}
				linkList=linkService.find(link, order, currPage, pageSize);
				totalCount=linkService.count(link);
				Pager pager=new Pager(getHttpRequest());
				pager.appendParam("link.site");
				pager.appendParam("link.type");
				pager.appendParam("link.name");
				pager.appendParam("link.pagemark");
				pager.appendParam("order");
				pager.appendParam("pageSize");
				pager.appendParam("pageFuncId");
				pager.setCurrPage(currPage);
				pager.setPageSize(pageSize);
				pager.setTotalCount(totalCount);
				pager.setOutStr("link_clazz.do");
				pageStr=pager.getOutStr();
			}
		}
		return "class";
	}

	/**
	 * 链接列表
	 * @return
	 */
	public String list(){
		site=getManageSite();
		if (site!=null) {
			if (link==null ){
				link=new Link();
			}
			if(link.getSite()==null || link.getSite().trim().length()==0) {
				link.setSite(site.getId());
			}
			//只有选择站点才查询
			if (link!=null && link.getSite()!=null && link.getSite().trim().length()>0) {
				if (order.trim().length()==0) {
					order=" ordernum ";
				}
				linkList=linkService.find(link, order, currPage, pageSize);
				totalCount=linkService.count(link);
				Pager pager=new Pager(getHttpRequest());
				pager.appendParam("link.site");
				pager.appendParam("link.name");
				pager.appendParam("link.className");
				pager.appendParam("link.pagemark");
				pager.appendParam("link.type");
				pager.appendParam("order");
				pager.appendParam("pageSize");
				pager.appendParam("pageFuncId");
				pager.setCurrPage(currPage);
				pager.setPageSize(pageSize);
				pager.setTotalCount(totalCount);
				pager.setOutStr("link_list.do");
				pageStr=pager.getOutStr();
			}
		}
		return "list";
	}
	
	public LinkService getLinkService() {
		return linkService;
	}

	public void setLinkService(LinkService linkService) {
		this.linkService = linkService;
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
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public List<Link> getLinkList() {
		return linkList;
	}
	public void setLinkList(List<Link> linkList) {
		this.linkList = linkList;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public Link getLinkClass() {
		return linkClass;
	}
	public void setLinkClass(Link linkClass) {
		this.linkClass = linkClass;
	}
}
