package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.service.GuestbookService;
import cn.freeteam.cms.util.FreemarkerPager;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: GuestbookPageDirective.java</p>
 * 
 * <p>Description: 留言分页列表 
 * 
 * 参数
 * num  		显示数量
 * siteid  		站点id
 * state		审核状态 空字符串表示所有(默认) 0未审核 1已审核 2审核不通过
 * order 		显示顺序
 * 				1.发表时间降序(默认)
 * 				2.发表时间升序
 * 				3.回复时间降序
 * 				4.回复时间升序
 * cache		是否使用缓存，默认为false
 * page			当前第几页，默认1		
 * action		分页跳转页面
 * titleLen		标题显示长度
 * 				
 * 
 * 返回值
 * guestbookList		留言对象列表
 * pager		分页对象
 * 
 * 示例
<@guestbookPage  num='1' page='${page}' siteid='${site.id}' action='${contextPath}templet_pro.do?siteid=${site.id}&currChannelid=${currChannel.id}&templetPath=guestbook.html';guestbookList,pager>

<ul>
	<#list guestbookList as guestbook>
	<li>
		分页:${guestbook.title}
	</li>
	</#list>
</ul>
${pager.formPageStr}
</@guestbookPage>
 * </p>
 * <p>Date: Jan 21, 2013</p>
 * 
 * <p>Time: 9:43:36 PM</p>
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
public class GuestbookPageDirective extends BaseDirective implements TemplateDirectiveModel{

	private GuestbookService guestbookService;
	
	public GuestbookPageDirective() {
		init("guestbookService");
	}
	
	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0) {
				//显示数量
				int num=getParamInt(params, "num", 10);
				//标题长度
				int titleLen=getParamInt(params, "titleLen",0);
				//当前第几页
				int page=getParamInt(params, "page", 1);
				//排序
				String order=getParam(params, "order","1");
				Guestbook guestbook=new Guestbook();
				guestbook.setState(getParam(params, "state"));
				guestbook.setSiteid(getParam(params, "siteid"));
				String orderSql="";
				if ("1".equals(order)) {
					//发布时间降序(默认)
					orderSql=" addtime desc";
				}
				else if ("2".equals(order)) {
					//发布时间升序
					orderSql=" addtime";
				}
				else if ("3".equals(order)) {
					//回复时间倒序
					orderSql=" retime desc";
				}
				else if ("4".equals(order)) {
					//回复时间升序
					orderSql=" retime";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				int count=guestbookService.count(guestbook, cache);
				FreemarkerPager pager=new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setAction(getParam(params, "action"));
				List<Guestbook> list=guestbookService.find(guestbook, orderSql, page, num,cache);
				if (list!=null && list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						if (titleLen>0 && list.get(i).getTitle().length()>titleLen) {
							//判断标题长度
							list.get(i).setTitle(list.get(i).getTitle().substring(0, titleLen));
						}
					}
				}
				loopVars[0]=new ArrayModel(list.toArray(),new BeansWrapper()); 
				if(loopVars.length>1){
					loopVars[1]=new BeanModel(pager,new BeansWrapper()); 
				}
				body.render(env.getOut());  
			}
		}
	}

	public GuestbookService getGuestbookService() {
		return guestbookService;
	}

	public void setGuestbookService(GuestbookService guestbookService) {
		this.guestbookService = guestbookService;
	}
}