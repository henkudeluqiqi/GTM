package org.king2.trm.advanced.controller;

import com.alibaba.fastjson.JSON;
import org.king2.trm.pojo.RedisKey;
import org.king2.trm.rpc.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * =======================================================
 * 说明:  用来处理信息
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/12/16:50          创建
 * =======================================================
 */
@Controller
public class AdvancedController {

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/")
    public String show() {
        return "table/index";
    }

    @RequestMapping("/get")
    @ResponseBody
    public List<List<RpcResponse>> get() {
        Jedis resource = null;
        List<List<RpcResponse>> data = null;
        try {
            resource = jedisPool.getResource ();
            data = new ArrayList<> ();
            List<String> commits = resource.lrange (RedisKey.GTM_COMMIT_KEY + "", 0, -1);
            commits.addAll (resource.lrange (RedisKey.GTM_ROLLBACK_KEY + "", 0, -1));
            for (String commit : commits) {
                data.add (JSON.parseObject (commit, List.class));
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (resource != null) {
                resource.close ();
            }
        }

        return data;
    }


    @RequestMapping("/get/info")
    public String getInfo(String info, HttpServletRequest request) {
        List list = JSON.parseObject (info, List.class);
        request.setAttribute ("fqz", list.get (0));
        list.remove (0);
        request.setAttribute ("result", list);
        return "advanced/index";
    }
}
