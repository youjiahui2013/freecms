package cn.freeteam.cms.util;

import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import cn.freeteam.cms.model.Htmlquartz;
import cn.freeteam.cms.service.HtmlquartzService;
/**
 * 
 * <p>Title: QuartzInitializer.java</p>
 * 
 * <p>Description: 处理所有调度</p>
 * 
 * <p>Date: Jan 24, 2013</p>
 * 
 * <p>Time: 7:27:59 PM</p>
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
public class QuartzInitializer extends HttpServlet{
	public static SchedulerFactory schedFact ;
	public static Scheduler sched;
	public void init(ServletConfig config) throws ServletException { 
		try {
			sched = QuartzUtil.getScheduler();
			QuartzUtil.startScheduler();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		HtmlquartzService htmlquartzService=new HtmlquartzService();
		//查询所有调度
		List<Htmlquartz> htmlquartzList=htmlquartzService.findAll();
		if (htmlquartzList!=null && htmlquartzList.size()>0) {
			//生成一个自动静态化时传递的唯一码
			String htmlQuartaKey=UUID.randomUUID().toString();
			config.getServletContext().setAttribute("htmlQuartaKey",htmlQuartaKey);
			Htmlquartz htmlquartz;
			try {
				for (int i = 0; i < htmlquartzList.size(); i++) {
					htmlquartz=htmlquartzList.get(i);
					
					JobDetail jobDetail = null;
					CronTrigger trigger = null;
					if (htmlquartz.getChannelid()!=null && htmlquartz.getChannelid().trim().length()>0) {
						//栏目静态化调度
						jobDetail = new JobDetail("HtmlChannelJob"+htmlquartz.getChannelid(), "HtmlChannelJob",HtmlChannelJob.class);
						trigger = new CronTrigger("HtmlChannelTrigger"+htmlquartz.getChannelid(), "HtmlChannelTrigger");
					}else if (htmlquartz.getSiteid()!=null && htmlquartz.getSiteid().trim().length()>0) {
						//站点静态化调度
						jobDetail = new JobDetail("HtmlSiteJob"+htmlquartz.getSiteid(), "HtmlSiteJob",HtmlSiteJob.class);
						trigger = new CronTrigger("HtmlSiteTrigger"+htmlquartz.getSiteid(), "HtmlSiteTrigger");
					}
					if (jobDetail!=null && trigger!=null) {
						//设置参数
						jobDetail.getJobDataMap().put("siteid", htmlquartz.getSiteid());
						jobDetail.getJobDataMap().put("channelid", htmlquartz.getChannelid());
						jobDetail.getJobDataMap().put("servletContext", config.getServletContext());
						jobDetail.getJobDataMap().put("htmlQuartaKey", htmlQuartaKey);
						//设置触发器
						String triggerStr=QuartzUtil.getTriggerStr(htmlquartz);
						if (triggerStr.trim().length()>0) {
							trigger.setCronExpression(triggerStr); 
							//添加到调度对列
							sched.scheduleJob(jobDetail, trigger);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("QuartzInitializer失败");
				e.printStackTrace();
			}
		}
	}
}
