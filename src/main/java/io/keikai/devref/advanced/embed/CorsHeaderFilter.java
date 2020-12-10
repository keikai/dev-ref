package io.keikai.devref.advanced.embed;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An example to enable Cross-Origin Resource Sharing with a Filter. <br/>
 * Only for demo purposes - not for production use ! <br/>
 * <a href="https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/Integration/Miscellenous/Embedded_ZK_Application">Embedded_ZK_Application</a>
 */
public class CorsHeaderFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		res.addHeader("Access-Control-Allow-Origin", "http://localhost:9000");
		res.addHeader("Access-Control-Allow-Headers", "zk-sid");
		res.addHeader("Access-Control-Expose-Headers", "zk-sid, zk-error");
		res.addHeader("Access-Control-Allow-Credentials", "true");
		res.addHeader("Access-Control-Allow-Methods", "GET, POST");
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
