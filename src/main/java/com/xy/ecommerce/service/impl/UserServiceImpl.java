package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
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
        Response<String> response=checkNotExist(username, Const.USERNAME);
        if(response.isSuccess()){
            return Response.createByErrorMessage("user name not exists");
        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectUser(username, md5Password);
        if(user==null){
            return Response.createByErrorMessage("invalid password");
        }
        user.setPassword("");
        return Response.createBySuccess("logged in successfully", user);
    }


    @Override
    public Response<String> register(User user) {
        Response<String> response=checkNotExist(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) return response;
        response=checkNotExist(user.getEmail(),Const.EMAIL);
        if (!response.isSuccess()) return response;

        user.setRole(Const.ROLE_CUSTOMER);

        // md5 encrypt
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return Response.createByErrorMessage("failed to create user");
        }

        return Response.createBySuccessMessage("user created successfully!");
    }

    @Override
    public Response<String> checkNotExist(String str, String type) {
        if (!StringUtils.isEmpty(type.trim())){
            if(type.equals(Const.USERNAME)){
                int countName=userMapper.checkUsername(str);
                if(countName>0){
                    return Response.createByErrorMessage("user name already exists");
                }
            }
            if (type.equals(Const.EMAIL)){
                int countEmail=userMapper.checkUsername(str);
                if(countEmail>0){
                    return Response.createByErrorMessage("email already exists");
                }
            }
        } else {
            return Response.createByErrorMessage("invalid parameters");
        }
        return Response.createBySuccessMessage("success check");
    }

    @Override
    public Response<String> getQuestion(String username) {
        Response<String> response=checkNotExist(username, Const.USERNAME);
        if (response.isSuccess()) return Response.createByErrorMessage("user not exists");

        String question=userMapper.selectQuestionByUsername(username);

        if (StringUtils.isEmpty(question))
            return Response.createByErrorMessage("empty question");
        return Response.createBySuccess(question);
    }

    @Override
    public Response<String> checkAnswer(String username, String question, String answer) {
        int count=userMapper.checkAnswer(username, question, answer);
        if (count>0){
            // answer is correct
            String token=UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, token);
            return Response.createBySuccess(token);
        }
        return Response.createByErrorMessage("incorrect answer");
    }

    @Override
    public Response<String> resetPassword(String username,String newPassword,String token){
        if(StringUtils.isEmpty(token)){
            return Response.createByErrorMessage("token needed");
        }
        Response response = this.checkNotExist(username,Const.USERNAME);
        if(response.isSuccess()) return Response.createByErrorMessage("user not exists");

        String storedToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isEmpty(storedToken)){
            return Response.createByErrorMessage("invalid token");
        }

        if(token.equals(storedToken)){
            String md5Password  = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if(rowCount > 0){
                return Response.createBySuccessMessage("reset password successfully");
            }
        }else{
            return response.createByErrorMessage("incorrect token");
        }
        return Response.createByErrorMessage("failed to reset password");
    }

    @Override
    public Response<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return Response.createByErrorMessage("email already exists");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return Response.createBySuccess("updated personal information successfully",updateUser);
        }
        return Response.createByErrorMessage("failed to update personal information");
    }

    @Override
    public Response<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return Response.createByErrorMessage("user not found");
        }
        user.setPassword("");
        return Response.createBySuccess(user);

    }
}
