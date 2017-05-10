package com.yinhai.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Desc <p>Zuul的过滤器实现我们对外服务的安全控制</p>
 * Package com.yinhai.filter
 * Created by 刘惠涛 on 2017/5/9.
 */
public class AccessFilter extends ZuulFilter{
    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

    /**
     * @return 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
     * pre：在请求被路由之前调用
     * routing：在路由请求时候被调用
     * post：在routing和error过滤器之后被调用
     * error：处理请求时发生错误时被调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * @return 通过int值来定义过滤器的执行顺序
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * @return 返回一个boolean类型来判断该过滤器是否要执行，所以通过此函数可实现过滤器的开关
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * @return 过滤器的具体逻辑。需要注意，这里我们通过ctx.setSendZuulResponse(false)令zuul过滤该请求，不对其进行路由，
     * 然后通过ctx.setResponseStatusCode(401)设置了其返回的错误码，当然我们也可以进一步优化我们的返回，
     * 比如，通过ctx.setResponseBody(body)对返回body内容进行编辑等
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("accessToken");
        if(accessToken == null) {
            log.warn("access token is empty,stop routing");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("accessToken is required");
            return null;
        }
        log.info("access token ok");
        return null;
    }
}
