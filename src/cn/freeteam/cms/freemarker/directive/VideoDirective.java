package cn.freeteam.cms.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

import cn.freeteam.base.BaseDirective;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * <p>Title: VideoDirective.java</p>
 * 
 * <p>Description: 视频播放标签</p>
 * 参数 
 * video		视频地址
 * img			视频截图地址
 * width		视频播放窗口宽度,默认300
 * height		视频播放窗口高度,默认200
 * 
 * 返回值
 * html   生成的html
 * 
 * 示例
<@video video="${currInfo.video}" ;html>
${html}											
</@video>
 * <p>Date: Jan 16, 2013</p>
 * 
 * <p>Time: 8:36:43 PM</p>
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
public class VideoDirective extends BaseDirective implements TemplateDirectiveModel{

	public void execute(Environment env, Map params, TemplateModel[] loopVars, 
			TemplateDirectiveBody body)throws TemplateException, IOException {
		
		//获取参数
		//视频字符串
		String video=getParam(params, "video");
		//视频播放窗口宽度
		String width=getParam(params, "width","300");
		//视频播放窗口高度
		String height=getParam(params, "height","200");
		//视频截图地址
		String img=getParam(params, "img");
		
		if (body!=null) {
			//设置循环变量
			if (loopVars!=null && loopVars.length>0 && video.trim().length()>0) {
				String contextPath=env.getDataModel().get("contextPath").toString();
				StringBuilder sb=new StringBuilder();
				//播放代码
				sb.append("<object type='application/x-shockwave-flash' data='"+contextPath+"js/player_flv_maxi.swf' width='"+width+"' height='"+height+"'>");
				sb.append("<param name='movie' value='"+contextPath+"js/player_flv_maxi.swf' /> ");
				sb.append("<param name='allowFullScreen' value='true' /> ");
				sb.append("<param name='FlashVars' value='" +
						"flv="+(!video.startsWith("http:")&&!video.startsWith("https:")&&!video.startsWith(contextPath)?contextPath:"")+video+"&amp;" +
						"startimage="+(!img.startsWith("http:")&&!img.startsWith("https:")&&!img.startsWith(contextPath)?contextPath:"")+img+"&amp;" +
						"showstop=1&amp;showvolume=1&amp;showtime=1&amp;showplayer=always&amp;showloading=always&amp;showfullscreen=1' />"); 
				sb.append("</object>");
				loopVars[0]=new StringModel(sb.toString(),new BeansWrapper());  
				body.render(env.getOut()); 
			}
		}
	}
}
