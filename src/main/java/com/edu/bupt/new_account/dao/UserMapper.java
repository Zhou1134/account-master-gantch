package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.User;
import com.edu.bupt.new_account.model.UserMobile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
        /**小程序用户**/
        int deleteByPrimaryKey(Integer id);

        int insert(User record);

        int insertSelective(User record);

        User selectByPrimaryKey(Integer id);

        int updateByPrimaryKeySelective(User record);

        int updateByPrimaryKey(User record);

        User selectByOpenid(String openid);

        void updateByUser(User user);

        List<User> searchAllUser();

        User selectByPhone(String phone);

        User selectByemail(String email);

        void deleteById(Integer id);

        /**移动端用户**/
        int deleteUserMobileByPrimaryKey(Integer id);

        int insertUserMobile(UserMobile record);

        void updateUserMobileOpenId(UserMobile userMobile);

        void updateUserMobilePwd(UserMobile userMobile);

        void updateUserMobileName(UserMobile userMobile);

        UserMobile selectByUserMobileNameAndPwd(UserMobile userMobile);

        UserMobile selectUserMobileByPhone(String phone);

        UserMobile selectUserMobileByEmail(String email);

        UserMobile selectUserMobileByUserName(String name);

        UserMobile selectUserMobileByPrimaryKey(Integer id);

        UserMobile selectUserMobileByOpenId(String openId);
}