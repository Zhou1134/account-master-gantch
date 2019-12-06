package com.edu.bupt.new_account.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.edu.bupt.new_account.dao.*;
import com.edu.bupt.new_account.model.*;
import com.edu.bupt.new_account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RelationMapper relationMapper;


    private static int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);

    /**小程序用户**/

    @Override
    public Tenant findTenantByNameAndPasswd(String tenantName, String passwd) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setPassword(passwd);
        return tenantMapper.selectByNameAndPassword(tenant);
    }

    @Override
    public User findUserByemail(String email) {return userMapper.selectByemail(email);}

    @Override
    public Integer saveUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User findUserByOpenid(String openid) {
        return userMapper.selectByOpenid(openid);
    }

    @Override
    public void updateUserInfo(User user) {
        userMapper.updateByUser(user);
    }

    @Override
    public List<User> findAllUser() {
        return userMapper.searchAllUser();
    }

    @Override
    public User findUserByphone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    // TODO: 2019/12/5 完成保存绑定者关系
    @Override
    public void saveRelation(Relation relation) {
        relationMapper.insert(relation);
    }

    @Override
    public List<Relation> getBindedRelations(int bindedId) {
        return relationMapper.getBindedRelations(bindedId);
    }

    @Override
    public Relation findRelationByBinderAndBinded(int binderId, int bindedId) {
        Relation re = new Relation();
        re.setBinder(binderId);
        re.setBinded(bindedId);
        return relationMapper.getRelationBy2Bind(re);
    }

    @Override
    public void unbind(Integer id) {
        relationMapper.deleteByPrimaryKey(id);
    }


    @Override
    public User findUserById(Integer binded) {
        return userMapper.selectByPrimaryKey(binded);
    }

    @Override
    public List<Relation> findRelationsByBinderID(int binderId) {
        return relationMapper.getRelationsByBinderId(binderId);
    }

    // TODO: 2019/12/5 完成更新绑定者关系
    @Override
    public void updateRelation(Relation re) {
        relationMapper.updateByPrimaryKey(re);
    }

    public void deleteUserById(Integer id){
        userMapper.deleteById(id);
    }

    public Boolean is_shared(String old_gatewayids, String new_gatewayids){
        String [] gatewayids = new_gatewayids.split(",");
        for (String gatewayid: gatewayids){
            if (old_gatewayids.contains(gatewayid)){
                return true;
            }
        }
        return false;
    }




    /**移动端用户**/
    @Override
    public Integer saveMobileUser(UserMobile userMobile) {
        return userMapper.insertUserMobile(userMobile);
    }

    @Override
    public UserMobile findUserMobileByUserNameAndPassword(String userName, String password) {
        UserMobile userMobile=new UserMobile();
        userMobile.setUsername(userName);
        userMobile.setPassword(password);
        return userMapper.selectByUserMobileNameAndPwd(userMobile);
    }

    @Override
    public UserMobile findUserMobileByUserName(String userName) {
        return userMapper.selectUserMobileByUserName(userName);
    }

    @Override
    public UserMobile findUserMobileByPhoneNumber(String phone) {
        return userMapper.selectUserMobileByPhone(phone);
    }

    @Override
    public UserMobile findUserMobileByEmail(String email) {
        return userMapper.selectUserMobileByEmail(email);
    }

    @Override
    public UserMobile findUserMobileByOpenId(String openId) {
        return userMapper.selectUserMobileByOpenId(openId);
    }

    @Override
    public void updateUserMobileNameById(Integer id, String name) {
        UserMobile userMobile1=new UserMobile();
        userMobile1.setId(id);
        userMobile1.setName(name);
        userMapper.updateUserMobileName(userMobile1);
    }

    @Override
    public void updateUserMobilePwdById(Integer id, String password) {
        UserMobile userMobile1=new UserMobile();
        userMobile1.setId(id);
        userMobile1.setPassword(password);
        userMapper.updateUserMobilePwd(userMobile1);
    }

    @Override
    public void updateUserMobileOpenIdById(Integer id, String openid) {
        UserMobile userMobile=new UserMobile();
        userMobile.setId(id);
        userMobile.setOpenid(openid);
        userMapper.updateUserMobileOpenId(userMobile);
    }

    @Override
    public void deleteUserMobileById(Integer id) {
        userMapper.deleteUserMobileByPrimaryKey(id);
    }

    @Override
    public UserMobile findUserMobileById(Integer binded) {
        return userMapper.selectUserMobileByPrimaryKey(binded);
    }


}
