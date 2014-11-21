package cn.freeteam.util;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


import cn.freeteam.dao.OperlogsMapper;
import cn.freeteam.model.Operlogs;


/**
 * 操作日志工具类
 * @author freeteam
 *2011-3-6
 */
public class OperLogUtil {

	public static void log(String loginname,String content,HttpServletRequest request){
		try {
			if (content!=null && content.trim().length()>0) {
				OperlogsMapper mapper=MybatisSessionFactory.getSession().getMapper(OperlogsMapper.class);
				Operlogs log=new Operlogs();
				log.setId(UUID.randomUUID().toString());
				log.setContent(content);
				log.setIp(request.getRemoteAddr());
				log.setLoginname(loginname);
				log.setOpertime(new Date());
				
				mapper.insert(log);
				MybatisSessionFactory.getSession().commit();
			}
		} catch (Exception e) {
			try {
				MybatisSessionFactory.getSession().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
