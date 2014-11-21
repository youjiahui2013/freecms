package cn.freeteam.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;


import cn.freeteam.model.Operbutton;
import cn.freeteam.service.OperbuttonService;
import cn.freeteam.util.OperButtonUtil;


/**
 * 字符串处理标签
 * 
 * @author freeteam
 *
 */
public class StringTag extends BaseTag{
	
	private String str="";//字符串内容

	private int len=10;//字符串显示长度
	
	public int doStartTag() throws JspException {
		if (str!=null && str.trim().length()>0) {
			if (str.length()>len) {
				str=str.substring(0,len);
			}
			try {
				pageContext.getOut().println(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.SKIP_BODY;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
}
