package com.xy.ecommerce.controller.backend;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/backend/user/")
public class BackendUserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login/")
    public Response<User> login(String username, String password, HttpSession session){
        Response<User> response=userService.login(username, password);
        if (response.isSuccess()){
            User user=response.getData();
            if (user.getRole()==Const.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER, user);
            } else {
                return Response.createByError(ResponseCode.NOT_AUTHORIZED);
            }
        }

        return response;
    }
}
