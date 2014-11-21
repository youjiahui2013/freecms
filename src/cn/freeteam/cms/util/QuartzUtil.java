package cn.freeteam.cms.util;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import cn.freeteam.cms.model.Htmlquartz;

/**
 * 
 * <p>Title: QuartzUtil.java</p>
 * 
 * <p>Description: 调度工具类</p>
 * 
 * <p>Date: Jan 24, 2013</p>
 * 
 * <p>Time: 9:04:40 PM</p>
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
public class QuartzUtil {
	/**
	 * 获取调度工厂
	 * @return
	 */
	public static SchedulerFactory getSchedulerFactory(){
		if (QuartzInitializer.schedFact==null) {
			QuartzInitializer.schedFact = new StdSchedulerFactory();
		}
		return QuartzInitializer.schedFact;
	}
	/**
	 * 获取调度
	 * @return
	 * @throws SchedulerException
	 */
	public static Scheduler getScheduler() throws SchedulerException{
		if (QuartzInitializer.sched==null) {
			QuartzInitializer.sched = getSchedulerFactory().getScheduler();
		}
		return QuartzInitializer.sched;
	}
	/**
	 * 启动调度
	 * @throws SchedulerException
	 */
	public static void startScheduler() throws SchedulerException{
		if (!getScheduler().isStarted()) {
			getScheduler().start();
		}
	}
	/**
	 * 根据htmlquartz生成触发器字符串
	 * @param htmlquartz
	 * @return
	 */
	public static String getTriggerStr(Htmlquartz htmlquartz){
		String triggerStr="";
		if (Htmlquartz.TYPE_EXETIME.equals(htmlquartz.getType()) && 
				(htmlquartz.getExetimehour()>0 || htmlquartz.getExetimemin()>0)) {
			//定时执行
			triggerStr="0 "+htmlquartz.getExetimemin()+" "+htmlquartz.getExetimehour()+" * * ? ";
		}else if (Htmlquartz.TYPE_INTERVAL.equals(htmlquartz.getType())) {
			//间隔重复执行
			//每小时
			if (Htmlquartz.INTERVALTYPE_HOUR.equals(htmlquartz.getIntervaltype()) 
					&& htmlquartz.getIntervalhour()>0) {
				triggerStr="0 0 0/"+htmlquartz.getIntervalhour()+" * * ? ";
			}
			//每分钟
			else if (Htmlquartz.INTERVALTYPE_MIN.equals(htmlquartz.getIntervaltype()) 
					&& htmlquartz.getIntervalmin()>0) {
				triggerStr="0 0/"+htmlquartz.getIntervalmin()+" * * * ? ";
			}
		}
		return triggerStr;
	}
}
