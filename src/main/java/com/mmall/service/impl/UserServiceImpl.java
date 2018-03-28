package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by jianl
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password) {
        //1.校验用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            //不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //2.对password进行md5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        //3.根据username和password进行查询，校验密码是否正确
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            //密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //4.将密码设为""
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //1.校验用户名是否存在
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    //用户名存在
                    return  ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            //2.校验email是否存在
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    //存在
                    return  ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> register(User user) {
        //1.校验用户名是否存在
        int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount > 0){
            //存在
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        //2.校验email是否存在
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0){
            //存在
            return ServerResponse.createByErrorMessage("email已存在");
        }
        //3.设置user的值
        user.setRole(Const.Role.ROLE_ADMIN);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //4.执行插入
        int count = userMapper.insert(user);
        if(count ==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username){
        //1.校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            //不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //2.根据用户名查询问题
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        //1.校验用户名、问题和答案
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount >0){
            //问题和答案是用户的且是正确的，则生成Token并添加到缓存中
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }


    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //1.forgetToken参数错误
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //2.校验用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //3.获取token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        //4.判断token是否失效
        if(StringUtils.isBlank(token)){
            //失效
            return ServerResponse.createByErrorMessage("token已经失效");
        }
        //5.判断token与forgetToken是否一致
        if(StringUtils.equals(token,forgetToken)){
            //若一致，则根据username进行密码的更新
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            return ServerResponse.createByErrorMessage("修改密码失败");
        }else{
            //若不一致
            return  ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //1.根据passwordOld和userId查询，防止横向越权
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            //旧密码错误
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //2.对passwordNew进行md5加密处理
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //3.执行更新
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //1.校验email是否唯一
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        //2.创建updateUser对象，并设置其值
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        //3.执行更新
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount > 0){
            updateUser.setUsername(user.getUsername());
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
