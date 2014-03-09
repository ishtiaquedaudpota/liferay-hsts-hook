package de.olafkock.liferay.hsts;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HSTSFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		// run only if request comes through https
		if(request.isSecure() && maxAge > 0) {
			try {
				((HttpServletResponse)response).addHeader("Strict-Transport-Security", "max-age=" + maxAge);
			} catch (Exception ignore) {
				_log.error("ignored exception in HSTS Filter. No header added. (" + 
						ignore.getClass().getName() + " " + ignore.getMessage() + ")");
			}
		} else {
			if(_log.isDebugEnabled())
				_log.debug("request is insecure - no HSTS Header added");
		}
		filter.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		maxAge = Long.valueOf(config.getInitParameter("max-age"));
		if(PropsUtil.contains("hsts-max-age")) {
			maxAge = Long.valueOf(PropsUtil.get("hsts-max-age"));
		}
		if(_log.isDebugEnabled())
			_log.debug("HSTS-Filter configured for max-age " + maxAge);
	}

	long maxAge = 0L;
	private static Log _log = LogFactoryUtil.getLog(HSTSFilter.class);
}
