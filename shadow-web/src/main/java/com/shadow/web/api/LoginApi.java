package com.shadow.web.api;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wangzhendong
 * @Date: 2019/11/11 21:57
 * @Description:
 */
public class LoginApi {

    @PostMapping("/user/login")
    public Map<String,Object> login(@RequestBody Map<String,Object> params){
        Map<String,Object> result = new HashMap<>();
        String password = (String)params.get("password");
        String userName = (String)params.get("userName");
        String type = (String)params.get("type");
        if (password.equals("admin")&& userName.equals("admin")) {
            result.put("status", "ok");
            result.put("type", "type");
            result.put("currentAuthority", "admin");
            return result;
        }
        result.put("status", "error");
        result.put("type", type);
        result.put("currentAuthority", "'guest'");
        return result;
    }

    @RequestMapping("/api/currentUser")
    public Map<String,Object> getCurrentUser(){
        Map<String,Object> result = new HashMap<>();
        result.put("name","admin");
        return result;
    }

}
