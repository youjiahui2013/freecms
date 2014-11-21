package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseDirective;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.Mail;
import cn.freeteam.cms.service.CommentService;
import cn.freeteam.cms.service.MailService;
import cn.freeteam.cms.util.FreemarkerPager;
import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: CommentPageDirective.java</p>
 * 
 * <p>Description: 评论分页
 * 
 * 参数
 * num  		显示数量
 * siteid 		站点id
 * objtype	 	评论对象
 * objid		评论对象id
 * membername	评论人
 * isanonymous	是否匿名
 * state		审核状态 空字符串表示所有(默认) 1已审核 0未审核
 * order 		显示顺序
 * 				1.发表时间降序(默认)
 * 				2.发表时间升序
 * cache		是否使用缓存，默认为false
 * page			当前第几页，默认1		
 * action		分页跳转页面
 * 				
 * 
 * 返回值
 * commentList	评论对象列表
 * pager		分页对象
 * 
 * 示例
<@commentPage  num='10' page='1' objtype="info" objid="${currInfo.id}" action='${contextPath}templet_pro.do?siteid=${site.id}&templetPath=comment.html';commentList,pager>

<ul>
	<#list commentList as comment>
	<li>
		分页:${comment.content!""}
	</li>
	</#list>
</ul>
${pager.formPageStr}
</@commentPage>
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
public class CommentPageDirective extends BaseDirective implements TemplateDirectiveModel{

	private CommentService commentService;
	
	public CommentPageDirective() {
		init("commentService");
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
				Comment comment=new Comment();
				comment.setSiteid(getParam(params, "siteid"));
				comment.setObjtype(getParam(params, "objtype"));
				comment.setObjid(getParam(params, "objid"));
				comment.setIsanonymous(getParam(params, "isanonymous"));
				comment.setMembername(getParam(params, "membername"));
				comment.setState(getParam(params, "state"));
				String orderSql="";
				if ("1".equals(order)) {
					//发布时间降序(默认)
					orderSql=" addtime desc";
				}
				else if ("2".equals(order)) {
					//发布时间升序
					orderSql=" addtime";
				}
				boolean cache="true".equals(getParam(params, "cache"))?true:false;
				int count=commentService.count(comment, cache);
				FreemarkerPager pager=new FreemarkerPager();
				pager.setCurrPage(page);
				pager.setTotalCount(count);
				pager.setPageSize(num);
				pager.setAction(getParam(params, "action"));
				List<Comment> list=commentService.find(comment, orderSql, page, num,cache);
				loopVars[0]=new ArrayModel(list.toArray(),new BeansWrapper()); 
				if(loopVars.length>1){
					loopVars[1]=new BeanModel(pager,new BeansWrapper()); 
				}
				body.render(env.getOut());  
			}
		}
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

}