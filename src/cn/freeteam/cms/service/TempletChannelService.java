package cn.freeteam.cms.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.TempletChannelMapper;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletChannel;
import cn.freeteam.cms.model.TempletChannelExample;
import cn.freeteam.cms.model.TempletChannelExample.Criteria;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.XMLUtil;
import freemarker.template.TemplateException;

/**
 * 
 * <p>Title: TempletChannelService.java</p>
 * 
 * <p>Description: 模板栏目相关服务</p>
 * 
 * <p>Date: Jan 23, 2012</p>
 * 
 * <p>Time: 11:49:58 AM</p>
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
public class TempletChannelService extends BaseService{

	private ChannelService channelService;
	private TempletChannelMapper templetChannelMapper;
	
	public TempletChannelService() {
		initMapper("templetChannelMapper");
	}
	/**
	 * 统计
	 * @param templetid
	 * @return
	 */
	public int count(String templetid){
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		if (templetid!=null && templetid.trim().length()>0) {
			criteria.andTempletidEqualTo(templetid.trim());
		}
		return templetChannelMapper.countByExample(example);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<TempletChannel> findByPar(String templetid,String parid){
		return findByPar(templetid, parid, null,null);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<TempletChannel> findByPar(String templetid,String parid,String state,String navigation){
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		if (templetid!=null && templetid.trim().length()>0) {
			criteria.andTempletidEqualTo(templetid.trim());
		}
		if (parid!=null && parid.trim().length()>0 && !"par".equals(parid)) {
			criteria.andParidEqualTo(parid.trim());
		}
		if ("par".equals(parid)) {
			criteria.andSql(" (parid is null or parid = '') ");
		}
		if (state!=null && state.trim().length()>0) {
			criteria.andStateEqualTo(state.trim());
		}
		if (navigation!=null && navigation.trim().length()>0) {
			criteria.andNavigationEqualTo(navigation.trim());
		}
		example.setOrderByClause(" orderNum ");
		return templetChannelMapper.selectByExample(example);
	}

	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<TempletChannel> findByParWithBLOBs(String templetid,String parid){
		return findByParWithBLOBs(templetid, parid, null,null);
	}
	/**
	 * 根据站点和父id查询
	 * @param siteid
	 * @param parid
	 * @return
	 */
	public List<TempletChannel> findByParWithBLOBs(String templetid,String parid,String state,String navigation){
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		if (templetid!=null && templetid.trim().length()>0) {
			criteria.andTempletidEqualTo(templetid.trim());
		}
		if (parid!=null && parid.trim().length()>0 && !"par".equals(parid)) {
			criteria.andParidEqualTo(parid.trim());
		}
		if ("par".equals(parid)) {
			criteria.andSql(" (parid is null or parid = '') ");
		}
		if (state!=null && state.trim().length()>0) {
			criteria.andStateEqualTo(state.trim());
		}
		if (navigation!=null && navigation.trim().length()>0) {
			criteria.andNavigationEqualTo(navigation.trim());
		}
		example.setOrderByClause(" orderNum ");
		return templetChannelMapper.selectByExampleWithBLOBs(example);
	}
	/**
	 * 查询是否有子数据
	 * @param parId
	 * @return
	 */
	public boolean hasChildren(String parId){
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		return templetChannelMapper.countByExample(example)>0;
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public TempletChannel findById(String id){
		return templetChannelMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param TempletChannel
	 */
	public void update(TempletChannel templetChannel){
		templetChannelMapper.updateByPrimaryKeyWithBLOBs(templetChannel);
		DBCommit();
	}
	/**
	 * 添加
	 * @param TempletChannel
	 */
	public String insert(TempletChannel templetChannel){
		String id=UUID.randomUUID().toString();
		templetChannel.setId(id);
		templetChannelMapper.insert(templetChannel);
		DBCommit();
		return id;
	}

	/**
	 * 删除
	 * @param templetId
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void del(String id,HttpServletRequest request) throws IOException, TemplateException{
		TempletChannel templetChannel=findById(id);
		if (templetChannel!=null) {
			delPar(id,request);
			templetChannelMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	/**
	 * 递归删除
	 * @param parId
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void delPar(String parId,HttpServletRequest request) throws IOException, TemplateException{
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andParidEqualTo(parId);
		List<TempletChannel> TempletChannelList=templetChannelMapper.selectByExample(example);
		if (TempletChannelList!=null && TempletChannelList.size()>0) {
			for (int i = 0; i < TempletChannelList.size(); i++) {
				delPar(TempletChannelList.get(i).getId(),request);
			}
		}
		templetChannelMapper.deleteByPrimaryKey(parId);
	}

	/**
	 * 判断是否已存在此页面标识
	 * @param pagemark
	 * @return
	 */
	public boolean hasPagemark(String templetid,String pagemark){
		TempletChannelExample example=new TempletChannelExample();
		Criteria criteria=example.createCriteria();
		criteria.andTempletidEqualTo(templetid);
		criteria.andPagemarkEqualTo(pagemark);
		return templetChannelMapper.countByExample(example)>0;
	}
	/**
	 * 生成指定模板的栏目初始化数据xml文件
	 * @param templetid
	 */
	public void createXML(Templet templet,HttpServletRequest request){
		if (templet!=null) {
			//查找是否有栏目数据
			List<TempletChannel> list=findByParWithBLOBs(templet.getId(), null);
			if (list!=null && list.size()>0) {
				//生成xml
				Document document = DocumentHelper.createDocument();
				//添加元素 channels
		        Element channelsElement = document.addElement("channels");
		        //给channels元素添加属性 xmlns:xs="http://www.w3.org/2001/XMLSchema"
		        channelsElement.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		        for (int i = 0; i < list.size(); i++) {
		        	TempletChannel templetChannel=list.get(i);
			        //添加元素channel
			        Element channelElement = channelsElement.addElement("channel");
			        if (templetChannel.getId()!=null) {
				        //添加channel子元素 id
				        Element idElement = channelElement.addElement("id");
				        idElement.setText(templetChannel.getId());
			        }
			        if (templetChannel.getName()!=null) {
				        //添加channel子元素 name
				        Element nameElement = channelElement.addElement("name");
				        nameElement.setText(templetChannel.getName());
			        }
			        if (templetChannel.getTemplet()!=null) {
				        //添加channel子元素 templet
				        Element templetElement = channelElement.addElement("templet");
				        templetElement.setText(templetChannel.getTemplet());
			        }
			        if (templetChannel.getContenttemplet()!=null) {
				        //添加channel子元素 contenttemplet
				        Element contenttempletElement = channelElement.addElement("contenttemplet");
				        contenttempletElement.setText(templetChannel.getContenttemplet());
			        }
			        if (templetChannel.getImg()!=null) {
				        //添加channel子元素 img
				        Element imgElement = channelElement.addElement("img");
				        imgElement.setText(templetChannel.getImg());
			        }
			        if (templetChannel.getDescription()!=null) {
				        //添加channel子元素 description
				        Element descriptionElement = channelElement.addElement("description");
				        descriptionElement.addCDATA(templetChannel.getDescription());
			        }
			        if (templetChannel.getParid()!=null) {
				        //添加channel子元素 parid
				        Element paridElement = channelElement.addElement("parid");
				        paridElement.setText(templetChannel.getParid());
			        }
			        if (templetChannel.getUrl()!=null) {
				        //添加channel子元素 url
				        Element urlElement = channelElement.addElement("url");
				        urlElement.setText(templetChannel.getUrl());
			        }
			        if (templetChannel.getState()!=null) {
				        //添加channel子元素 state
				        Element stateElement = channelElement.addElement("state");
				        stateElement.setText(templetChannel.getState());
			        }
			        if (templetChannel.getOrdernum()!=null) {
				        //添加channel子元素 ordernum
				        Element ordernumElement = channelElement.addElement("ordernum");
				        ordernumElement.setText(""+templetChannel.getOrdernum());
			        }
			        if (templetChannel.getNavigation()!=null) {
				        //添加channel子元素 navigation
				        Element navigationElement = channelElement.addElement("navigation");
				        navigationElement.setText(templetChannel.getNavigation());
			        }
			        if (templetChannel.getPagemark()!=null) {
				        //添加channel子元素 pagemark
				        Element pagemarkElement = channelElement.addElement("pagemark");
				        pagemarkElement.setText(templetChannel.getPagemark());
			        }
			        if (templetChannel.getHtmlchannel()!=null) {
				        //添加channel子元素 htmlchannel
				        Element htmlchannelElement = channelElement.addElement("htmlchannel");
				        htmlchannelElement.setText(templetChannel.getHtmlchannel());
			        }
			        if (templetChannel.getHtmlchannelold()!=null) {
				        //添加channel子元素 htmlchannelold
				        Element htmlchanneloldElement = channelElement.addElement("htmlchannelold");
				        htmlchanneloldElement.setText(templetChannel.getHtmlchannelold());
			        }
			        if (templetChannel.getHtmlparchannel()!=null) {
				        //添加channel子元素 htmlparchannel
				        Element htmlparchannelElement = channelElement.addElement("htmlparchannel");
				        htmlparchannelElement.setText(templetChannel.getHtmlparchannel());
			        }
			        if (templetChannel.getHtmlsite()!=null) {
				        //添加channel子元素 htmlsite
				        Element htmlsiteElement = channelElement.addElement("htmlsite");
				        htmlsiteElement.setText(templetChannel.getHtmlsite());
			        }
				}
		        //生成xml文件
		        XMLUtil.writeFile(document, request.getRealPath("/")+"/templet/"+templet.getId()+"/channels.xml");
			}
		}
	}
	/**
	 * 导入栏目数据
	 * @throws DocumentException 
	 */
	public void importChannels(Templet templet,HttpServletRequest request) throws DocumentException{
		if (templet!=null) {
			//判断channels.xml文件是否存在
			File file=new File(request.getRealPath("/")+"/templet/"+templet.getId()+"/channels.xml");
			if (file.exists()) {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(file);
				Element root = document.getRootElement();
				Map<String, TempletChannel> channelMap=new HashMap<String, TempletChannel>();
				Map<String, String> importedMap=new HashMap<String, String>();
				// 遍历根结点（channels）的所有孩子节点（channel节点）
				for (Iterator iter = root.elementIterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					TempletChannel templetChannel=new TempletChannel();
					//关联模板
					templetChannel.setTempletid(templet.getId());
					// 遍历channel结点的所有孩子节点，并进行处理
					for (Iterator iterInner = element.elementIterator(); iterInner
							.hasNext();) {
						Element elementInner = (Element) iterInner.next();
						//获取并设置属性
						if (elementInner.getName().equals("id")){
							templetChannel.setId(elementInner.getText());
						}
						else if (elementInner.getName().equals("name")){
							templetChannel.setName(elementInner.getText());
						}
						else if (elementInner.getName().equals("templet")){
							templetChannel.setTemplet(elementInner.getText());
						}
						else if (elementInner.getName().equals("contenttemplet")){
							templetChannel.setContenttemplet(elementInner.getText());
						}
						else if (elementInner.getName().equals("img")){
							templetChannel.setImg(elementInner.getText());
						}
						else if (elementInner.getName().equals("description")){
							templetChannel.setDescription(elementInner.getText());
						}
						else if (elementInner.getName().equals("parid")){
							templetChannel.setParid(elementInner.getText());
						}
						else if (elementInner.getName().equals("url")){
							templetChannel.setUrl(elementInner.getText());
						}
						else if (elementInner.getName().equals("state")){
							templetChannel.setState(elementInner.getText());
						}
						else if (elementInner.getName().equals("ordernum")){
							try {
								templetChannel.setOrdernum(Integer.parseInt(elementInner.getText()));
							} catch (Exception e) {
							}
						}
						if (elementInner.getName().equals("navigation")){
							templetChannel.setNavigation(elementInner.getText());
						}
						if (elementInner.getName().equals("pagemark")){
							templetChannel.setPagemark(elementInner.getText());
						}
						if (elementInner.getName().equals("htmlchannel")){
							templetChannel.setHtmlchannel(elementInner.getText());
						}
						if (elementInner.getName().equals("htmlchannelold")){
							templetChannel.setHtmlchannelold(elementInner.getText());
						}
						if (elementInner.getName().equals("htmlparchannel")){
							templetChannel.setHtmlparchannel(elementInner.getText());
						}
						if (elementInner.getName().equals("htmlsite")){
							templetChannel.setHtmlsite(elementInner.getText());
						}
					}
					channelMap.put(templetChannel.getId(), templetChannel);
				}
				if (!channelMap.isEmpty()) {
					//递归导入
					importChannel(channelMap, importedMap);
				}
			}
		}
	}
	/**
	 * 递归方法导入栏目 
	 */
	public void importChannel(Map<String, TempletChannel> channelMap,Map<String, String> importedMap){
		if (!channelMap.isEmpty()) {
			Iterator<String> iterator=channelMap.keySet().iterator();
			List<String> deList=new ArrayList<String>();
			while (iterator.hasNext()) {
				TempletChannel templetChannel=channelMap.get(iterator.next());
				if (templetChannel!=null) {
					//保存栏目
					String id=templetChannel.getId();
					boolean isinsert=true;
					if (templetChannel.getParid()!=null && templetChannel.getParid().trim().length()>0) {
						//查询父栏目是否保存
						if (importedMap.containsKey(templetChannel.getParid())) {
							//设置parid是父栏目的新id
							templetChannel.setParid(importedMap.get(templetChannel.getParid()));
						}else {
							isinsert=false;
						}
					}
					if (isinsert) {
						templetChannel.setId("");
						importedMap.put(id, insert(templetChannel));
						deList.add(id);
					}
				}
			}
			if (deList.size()>0) {
				for (int i = 0; i < deList.size(); i++) {
					channelMap.remove(deList.get(i));
				}
			}
			if (!channelMap.isEmpty()) {
				importChannel(channelMap, importedMap);
			}
		}
		
	}
	/**
	 * 导入站点栏目数据
	 * @throws DocumentException 
	 */
	public void importSiteChannels(Templet templet,Site site){
		if (templet!=null && site!=null) {
			//查找是否有栏目数据
			List<TempletChannel> list=findByParWithBLOBs(templet.getId(), null);
			Map<String, TempletChannel> channelMap=new HashMap<String, TempletChannel>();
			Map<String, String> importedMap=new HashMap<String, String>();
			if (list!=null && list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setSite(site.getId());
					channelMap.put(list.get(i).getId(), list.get(i));
				}
				importSiteChannel(channelMap, importedMap,site);
			}
		}
	}
	/**
	 * 递归方法导入站点栏目 
	 */
	public void importSiteChannel(Map<String, TempletChannel> channelMap,Map<String, String> importedMap,Site site){
		if (!channelMap.isEmpty()) {
			Iterator<String> iterator=channelMap.keySet().iterator();
			List<String> deList=new ArrayList<String>();
			while (iterator.hasNext()) {
				TempletChannel templetChannel=channelMap.get(iterator.next());
				if (templetChannel!=null) {
					Channel channel=new Channel();
					//保存栏目
					String id=templetChannel.getId();
					boolean isinsert=true;
					if (templetChannel.getParid()!=null && templetChannel.getParid().trim().length()>0) {
						//查询父栏目是否保存
						if (importedMap.containsKey(templetChannel.getParid())) {
							//设置parid是父栏目的新id
							channel.setParid(importedMap.get(templetChannel.getParid()));
						}else {
							isinsert=false;
						}
					}
					if (isinsert) {
						init("channelService");
						channel.setName(templetChannel.getName());
						channel.setTemplet(templetChannel.getTemplet());
						channel.setContenttemplet(templetChannel.getContenttemplet());
						//处理图片
						channel.setImg("/site/"+site.getSourcepath()+templetChannel.getImg());
						channel.setDescription(templetChannel.getDescription());
						channel.setUrl(templetChannel.getUrl());
						channel.setState(templetChannel.getState());
						channel.setOrdernum(templetChannel.getOrdernum());
						channel.setNavigation(templetChannel.getNavigation());
						channel.setPagemark(templetChannel.getPagemark());
						channel.setHtmlchannel(templetChannel.getHtmlchannel());
						channel.setHtmlchannelold(templetChannel.getHtmlchannelold());
						channel.setHtmlparchannel(templetChannel.getHtmlparchannel());
						channel.setHtmlsite(templetChannel.getHtmlsite());
						channel.setSite(templetChannel.getSite());
						importedMap.put(id, channelService.insert(channel));
						deList.add(id);
					}
				}
			}
			if (deList.size()>0) {
				for (int i = 0; i < deList.size(); i++) {
					channelMap.remove(deList.get(i));
				}
			}
			if (!channelMap.isEmpty()) {
				importSiteChannel(channelMap, importedMap,site);
			}
		}
		
	}

	/**
	 * 从站点导入栏目数据
	 * @throws DocumentException 
	 */
	public void importSite(Templet templet,Site site,HttpServletRequest request){
		if (templet!=null && site!=null) {
			//查找是否有栏目数据
			init("channelService");
			List<Channel> list=channelService.findBySite(site.getId(), null, null);
			Map<String, Channel> channelMap=new HashMap<String, Channel>();
			Map<String, String> importedMap=new HashMap<String, String>();
			if (list!=null && list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					channelMap.put(list.get(i).getId(), list.get(i));
				}
				importSite(channelMap, importedMap,templet,request);
			}
		}
	}
	/**
	 * 递归方法导入站点栏目 
	 */
	public void importSite(Map<String, Channel> channelMap,Map<String, String> importedMap,Templet templet,HttpServletRequest request){
		if (!channelMap.isEmpty()) {
			Iterator<String> iterator=channelMap.keySet().iterator();
			List<String> deList=new ArrayList<String>();
			while (iterator.hasNext()) {
				Channel channel=channelMap.get(iterator.next());
				if (channel!=null) {
					TempletChannel templetChannel=new TempletChannel();
					//保存栏目
					String id=channel.getId();
					boolean isinsert=true;
					if (channel.getParid()!=null && channel.getParid().trim().length()>0) {
						//查询父栏目是否保存
						if (importedMap.containsKey(channel.getParid())) {
							//设置parid是父栏目的新id
							templetChannel.setParid(importedMap.get(channel.getParid()));
						}else {
							isinsert=false;
						}
					}
					if (isinsert) {
						templetChannel.setTempletid(templet.getId());
						templetChannel.setName(channel.getName());
						templetChannel.setTemplet(channel.getTemplet());
						templetChannel.setContenttemplet(channel.getContenttemplet());
						//处理图片
						if (channel.getImg()!=null && channel.getImg().trim().length()>0) {
							//判断文件是否存在
							File file=new File(request.getRealPath("/")+channel.getImg());
							if (file.exists()) {
								//复制到模板资源文件夹
								File folder=new File(request.getRealPath("/")+"templet/"+templet.getId()+"/resources/upload");
								if (!folder.exists()) {
									folder.mkdirs();
								}
								String uuid=UUID.randomUUID().toString();
								FileUtil.copy(file, new File(request.getRealPath("/")
										+"templet/"+templet.getId()+"/resources/upload/"
										+uuid+channel.getImg().substring(channel.getImg().lastIndexOf("."))));
								templetChannel.setImg("/resources/upload/"
										+uuid+channel.getImg().substring(channel.getImg().lastIndexOf(".")));
							}
						}
						templetChannel.setDescription(channel.getDescription());
						templetChannel.setUrl(channel.getUrl());
						templetChannel.setState(channel.getState());
						templetChannel.setOrdernum(channel.getOrdernum());
						templetChannel.setNavigation(channel.getNavigation());
						templetChannel.setPagemark(channel.getPagemark());
						templetChannel.setHtmlchannel(channel.getHtmlchannel());
						templetChannel.setHtmlchannelold(channel.getHtmlchannelold());
						templetChannel.setHtmlparchannel(channel.getHtmlparchannel());
						templetChannel.setHtmlsite(channel.getHtmlsite());
						templetChannel.setSite(channel.getSite());
						importedMap.put(id, insert(templetChannel));
						deList.add(id);
					}
				}
			}
			if (deList.size()>0) {
				for (int i = 0; i < deList.size(); i++) {
					channelMap.remove(deList.get(i));
				}
			}
			if (!channelMap.isEmpty()) {
				importSite(channelMap, importedMap,templet,request);
			}
		}
		
	}
	public TempletChannelMapper getTempletChannelMapper() {
		return templetChannelMapper;
	}

	public void setTempletChannelMapper(TempletChannelMapper templetChannelMapper) {
		this.templetChannelMapper = templetChannelMapper;
	}
	public ChannelService getChannelService() {
		return channelService;
	}
	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}
}
