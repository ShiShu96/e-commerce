package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.common.TokenCache;
import com.xy.ecommerce.dao.UserMapper;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.UserService;
import com.xy.ecommerce.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response<User> login(String username, String password) {
        Response response=checkNotExist(username, Const.USERNAME);
        if(response.isSuccess()){
            return Response.createByError(ResponseCode.USER_NOT_FOUND);
        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectUser(username, md5Password);
        if(user==null){
            return Response.createByError(ResponseCode.INCORRECT_PASSWORD);
        }
        user.setPassword("");
        return Response.createBySuccess(user);
    }


    @Override
    public Response register(User user) {
        Response response=checkNotExist(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) return response;
        response=checkNotExist(user.getEmail(),Const.EMAIL);
        if (!response.isSuccess()) return response;

        user.setRole(Const.ROLE_CUSTOMER);

        // md5 encrypt
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return Response.createByError();
        }

        return Response.createBySuccess();
    }

    @Override
    public Response checkNotExist(String str, String type) {
        if (!StringUtils.isEmpty(type.trim())){
            if(type.equals(Const.USERNAME)){
                int countName=userMapper.checkUsername(str);
                if(countName>0){
                    return Response.createByError(ResponseCode.USER_NAME_TAKEN);
                }
            }
            if (type.equals(Const.EMAIL)){
                int countEmail=userMapper.checkUsername(str);
                if(countEmail>0){
                    return Response.createByError(ResponseCode.EMAIL_TAKEN);
                }
            }
        } else {
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return Response.createBySuccess();
    }

    @Override
    public Response getQuestion(String username) {
        Response response=checkNotExist(username, Const.USERNAME);
        if (response.isSuccess()) return Response.createByError(ResponseCode.USER_NOT_FOUND);

        String question=userMapper.selectQuestionByUsername(username);

        if (StringUtils.isEmpty(question))
            return Response.createByError(ResponseCode.EMPTY_QUESTION);
        return Response.createBySuccess(question);
    }

    @Override
    public Response checkAnswer(String username, String question, String answer) {
        int count=userMapper.checkAnswer(username, question, answer);
        if (count>0){
            // answer is correct
            String token=UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, token);
            return Response.createBySuccess(token);
        }
        return Response.createByError(ResponseCode.INCORRECT_ANSWER);
    }

    @Override
    public Response resetPassword(String username,String newPassword,String token){
        if(StringUtils.isEmpty(token)){
            return Response.createByError(ResponseCode.EMAIL_TAKEN);
        }
        Response response = this.checkNotExist(username,Const.USERNAME);
        if(response.isSuccess()) return Response.createByError(ResponseCode.USER_NOT_FOUND);

        String storedToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isEmpty(storedToken)){
            return Response.createByError(ResponseCode.INVALID_TOKEN);
        }

        if(token.equals(storedToken)){
            String md5Password  = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if(rowCount > 0){
                return Response.createBySuccess();
            }
        }else{
            return Response.createByError(ResponseCode.TOKEN_NOT_MATCH);
        }
        return Response.createByError();
    }

    @Override
    public Response<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return Response.createByError(ResponseCode.EMAIL_TAKEN);
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return Response.createBySuccess(updateUser);
        }
        return Response.createByError();
    }

    @Override
    public Response<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return Response.createByError(ResponseCode.USER_NOT_FOUND);
        }
        user.setPassword("");
        return Response.createBySuccess(user);

    }
}
