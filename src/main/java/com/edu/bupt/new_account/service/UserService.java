package com.edu.bupt.new_account.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.edu.bupt.new_account.model.Relation;
import com.edu.bupt.new_account.model.Tenant;
import com.edu.bupt.new_account.model.User;
import com.edu.bupt.new_account.model.UserMobile;

import java.util.List;

public interface UserService {
    /**小程序用户**/
    Tenant findTenantByNameAndPasswd(String tenantName, String passwd);

    Integer saveUser(User user);

    User findUserByOpenid(String openid);

    User findUserByemail(String email);

    void updateUserInfo(User user);

    List<User> findAllUser();

    User findUserByphone(String phone);

    void saveRelation(Relation relation);

    List<Relation> getBindedRelations(int bindedId);

    Relation findRelationByBinderAndBinded(int binderId, int bindedId);

    void unbind(Integer id);


    User findUserById(Integer binded);

    List<Relation> findRelationsByBinderID(int binderId);

    void updateRelation(Relation re);

    void deleteUserById(Integer id);

    Boolean is_shared(String old_gatewayids, String new_gatewayids);


    /**移动端用户**/
    Integer saveMobileUser(UserMobile userMobile);

    UserMobile findUserMobileByUserNameAndPassword(String userName, String password);

    UserMobile findUserMobileByUserName(String userName);

    UserMobile findUserMobileByPhoneNumber(String phone);

    UserMobile findUserMobileByEmail(String email);

    UserMobile findUserMobileByOpenId(String openId);

    UserMobile findUserMobileById(Integer binded);

    void updateUserMobileNameById(Integer id,String name);

    void updateUserMobilePwdById(Integer id,String password);

    void updateUserMobileOpenIdById(Integer id,String openid);

    void deleteUserMobileById(Integer id);

}
