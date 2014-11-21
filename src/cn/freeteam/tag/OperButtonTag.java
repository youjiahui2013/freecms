package cn.freeteam.tag;

import java.io.IOException;
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
 * 操作按钮标签：
 * 2011-4-16
 * 
 * @author freeteam
 */
public class OperButtonTag extends BaseTag{

	private String buttonAttr=" class=\"button\" ";
	private String buttonName;
	public int doStartTag() throws JspException {
		try {
			//判断是否有pageFuncId参数
			HttpServletRequest request=getRequest();
			if (request.getParameter("pageFuncId")!=null && request.getParameter("pageFuncId").trim().length()>0) {
				String pageFuncId=request.getParameter("pageFuncId").trim();
				//先查找session中是否已存在
				Operbutton button=OperButtonUtil.getButton(pageFuncId, buttonName,request);
				//button为null并且session中并没保存funcid则得重新提取
				if (button==null && !OperButtonUtil.haveFunc(pageFuncId, request)) {
					//未存在则重新读取
					OperbuttonService service=new OperbuttonService();
					List<Operbutton> buttonList=null;
					buttonList=service.findByFuncOk(pageFuncId);
					if (buttonList!=null && buttonList.size()>0) {
						Operbutton operbutton;
						//输出操作按钮
						for (int i = 0; i < buttonList.size(); i++) {
							operbutton=buttonList.get(i);
							if (operbutton!=null) {
								if (operbutton.getName().equals(buttonName)) {
									button=operbutton;
								}
							}
						}
					}
					//设置到session
					OperButtonUtil.setButtons(pageFuncId, buttonList, request);
				}
				if (button!=null) {
					StringBuilder sBuilder=new StringBuilder();
					//输出操作按钮
					sBuilder.append("<input type=\"button\" value=\"");
					sBuilder.append(button.getName());
					sBuilder.append("\" onclick=\"");
					sBuilder.append(button.getCode());
					sBuilder.append("\" ");
					sBuilder.append(buttonAttr);
					sBuilder.append(" />");
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
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
}