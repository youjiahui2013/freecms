package cn.freeteam.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpSession;

import cn.freeteam.cms.model.Member;
import cn.freeteam.model.Users;




public class MemberLoginFilter implements Filter {
	protected final static String LOGIN_PAGE = "mlogin.jsp";
	protected FilterConfig filterConfig;

	/**
	 * 过滤处理的方法
	 */
	public void doFilter(final ServletRequest req, final ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hres = (HttpServletResponse) res;
		HttpSession session = hreq.getSession();
		Member member;
		try {
			member = (Member) session.getAttribute("loginMember");
			if (member!=null) {
				chain.doFilter(req, res);
				return;
			} else {
				String path = hreq.getContextPath();
				String basePath = hreq.getScheme() + "://" + hreq.getServerName() + ":"
						+ hreq.getServerPort() + path + "/";
				hres.sendRedirect(basePath + LOGIN_PAGE);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		chain.doFilter(req, res);
	}

	public void setFilterConfig(final FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * 销毁过滤器
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * 初始化过滤器,和一般的Servlet一样，它也可以获得初始参数。
	 */
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
	}

}
