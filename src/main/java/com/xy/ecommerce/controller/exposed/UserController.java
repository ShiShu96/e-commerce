package com.xy.ecommerce.controller.exposed;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public Response<User> login(String username, String password, HttpSession httpSession){
        Response<User> response=userService.login(username, password);
        if (response.isSuccess()){
            httpSession.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    public Response<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return Response.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    public Response<String> register(User user){
        return userService.register(user);
    }

    @RequestMapping(value = "check_not_exist.do", method = RequestMethod.GET)
    public Response<String> checkValid(String str, String type){
        return userService.checkNotExist(str,type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    public Response<User> getUserInfo(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createBySuccess(user);
        }
        return Response.createByError(ResponseCode.NEED_LOGIN);
    }

    @RequestMapping(value = "get_question.do",method = RequestMethod.GET)
    public Response<String> getQuestion(String username){
        return userService.getQuestion(username);
    }

    @RequestMapping(value = "check_answer.do",method = RequestMethod.POST)
    public Response<String> forgetCheckAnswer(String username,String question,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    public Response<String> restPassword(String username,String newPassword,String token){
        return userService.resetPassword(username, newPassword, token);
    }

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    public Response<User> update_information(User user, HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        Response<User> response = userService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return response;
    }

}
