package cn.freeteam.cms.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.TempletLinkMapper;
import cn.freeteam.cms.model.Link;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletChannel;
import cn.freeteam.cms.model.TempletLink;
import cn.freeteam.cms.model.TempletLinkExample;
import cn.freeteam.cms.model.TempletLinkExample.Criteria;
import cn.freeteam.util.XMLUtil;

/**
 * 
 * <p>Title: TempletLinkService.java</p>
 * 
 * <p>Description: 模板链接相关服务</p>
 * 
 * <p>Date: May 8, 2013</p>
 * 
 * <p>Time: 8:41:17 PM</p>
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
public class TempletLinkService extends BaseService{

	private TempletLinkMapper templetLinkMapper;
	private LinkService linkService;
	
	public TempletLinkService() {
		initMapper("templetLinkMapper");
	}

	/**
	 * 统计
	 * @param templetid
	 * @return
	 */
	public int count(String templetid){
		TempletLinkExample example=new TempletLinkExample();
		Criteria criteria=example.createCriteria();
		criteria.andTempletEqualTo(templetid);
		return templetLinkMapper.countByExample(example);
	}
	/**
	 * 删除分类
	 * @param id
	 */
	public void delClass(String id){
		if (id!=null && id.trim().length()>0) {
			//先删除子链接
			TempletLinkExample example=new TempletLinkExample();
			Criteria criteria=example.createCriteria();
			criteria.andParidEqualTo(id);
			templetLinkMapper.deleteByExample(example);
			//删除此分类
			templetLinkMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	/**
	 * 更新
	 * @param templet
	 */
	public void update(TempletLink templetLink){
		templetLinkMapper.updateByPrimaryKey(templetLink);
		DBCommit();
	}
	/**
	 * 添加
	 * @param link
	 * @return
	 */
	public String add(TempletLink templetLink){
		templetLink.setId(UUID.randomUUID().toString());
		templetLinkMapper.insert(templetLink);
		DBCommit();
		return templetLink.getId();
	}
	/**
	 * 检验是否已存在页面标识
	 * @param siteid
	 * @param type
	 * @param isClass
	 * @return
	 */
	public boolean hasPagemark(String templet,String type,boolean isClass,String pagemark){
		TempletLinkExample example=new TempletLinkExample();
		Criteria criteria=example.createCriteria();
		criteria.andTempletEqualTo(templet);
		criteria.andTypeEqualTo(type);
		criteria.andPagemarkEqualTo(pagemark);
		if (isClass) {
			criteria.andSql(" (parid is null or parid = '') ");
		}else {
			criteria.andSql(" (parid is not null and parid != '') ");
		}
		return templetLinkMapper.countByExample(example)>0;
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public TempletLink findById(String id){
		return templetLinkMapper.selectByPrimaryKey(id);
	}
	/**
	 * 查询
	 */
	public List<TempletLink> findAll(TempletLink templetLink,String order){
		TempletLinkExample example=new TempletLinkExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(templetLink, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return templetLinkMapper.selectByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(TempletLink templetLink,Criteria criteria){
		if (templetLink!=null ) {
			if (templetLink.getTemplet()!=null && templetLink.getTemplet().trim().length()>0) {
				criteria.andTempletEqualTo(templetLink.getTemplet());
			}
			if ("1".equals(templetLink.getIsClass())) {
				criteria.andSql(" (parid is null or parid = '') ");
			}else {
				criteria.andSql(" (parid is not null and parid != '') ");
			}
			if (templetLink.getType()!=null && templetLink.getType().trim().length()>0) {
				criteria.andTypeEqualTo(templetLink.getType());
			}
		}
	}
	/**
	 * 生成指定模板的链接分类初始化数据xml文件
	 * @param templetid
	 */
	public void createXML(Templet templet,HttpServletRequest request){
		if (templet!=null) {
			//查找是否有链接分类数据
			TempletLink templetLink=new TempletLink();
			templetLink.setTemplet(templet.getId());
			templetLink.setIsClass("1");
			List<TempletLink> list=findAll(templetLink, "");
			if (list!=null && list.size()>0) {
				//生成xml
				Document document = DocumentHelper.createDocument();
				//添加元素 links
		        Element linksElement = document.addElement("links");
		        //给links元素添加属性 xmlns:xs="http://www.w3.org/2001/XMLSchema"
		        linksElement.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		        for (int i = 0; i < list.size(); i++) {
		        	templetLink=list.get(i);
			        //添加元素link
			        Element linkElement = linksElement.addElement("link");
			        if (templetLink.getId()!=null) {
				        //添加link子元素 id
				        Element idElement = linkElement.addElement("id");
				        idElement.setText(templetLink.getId());
			        }
			        if (templetLink.getName()!=null) {
				        //添加link子元素 name
				        Element nameElement = linkElement.addElement("name");
				        nameElement.setText(templetLink.getName());
			        }
			        if (templetLink.getTemplet()!=null) {
				        //添加link子元素 templet
				        Element templetElement = linkElement.addElement("templet");
				        templetElement.setText(templetLink.getTemplet());
			        }
			        if (templetLink.getImg()!=null) {
				        //添加link子元素 img
				        Element imgElement = linkElement.addElement("img");
				        imgElement.setText(templetLink.getImg());
			        }
			        if (templetLink.getParid()!=null) {
				        //添加link子元素 parid
				        Element paridElement = linkElement.addElement("parid");
				        paridElement.setText(templetLink.getParid());
			        }
			        if (templetLink.getUrl()!=null) {
				        //添加link子元素 url
				        Element urlElement = linkElement.addElement("url");
				        urlElement.setText(templetLink.getUrl());
			        }
			        if (templetLink.getOrdernum()!=null) {
				        //添加link子元素 ordernum
				        Element ordernumElement = linkElement.addElement("ordernum");
				        ordernumElement.setText(""+templetLink.getOrdernum());
			        }
			        if (templetLink.getPagemark()!=null) {
				        //添加link子元素 pagemark
				        Element pagemarkElement = linkElement.addElement("pagemark");
				        pagemarkElement.setText(templetLink.getPagemark());
			        }
			        if (templetLink.getIsok()!=null) {
				        //添加link子元素 isok
				        Element isokElement = linkElement.addElement("isok");
				        isokElement.setText(templetLink.getIsok());
			        }
			        if (templetLink.getType()!=null) {
				        //添加link子元素 type
				        Element typeElement = linkElement.addElement("type");
				        typeElement.setText(templetLink.getType());
			        }
				}
		        //生成xml文件
		        XMLUtil.writeFile(document, request.getRealPath("/")+"/templet/"+templet.getId()+"/links.xml");
			}
		}
	}
	/**
	 * 导入链接分类数据
	 * @throws DocumentException 
	 */
	public void importLinks(Templet templet,HttpServletRequest request) throws DocumentException{
		if (templet!=null) {
			//判断links.xml文件是否存在
			File file=new File(request.getRealPath("/")+"/templet/"+templet.getId()+"/links.xml");
			if (file.exists()) {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(file);
				Element root = document.getRootElement();
				// 遍历根结点（links）的所有孩子节点（link节点）
				for (Iterator iter = root.elementIterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					TempletLink templetLink=new TempletLink();
					//关联模板
					templetLink.setTemplet(templet.getId());
					// 遍历link结点的所有孩子节点，并进行处理
					for (Iterator iterInner = element.elementIterator(); iterInner
							.hasNext();) {
						Element elementInner = (Element) iterInner.next();
						//获取并设置属性
						if (elementInner.getName().equals("id")){
							templetLink.setId(elementInner.getText());
						}
						else if (elementInner.getName().equals("name")){
							templetLink.setName(elementInner.getText());
						}
						else if (elementInner.getName().equals("isok")){
							templetLink.setIsok(elementInner.getText());
						}
						else if (elementInner.getName().equals("img")){
							templetLink.setImg(elementInner.getText());
						}
						else if (elementInner.getName().equals("type")){
							templetLink.setType(elementInner.getText());
						}
						else if (elementInner.getName().equals("parid")){
							templetLink.setParid(elementInner.getText());
						}
						else if (elementInner.getName().equals("url")){
							templetLink.setUrl(elementInner.getText());
						}
						else if (elementInner.getName().equals("ordernum")){
							try {
								templetLink.setOrdernum(Integer.parseInt(elementInner.getText()));
							} catch (Exception e) {
							}
						}
						if (elementInner.getName().equals("pagemark")){
							templetLink.setPagemark(elementInner.getText());
						}
					}
					//保存到数据库
					add(templetLink);
				}
			}
		}
	}
	/**
	 * 导入站点链接分类数据
	 */
	public void importSiteLinks(Templet templet,Site site){
		if (templet!=null && site!=null) {
			//查找是否有链接分类数据
			TempletLink templetLink=new TempletLink();
			templetLink.setTemplet(templet.getId());
			templetLink.setIsClass("1");
			List<TempletLink> list=findAll(templetLink, "");
			if (list!=null && list.size()>0) {
				init("linkService");
				for (int i = 0; i < list.size(); i++) {
					templetLink=list.get(i);
					if (templetLink!=null) {
						Link link=new Link();
						link.setSite(site.getId());
						link.setName(templetLink.getName());
						link.setImg(templetLink.getImg());
						link.setParid(templetLink.getParid());
						link.setUrl(templetLink.getUrl());
						link.setOrdernum(templetLink.getOrdernum());
						link.setPagemark(templetLink.getPagemark());
						link.setIsok(templetLink.getIsok());
						link.setType(templetLink.getType());
						linkService.add(link);
					}
				}
			}
		}
	}

	/**
	 * 导入站点链接分类数据
	 */
	public void importSite(Templet templet,Site site){
		if (templet!=null && site!=null) {
			//查找是否有链接分类数据
			Link link=new Link();
			link.setSite(site.getId());
			link.setIsClass("1");
			init("linkService");
			List<Link> list=linkService.findAll(link, "");
			if (list!=null && list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					link=list.get(i);
					if (link!=null) {
						TempletLink templetLink=new TempletLink();
						templetLink.setTemplet(templet.getId());
						templetLink.setName(link.getName());
						templetLink.setImg(link.getImg());
						templetLink.setParid(link.getParid());
						templetLink.setUrl(link.getUrl());
						templetLink.setOrdernum(link.getOrdernum());
						templetLink.setPagemark(link.getPagemark());
						templetLink.setIsok(link.getIsok());
						templetLink.setType(link.getType());
						add(templetLink);
					}
				}
			}
		}
	}

	public TempletLinkMapper getTempletLinkMapper() {
		return templetLinkMapper;
	}

	public void setTempletLinkMapper(TempletLinkMapper templetLinkMapper) {
		this.templetLinkMapper = templetLinkMapper;
	}

	public LinkService getLinkService() {
		return linkService;
	}

	public void setLinkService(LinkService linkService) {
		this.linkService = linkService;
	}
}
