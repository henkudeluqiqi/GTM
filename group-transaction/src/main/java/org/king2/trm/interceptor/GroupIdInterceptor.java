package org.king2.trm.interceptor;

import org.king2.trm.cache.TransactionCache;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * =======================================================
 * 说明:
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/13:14          创建
 * =======================================================
 */
public class GroupIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取到本次的一个路径 供后面调用错误时检查
        String requestURL = request.getRequestURL ().toString ();
        // 从头部获取groupId
        String groupId = request.getHeader ("GTM_GROUP_ID");
        if (groupId == null) {
            groupId = request.getParameter ("GTM_GROUP_ID");
        }
        TransactionCache.CURRENT_GROUP_ID.set (groupId);
        TransactionCache.CURRENT_REQUEST_URL.set (requestURL);
        return true;
    }
}
