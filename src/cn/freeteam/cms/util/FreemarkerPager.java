package cn.freeteam.cms.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>Title: FreemarkerPager.java</p>
 * 
 * <p>Description: freemarker使用的分页类</p>
 * 
 * <p>Date: May 18, 2012</p>
 * 
 * <p>Time: 12:13:56 PM</p>
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
public class FreemarkerPager {

	private int pageSize=10;
	private int currPage=1;
	private int totalCount=0;
	private int totalPage=0;
	private String url="";
	private String tablePageStr ;
	private String formTablePageStr ;
	private String formPageStr;
	private String pageStr;
	private Map<String, String> params ;
	private String action;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrPage() {
		currPage = currPage<1?1:currPage;
		currPage = totalPage>0&&currPage>totalPage?totalPage:currPage;
		return currPage;
	}
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	public int getTotalCount() {
		if (getTotalPage()*pageSize<totalCount) {
			totalCount=getTotalPage()*pageSize;
		}
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		if (totalPage==0) {
			if(totalCount%pageSize==0){
				totalPage = totalCount/pageSize;
			}else{
				totalPage = totalCount/pageSize+1;
			}
		}
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTablePageStr() {

		StringBuffer sb=new StringBuffer();
		sb.append("<table width=\"97%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		sb.append("	<tr>");
		sb.append("	  <td height=\"46\" align=\"center\" valign=\"middle\" background=\"../images/main_bbj.gif\"><table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("		<tr>");
		sb.append("       <td height=\"40\" align=\"center\" valign=\"middle\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("       <tr>");
		sb.append("       <td width=\"100\" height=\"40\" align=\"center\" valign=\"middle\">共&nbsp;<b>"+getTotalCount()+"</b>&nbsp;条</td>");
		if(currPage-1>=1){
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:location.href='"+url+".html'\">首页</a></td>");
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:location.href='"+url+((currPage-1)>1?"_"+(currPage-2):"")+".html';\">上一页</a></td>");
		}else{
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">首页</td>");
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">上一页</td>");
		}
		if(currPage+1<=getTotalPage()){
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:location.href='"+url+(((currPage+1))>1?"_"+(currPage):"")+".html';\">下一页</a></td>");
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:location.href='"+url+(((getTotalPage()))>1?"_"+(getTotalPage()-1):"")+".html'\">尾页</a></td>");
		}else{
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">下一页</td>");
			sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">尾页</td>");
		}
		sb.append("		  <td width=\"220\" align=\"center\" valign=\"middle\" >每页&nbsp;<b>");
		sb.append(pageSize+"</b>&nbsp;");
		sb.append("条&nbsp;当前第&nbsp;<b>"+currPage+"</b>&nbsp;页/共&nbsp;<b>"+getTotalPage()+"</b>&nbsp;页</td>");
		sb.append("		</tr>");
		sb.append("	  </table></td>");
		
		sb.append("	  <td width=\"35%\" height=\"40\" align=\"center\" valign=\"middle\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("		<tr>");
		sb.append("       <td width=\"120\" height=\"40\" align=\"center\" valign=\"middle\">第&nbsp;<input id=\"pageNum\" type=\"text\" value=\""+currPage+"\" class=\"ts_box4\" size=\"1\" style=\"text-align:center\">&nbsp;页</td>");
		sb.append("       <td width=\"30\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:"+(getTotalPage()>1?"if(isNaN(document.getElementById('pageNum').value)==false&&document.getElementById('pageNum').value!="+currPage
				+"&&document.getElementById('pageNum').value>=1&&document.getElementById('pageNum').value<="+getTotalPage()
				+"){location.href='"+url+"'+(parseInt(document.getElementById('pageNum').value)>1?'_'+(parseInt(document.getElementById('pageNum').value)-1):'')+'.html';}":"void(0)")+"\">跳转</a></td>");
		sb.append("		</tr>");
		sb.append("	  </table></td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("</td></tr></table>");
		tablePageStr=sb.toString();
		return tablePageStr;
	}
	public void setTablePageStr(String pageStr) {
		this.tablePageStr = pageStr;
	}
	public String getFormTablePageStr() {

		try {
			totalPage=getTotalPage();
			currPage = currPage<1?1:currPage;
			currPage = totalPage>0&&currPage>totalPage?totalPage:currPage;
			
			StringBuffer sb=new StringBuffer();
			sb.append("<form name='pageForm' method='get' action=\""+action+"\" onkeydown=\"if(event.keyCode==13){return false;}\">");
			
			sb.append("<input type='hidden' name='page' value=''>");
			if(params!=null && params.size()>0){
					Iterator<String> keys = params.keySet().iterator();
					String name="";
					while (keys.hasNext()) {
						name=keys.next();
						sb.append("<input type='hidden' name='"+name+"' value='"+params.get(name)+"'>");
					}
			}
			sb.append("<table width=\"97%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			sb.append("	<tr>");
			sb.append("	  <td height=\"46\" align=\"center\" valign=\"middle\" background=\"../images/main_bbj.gif\"><table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			sb.append("		<tr>");
			sb.append("       <td height=\"40\" align=\"center\" valign=\"middle\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			sb.append("       <tr>");
			sb.append("       <td width=\"100\" height=\"40\" align=\"center\" valign=\"middle\">共&nbsp;<b>"+getTotalCount()+"</b>&nbsp;条</td>");
			if(currPage-1>=1){
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:pageForm.page.value=1;pageForm.submit();\">首页</a></td>");
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:pageForm.page.value="+(currPage-1)+";pageForm.submit();\">上一页</a></td>");
			}else{
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">首页</td>");
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">上一页</td>");
			}
			if(currPage+1<=totalPage){
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:pageForm.page.value="+(currPage+1)+";pageForm.submit();\">下一页</a></td>");
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:pageForm.page.value="+(totalPage)+";pageForm.submit();\">尾页</a></td>");
			}else{
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">下一页</td>");
				sb.append("       <td width=\"50\" height=\"40\" align=\"center\" valign=\"middle\">尾页</td>");
			}
			sb.append("		  <td width=\"220\" align=\"center\" valign=\"middle\" >每页&nbsp;<b>");
			sb.append(pageSize+"</b>&nbsp;");
			sb.append("条&nbsp;当前第&nbsp;<b>"+currPage+"</b>&nbsp;页/共&nbsp;<b>"+totalPage+"</b>&nbsp;页</td>");
			sb.append("		</tr>");
			sb.append("	  </table></td>");
			
			sb.append("	  <td width=\"35%\" height=\"40\" align=\"center\" valign=\"middle\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			sb.append("		<tr>");
			sb.append("       <td width=\"120\" height=\"40\" align=\"center\" valign=\"middle\">第&nbsp;<input name=\"pageNum\" type=\"text\" value=\""+currPage+"\" class=\"ts_box4\" size=\"1\" style=\"text-align:center\">&nbsp;页</td>");
			sb.append("       <td width=\"30\" height=\"40\" align=\"center\" valign=\"middle\"><a href=\"javascript:"+(totalPage>1?"if(isNaN(pageForm.pageNum.value)==false&&pageForm.pageNum.value!="+currPage+"&&pageForm.pageNum.value>=1&&pageForm.pageNum.value<="+totalPage+"){pageForm.page.value=pageForm.pageNum.value;pageForm.submit();}":"void(0)")+"\">跳转</a></td>");
			sb.append("		</tr>");
			sb.append("	  </table></td>");
			sb.append("	</tr>");
			sb.append("</table>");
			sb.append("</td></tr></table>");
			sb.append("</form>");
			formTablePageStr=sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formTablePageStr;
	}
	public void setFormTablePageStr(String formPageStr) {
		this.formTablePageStr = formPageStr;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getFormPageStr() {

		try {
			totalPage=getTotalPage();
			currPage = currPage<1?1:currPage;
			currPage = totalPage>0&&currPage>totalPage?totalPage:currPage;
			
			StringBuffer sb=new StringBuffer();
			sb.append("<form name='pageForm' method='post' action=\""+action+"\" onkeydown=\"if(event.keyCode==13){return false;}\">");
			
			sb.append("<input type='hidden' name='page' value=''>");
			if(params!=null && params.size()>0){
					Iterator<String> keys = params.keySet().iterator();
					String name="";
					while (keys.hasNext()) {
						name=keys.next();
						sb.append("<input type='hidden' name='"+name+"' value='"+params.get(name)+"'>");
					}
			}
			sb.append("共&nbsp;<b>"+getTotalCount()+"</b>&nbsp;条&nbsp;&nbsp;&nbsp;");
			if(currPage-1>=1){
				sb.append("<a href=\"javascript:pageForm.page.value=1;pageForm.submit();\">首页</a>&nbsp;");
				sb.append("<a href=\"javascript:pageForm.page.value="+(currPage-1)+";pageForm.submit();\">上一页</a>&nbsp;");
			}else{
				sb.append("首页&nbsp;");
				sb.append("上一页&nbsp;");
			}
			if(currPage+1<=totalPage){
				sb.append("<a href=\"javascript:pageForm.page.value="+(currPage+1)+";pageForm.submit();\">下一页</a>&nbsp;");
				sb.append("<a href=\"javascript:pageForm.page.value="+(totalPage)+";pageForm.submit();\">尾页</a>&nbsp;");
			}else{
				sb.append("下一页&nbsp;");
				sb.append("尾页&nbsp;");
			}
			sb.append("&nbsp;每页&nbsp;<b>");
			sb.append(pageSize+"</b>&nbsp;");
			sb.append("条&nbsp;当前第&nbsp;<b>"+currPage+"</b>&nbsp;页/共&nbsp;<b>"+totalPage+"</b>&nbsp;页&nbsp;");
			
			sb.append("第&nbsp;<input name=\"pageNum\" type=\"text\" value=\""+currPage+"\" class=\"ts_box4\" size=\"1\" style=\"text-align:center\">&nbsp;页&nbsp;");
			sb.append("<a href=\"javascript:"+(totalPage>1?"if(isNaN(pageForm.pageNum.value)==false&&pageForm.pageNum.value!="+currPage+"&&pageForm.pageNum.value>=1&&pageForm.pageNum.value<="+totalPage+"){pageForm.page.value=pageForm.pageNum.value;pageForm.submit();}":"void(0)")+"\">跳转</a>&nbsp;");
			sb.append("</form>");
			formPageStr=sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formPageStr;
	}
	public void setFormPageStr(String formPageStr) {
		this.formPageStr = formPageStr;
	}
	public String getPageStr() {

		StringBuffer sb=new StringBuffer();
		sb.append("共&nbsp;<b>"+getTotalCount()+"</b>&nbsp;条&nbsp;");
		if(currPage-1>=1){
			sb.append("<a href=\"javascript:location.href='"+url+".html'\">首页</a>&nbsp;");
			sb.append("<a href=\"javascript:location.href='"+url+((currPage-1)>1?"_"+(currPage-2):"")+".html';\">上一页</a>&nbsp;");
		}else{
			sb.append("首页&nbsp;");
			sb.append("上一页&nbsp;");
		}
		if(currPage+1<=getTotalPage()){
			sb.append("<a href=\"javascript:location.href='"+url+(((currPage+1))>1?"_"+(currPage):"")+".html';\">下一页</a>&nbsp;");
			sb.append("<a href=\"javascript:location.href='"+url+(((getTotalPage()))>1?"_"+(getTotalPage()-1):"")+".html'\">尾页</a>&nbsp;");
		}else{
			sb.append("下一页&nbsp;");
			sb.append("尾页&nbsp;");
		}
		sb.append("每页&nbsp;<b>");
		sb.append(pageSize+"</b>&nbsp;");
		sb.append("条&nbsp;当前第&nbsp;<b>"+currPage+"</b>&nbsp;页/共&nbsp;<b>"+getTotalPage()+"</b>&nbsp;页&nbsp;");
		sb.append("第&nbsp;<input id=\"pageNum\" type=\"text\" value=\""+currPage+"\" class=\"ts_box4\" size=\"1\" style=\"text-align:center\">&nbsp;页&nbsp;");
		sb.append("<a href=\"javascript:"+(getTotalPage()>1?"if(isNaN(document.getElementById('pageNum').value)==false&&document.getElementById('pageNum').value!="+currPage
				+"&&document.getElementById('pageNum').value>=1&&document.getElementById('pageNum').value<="+getTotalPage()
				+"){location.href='"+url+"'+(parseInt(document.getElementById('pageNum').value)>1?'_'+(parseInt(document.getElementById('pageNum').value)-1):'')+'.html';}":"void(0)")+"\">跳转</a>&nbsp;");
		pageStr=sb.toString();
		return pageStr;
	}
	public void setPageStr(String pageStr) {
		this.pageStr = pageStr;
	}
}
