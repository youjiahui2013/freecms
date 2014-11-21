package cn.freeteam.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


import cn.freeteam.model.Operbutton;
import cn.freeteam.service.OperbuttonService;
import cn.freeteam.util.OperButtonUtil;




/**
 * 操作按钮列表标签：
 * 2011-4-15
 * 
 * @author freeteam
 */
public class OperButtonsTag extends BaseTag{

	private String buttonAttr=" class=\"button\" ";
	
	private String split="&nbsp;";
	public int doStartTag() throws JspException {
		try {
			//判断是否有pageFuncId参数
			HttpServletRequest request=getRequest();
			if (request.getParameter("pageFuncId")!=null && request.getParameter("pageFuncId").trim().length()>0) {
				String pageFuncId=request.getParameter("pageFuncId").trim();
				//先查找session中是否已存在
				List<Operbutton> buttons=OperButtonUtil.getButtons(pageFuncId, request);
				if (buttons==null || buttons.size()==0) {
					buttons=new ArrayList<Operbutton>();
					//未存在则重新读取
					OperbuttonService service=new OperbuttonService();
					List<Operbutton> buttonList=null;
					//if (isWebSysteLogin()) {
						buttons=service.findByFuncOk(pageFuncId);
					//}else {
					//	buttons=service.findByAuth(pageFuncId, getLoginAdmin().getId());
					//}
					//设置到session
					OperButtonUtil.setButtons(pageFuncId, buttons, request);
				}
				if (buttons!=null && buttons.size()>0) {
					StringBuilder sBuilder=new StringBuilder();
					Operbutton operbutton;
					//输出操作按钮
					for (int i = 0; i < buttons.size(); i++) {
						operbutton=buttons.get(i);
						if (operbutton!=null) {
							if (i>0) {
								sBuilder.append(split);
							}
							sBuilder.append("<input type=\"button\" value=\"");
							sBuilder.append(operbutton.getName());
							sBuilder.append("\" onclick=\"");
							sBuilder.append(operbutton.getCode());
							sBuilder.append("\" ");
							sBuilder.append(buttonAttr);
							sBuilder.append(" />");
						}
					}
					pageContext.getOut().println(sBuilder.toString());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.SKIP_BODY;
	}
	public String getButtonAttr() {
		return buttonAttr;
	}
	public void setButtonAttr(String buttonAttr) {
		this.buttonAttr = buttonAttr;
	}
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
}