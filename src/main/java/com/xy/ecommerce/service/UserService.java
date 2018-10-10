package com.xy.ecommerce.service;

import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.entity.User;

public interface UserService {

    Response<User> login(String username, String password);

    Response<String> register(User user);

    Response<String> checkNotExist(String str, String type);

    Response<String> getQuestion(String username);

    Response<String> checkAnswer(String username, String question, String answer);

    Response<String> resetPassword(String username,String newPassword, String token);

    Response<User> updateInformation(User user);

    Response<User> getInformation(Integer userId);
}
