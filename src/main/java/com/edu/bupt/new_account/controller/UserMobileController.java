package com.edu.bupt.new_account.controller;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.edu.bupt.new_account.model.Relation;
import com.edu.bupt.new_account.model.Result;
import com.edu.bupt.new_account.model.SMS;
import com.edu.bupt.new_account.model.UserMobile;
import com.edu.bupt.new_account.service.RuleService;
import com.edu.bupt.new_account.service.UserService;
import com.edu.bupt.new_account.util.AliyunSmsUtils;
import com.edu.bupt.new_account.util.MD5Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author lcw332
 * @group Gantch
 * @time 2019年12月4日14:02:17
 */
@RestController
@RequestMapping("/api/v1/mobile")
@Transactional(rollbackFor = Exception.class)
public class UserMobileController {



    @Autowired
    private UserService userService;


    @Autowired
    private RuleService ruleService;

    /**
     * ANDROID OR IOS 用户注册
     *
     * @param info
     * @return result
     */
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ResponseBody
    public Result createMobileUser(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        String name = jsonInfo.get("name").getAsString();
        String userName = jsonInfo.get("userName").getAsString();
        String password = jsonInfo.get("passWord").getAsString();
        String email = jsonInfo.get("email").getAsString();
        String phone = jsonInfo.get("phone").getAsString();
        String openId = jsonInfo.get("openId").getAsString();
        try {
            UserMobile userNameResult = userService.findUserMobileByUserName(userName);
            if (userNameResult != null) {
                result.setStatus("error");
                result.setResultMsg("创建失败，用户名已存在");
                return result;
            }
            UserMobile phoneNumberResult = userService.findUserMobileByPhoneNumber(phone);
            if (phoneNumberResult != null) {
                result.setStatus("error");
                result.setResultMsg("创建失败，手机号已存在!");
                return result;
            }
//            UserMobile emailResult = userService.findUserMobileByEmail(email);
//            if (emailResult != null) {
//                result.setStatus("error");
//                result.setResultMsg("创建失败，邮箱地址已存在");
//                return result;
//            }
            if(!openId.equals("")) {
                UserMobile openIdResult = userService.findUserMobileByOpenId(openId);
                if (openIdResult != null) {
                    result.setStatus("error");
                    result.setResultMsg("创建失败，该用户已绑定微信");
                    return result;
                }
            }
            UserMobile userMobile = new UserMobile();
            userMobile.setName(name);
            userMobile.setUsername(userName);
            userMobile.setPassword(MD5Util.MD5EncodeUtf8(password)); //MD5加密
            userMobile.setPhone(phone);
            userMobile.setEmail(email);
            userMobile.setOpenid(openId);

            userService.saveMobileUser(userMobile);
            int customerid = userService.findUserMobileByUserName(userName).getId();
            userMobile.setId(customerid);
            result.setResultMsg("create success");
            result.setData(userMobile);

        } catch (Exception e) {
            userService.deleteUserMobileById(userService.findUserMobileByUserName(userName).getId());
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("some error!");
        } finally {
            return result;
        }
    }
    /**
     * 注册时，发送验证码，返回验证码
     *
     * @param phone
     * @return result
     */
    @RequestMapping(value = "/smsCode/{phone}", method = RequestMethod.GET)
    @ResponseBody
    public Result smsCode(@PathVariable("phone") String phone) {
        Result result = new Result();
        try{
            SendSmsResponse response = AliyunSmsUtils.sendSms(phone);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            Thread.sleep(2000L);
            //查明细
            if(response.getCode() != null && response.getCode().equals("OK")) {
                QuerySendDetailsResponse querySendDetailsResponse= AliyunSmsUtils.querySendDetails(response.getBizId(),phone);
                System.out.println("短信明细查询接口返回数据----------------");
                System.out.println("Code=" + querySendDetailsResponse.getCode());
                System.out.println("Message=" + querySendDetailsResponse.getMessage());
                SMS sms=new SMS();
                for(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs())
                {
                    System.out.println("Content=" + smsSendDetailDTO.getContent());
                    System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                    System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                    System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                    System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                    sms.setContent(smsSendDetailDTO.getContent());
                    sms.setPhoneNumber(phone);
                    sms.setSendDate(smsSendDetailDTO.getSendDate());
                }
                result.setData(sms);
             }
         }catch (Exception e){
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("错误的手机号");
        }finally {
            return result;
        }
    }

    /**
     * 用户普通登录
     *
     * @param info
     * @return result
     */
    @RequestMapping(value = "/mobileUserLogin", method = RequestMethod.POST)
    @ResponseBody
    public Result mobileUserLogin(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        try {
            String userName = jsonInfo.get("userName").getAsString();
            String password = jsonInfo.get("passWord").getAsString();
            UserMobile userMobile = userService.findUserMobileByUserNameAndPassword(userName, MD5Util.MD5EncodeUtf8(password));
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("用户名或密码有误");
                return result;
            }
            result.setResultMsg("用户存在,登录成功");
            result.setData(userMobile);
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("用户不存在,登录失败");
        } finally {
            return result;
        }
    }



    /**
     * 用户信息更改接口 更新用户OpenId
     *
     * @param Info
     * @return result
     */
    @RequestMapping(value = "/userModifyOpenId", method = RequestMethod.POST)
    @ResponseBody
    public Result userModifyOpenId(@RequestBody String Info){
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try{
            String id =info.get("id").getAsString();
            String openId =info.get("openid").getAsString();
            Integer userId = Integer.parseInt(id);
            UserMobile userMobile = userService.findUserMobileById(userId);
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("未找到该用户");
                return result;
            }
            UserMobile openIdResult = userService.findUserMobileByOpenId(openId);
            if (openIdResult !=null){
                result.setStatus("error");
                result.setResultMsg("该用户已绑定过微信");
                return result;
            }
            userService.updateUserMobileOpenIdById(userId,openId);
        }catch (Exception e){
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("用户不存在");
        } finally {
            return result;
        }
    }


    /**
     * 通过openid查看用户是否存在
     *
     * @param Info
     * @return result
     */
    @RequestMapping(value = "/checkOpenId", method = RequestMethod.POST)
    @ResponseBody
    public Result usercheckOpenId(@RequestBody String Info){
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try{
            String openId =info.get("openid").getAsString();
            UserMobile userMobile = userService.findUserMobileByOpenId(openId);
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("未找到该用户");
                return result;
            }else{
                result.setStatus("success");
                result.setData(userMobile);
                result.setResultMsg("用户存在");
            }
        }catch (Exception e){
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("未知错误");
        } finally {
            return result;
        }
    }

    /**
     * 判断用户名是否在表中
     *
     * @param info
     * @return result
     */
    @RequestMapping(value = "/checkUser", method = RequestMethod.POST)
    @ResponseBody
    public Result mobileCheckUser(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        try {
            String userName = jsonInfo.get("userName").getAsString();
            UserMobile userMobile = userService.findUserMobileByUserName(userName);
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("用户名不存在");
                return result;
            }
            result.setResultMsg("用户存已存在");
            result.setData(userMobile);
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("错误");
        } finally {
            return result;
        }
    }


    /**
     * 用户信息更改接口 修改用户密码
     *
     * @param info
     * @return result
     */
    @RequestMapping(value = "/userModifyPwd", method = RequestMethod.POST)
    @ResponseBody
    public Result userModifyPwd(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        try {
            String id = jsonInfo.get("id").getAsString();
            Integer idResult= Integer.parseInt(id);
            String newPassword = jsonInfo.get("newPassword").getAsString();
            UserMobile userMobile = userService.findUserMobileById(idResult);
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("未找到该用户");
                return result;
            }
            userService.updateUserMobilePwdById(idResult, MD5Util.MD5EncodeUtf8(newPassword));
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("用户不存在");
        } finally {
            return result;
        }
    }

    /**
     * 用户信息更改接口 用户名
     *
     * @param info
     * @return result
     */
    @RequestMapping(value = "/userModifyName" , method = RequestMethod.POST)
    @ResponseBody
    public Result userModifyName(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        try {
            Integer id = jsonInfo.get("id").getAsInt();
            String newName = jsonInfo.get("newName").getAsString();
            UserMobile userMobile = userService.findUserMobileById(id);
            if (userMobile == null) {
                result.setStatus("error");
                result.setResultMsg("未找到该用户");
                return result;
            }
            userService.updateUserMobileNameById(id,newName);
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("操作错误");
        }finally {
            return result;
        }
    }

    /**
     * 绑定所属主用户及其网关   A 分享给 B
     * @param Info
     * @return
     */
    @RequestMapping(value = "/bindGate", method = RequestMethod.POST)
    @ResponseBody
    public Result bindGate(@RequestBody String Info){
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try{
            String binded = info.get("customerid").getAsString();
            String phone = info.get("phone").getAsString();
            String gateids = info.get("gateids").getAsString();
            String remark = info.get("remark").getAsString();

            UserMobile userMobile=userService.findUserMobileByPhoneNumber(phone);
            if (userMobile == null){
                result.setStatus("error");
                result.setResultMsg("该用户不存在");
                return result;
            }
            Relation re = userService.findRelationByBinderAndBinded(userMobile.getId(),Integer.parseInt(binded));
            if (re !=null){
                if (userService.is_shared(re.getGateid(), gateids)){
                    result.setStatus("error");
                    result.setResultMsg("网关重复分享");
                    return result;
                } else{
                    String old_gatewayids = re.getGateid();
                    re.setGateid(old_gatewayids + "," + gateids);
                    userService.updateRelation(re);
                    result.setResultMsg("绑定成功");
                }
            }else{
                Relation relation = new Relation();
                relation.setBinded(Integer.parseInt(binded));
                relation.setBinder(userMobile.getId());
                relation.setGateid(gateids);
                relation.setRemark(remark);
                userService.saveRelation(relation);
                result.setResultMsg("绑定成功");
            }
        }catch (Exception e){
            result.setStatus("error");
            result.setResultMsg("插入失败");
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    /**
     * 获取绑定者对应被绑定者电话及其网关  A分享给B， B要获取A信息
     * @param Info
     * @return
     */
    @RequestMapping(value = "/getBinderGates", method = RequestMethod.POST)
    @ResponseBody
    public Result getBinderGates(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binder = info.get("customerid").getAsString();
            int binderId = Integer.parseInt(binder);
            // 查找是否存在别人分享的网关
            List<Relation> relations = userService.findRelationsByBinderID(binderId);
            if (relations.size() == 0) {
                result.setStatus("error");
                result.setResultMsg("该绑定关系不存在");
                return result;
            }
            List list = new ArrayList();
            for (Relation relation : relations) {
                list.add(relation.getGateid());
            }
            result.setData(list);
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("操作失败");
        } finally {
            return result;
        }
     }

    /**
     * 解绑被绑定者及其网关  A 分享给 B  A 要取消对 B 的分享
     * @param Info
     * @return
     */
    @RequestMapping(value = "/unBindedGate", method = RequestMethod.POST)
    @ResponseBody
    public Result unBindedGate(@RequestBody String Info){
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            String phone = info.get("phone").getAsString();
            String gateids = info.get("gateids").getAsString();
            UserMobile userMobile = userService.findUserMobileByPhoneNumber(phone);
            int bindedId = Integer.parseInt(binded);
            int binderId = userMobile.getId();
            Relation relation = userService.findRelationByBinderAndBinded(binderId, bindedId);
            if (relation == null) {
                result.setStatus("error");
                result.setResultMsg("不存在该绑定关系");
                return result;
            }
            //删除绑定关系
            userService.unbind(relation.getId());
            String[] neededUnbindGates = gateids.split(",");
            String[] originGates = relation.getGateid().split(",");
            String newGates = "";
            int judge = 0;
            for (String i : originGates) {
                if (!Arrays.asList(neededUnbindGates).contains(i)) {
                    newGates += i + ',';
                    judge = 1;
                }
            }
            if (judge == 1) {
                newGates = newGates.substring(0, newGates.length() - 1);
            }
            if (newGates != "") {
                relation.setBinder(binderId);
                relation.setBinded(bindedId);
                relation.setGateid(newGates);
                userService.saveRelation(relation);
            }
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("解除失败");
        } finally {
            return result;
        }
    }

    /**
     * 解绑被绑定者及其网关 只有一个网关id  网关拥有者取消所有分享
     * @param Info
     * @return
     */
    @RequestMapping(value = "/unBindedALLGate", method = RequestMethod.POST)
    @ResponseBody
    public Result unBindedALLGate(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            String gateid = info.get("gateid").getAsString();
            int bindedId = Integer.parseInt(binded);
            List<Relation> relations = userService.getBindedRelations(bindedId);

            if (relations.size() == 0) {
                result.setStatus("error");
                result.setResultMsg("不存在该绑定关系");
                return result;
            }

            for(Relation re:relations){
                //删除绑定关系
                String re_gates = re.getGateid();
                if(re_gates.indexOf(gateid)==-1){
                    continue;
                }

                String[] originGates = re.getGateid().split(",");
                String newGates = "";
                int judge = 0;
                for (String i : originGates) {
                    if (!Arrays.asList(gateid).contains(i)) {
                        newGates += i + ',';
                        judge = 1;
                    }
                }
                if (judge == 1) {
                    newGates = newGates.substring(0, newGates.length() - 1);
                }
                if (newGates == "") {
                    userService.unbind(re.getId());
                }else {
                    re.setGateid(newGates);
                    userService.updateRelation(re);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("解除失败");
        } finally {
            return result;
        }
    }

    /**
     * 深度解绑网关
     * @param Info
     * @return
     */
    @RequestMapping(value = "/deepUnBindedALLGate", method = RequestMethod.POST)
    public Result clearGateway(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            String gateid = info.get("gateid").getAsString();
            int bindedId = Integer.parseInt(binded);
            List<Relation> relations = userService.getBindedRelations(bindedId);

            if (relations.size() == 0) {
                result.setStatus("error");
                result.setResultMsg("不存在该绑定关系");
                return result;
            }

            for(Relation re:relations){
                //删除绑定关系
                String re_gates = re.getGateid();
                if(re_gates.indexOf(gateid)==-1){
                    continue;
                }

                String[] originGates = re.getGateid().split(",");
                String newGates = "";
                int judge = 0;
                for (String i : originGates) {
                    if (!Arrays.asList(gateid).contains(i)) {
                        newGates += i + ',';
                        judge = 1;
                    }
                }
                if (judge == 1) {
                    newGates = newGates.substring(0, newGates.length() - 1);
                }
                if (newGates == "") {
                    userService.unbind(re.getId());
                }else {
                    re.setGateid(newGates);
                    userService.updateRelation(re);
                }
            }

            URL url = new URL("http://47.105.120.203:30080/api/v1/smartruler/removeRules/"+gateid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.connect();
            if (conn.getResponseCode() == 200){
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer res = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null){
                    res.append(temp);
                }
                if (!res.equals("OK")){
                    result.setStatus("error");
                    result.setResultMsg("删除规则失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setResultMsg("删除规则失败");
        }
        return result;
    }

    /**
     * 解绑绑定者及其网关
     * @param Info
     * @return
     */
    @RequestMapping(value = "/unBinderGates", method = RequestMethod.POST)
    @ResponseBody
    public Result unBinderGates(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binder = info.get("customerid").getAsString();
            String gateid = info.get("gateids").getAsString();
            List<Relation> relations = userService.findRelationsByBinderID(Integer.parseInt(binder));
            if (relations.size() == 0) {
                result.setStatus("error");
                result.setResultMsg("不存在该绑定关系");
                return result;
            }

            for (Relation re : relations) {
                String gates = re.getGateid();
                if (gates.indexOf(gateid) != -1) {
                    //删除绑定关系
                    userService.unbind(re.getId());
                    String[] originGates = re.getGateid().split(",");
                    String newGates = "";
                    int judge = 0;
                    for (String i : originGates) {
                        if (!i.equals(gateid)) {
                            newGates += i + ',';
                            judge = 1;
                        }
                    }
                    if (judge == 1) {
                        newGates = newGates.substring(0, newGates.length() - 1);
                    }
                    Relation relation = new Relation();
                    if (newGates != "") {
                        relation.setBinder(re.getBinder());
                        relation.setBinded(re.getBinded());
                        relation.setGateid(newGates);
                        userService.saveRelation(relation);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("解除失败");
        } finally {
            result.setResultMsg("解除成功");
            return result;
        }
    }

    /**
     * 查询被绑定用户所对应的网关
     * @param loginInfo
     * @return
     */
    @RequestMapping(value = "/getGates", method = RequestMethod.POST)
    @ResponseBody
    public Result getGates(@RequestBody String loginInfo) {
        JsonObject info = new JsonParser().parse(loginInfo).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            int bindedId = Integer.parseInt(binded);
            List<Relation> relations = userService.getBindedRelations(bindedId);
            if (relations.size() == 0) {
                result.setStatus("error");
                result.setResultMsg("未找到绑定网关");
                return result;
            }
            List list = new ArrayList();

            for (Relation relation : relations) {
                UserMobile userMobile = userService.findUserMobileById(relation.getBinder());
                Map map = new HashMap();
                map.put("phone", userMobile.getPhone());
                map.put("gates", relation.getGateid());
                map.put("remark",relation.getRemark());
                list.add(map);
            }
            result.setData(list);

        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
        } finally {
            return result;
        }
    }

    // TODO: 2019/12/6 不知道什么原因开了这个方法，就会报错

    //解绑被绑定者及其网关 只有一个网关id  网关拥有者取消所有分享
//    @RequestMapping(value = "/deepUnBindedALLGate", method = RequestMethod.POST)
//    @ResponseBody
//    public Result deepUnBindedALLGate(@RequestBody String Info) {
//        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
//        Result result = new Result();
//        try {
//            String binded = info.get("customerid").getAsString();
//            String gateid = info.get("gateid").getAsString();
//            int bindedId = Integer.parseInt(binded);
//            List<Relation> relations = userService.getBindedRelations(bindedId);
//
//            if (relations.size() == 0) {
//                result.setStatus("error");
//                result.setResultMsg("不存在该绑定关系");
//                return result;
//            }
//
//            for(Relation re:relations){
//                //删除绑定关系
//                String re_gates = re.getGateid();
//                if(re_gates.indexOf(gateid)==-1){
//                    continue;
//                }
//
//                String[] originGates = re.getGateid().split(",");
//                String newGates = "";
//                int judge = 0;
//                for (String i : originGates) {
//                    if (!Arrays.asList(gateid).contains(i)) {
//                        newGates += i + ',';
//                        judge = 1;
//                    }
//                }
//                if (judge == 1) {
//                    newGates = newGates.substring(0, newGates.length() - 1);
//                }
//                if (newGates == "") {
//                    userService.unbind(re.getId());
//                }else {
//                    re.setGateid(newGates);
//                    userService.updateRelation(re);
//                }
//            }
//            //级联解绑
//            List<Rule> rules = ruleService.getBindedRules(gateid);//获得所有和gateid绑定的rule元组
//            List<Integer> ruleIds = new ArrayList<Integer>();
//            for(Rule r:rules){//获得所有的ruleId
//                ruleIds.add(r.getRuleid());
//            }
//            List<Rule2FilterKey> filterRelations = new ArrayList<Rule2FilterKey>();
//            for(Integer ruleId:ruleIds){//获得所有涉及到的rule2filter元组
//                filterRelations.addAll(ruleService.getBindedR2F(ruleId));
//            }
//            List<Filter> filters = new ArrayList<Filter>();
//            for(Rule2FilterKey R2F:filterRelations){//获得所有涉及到的filter元组
//                filters.add(ruleService.getBindedFilter(R2F.getFilterid()));
//            }
//            List<Rule2TransFormKey> transformRelations = new ArrayList<Rule2TransFormKey>();
//            for(Integer ruleId:ruleIds){//获得所有涉及到的rule2transform元组
//                transformRelations.addAll(ruleService.getBindedR2T(ruleId));
//            }
//            List<Transform> tranforms = new ArrayList<Transform>();
//            for(Rule2TransFormKey R2T:transformRelations){//获得所有涉及到的transform元组
//                tranforms.add(ruleService.getBindedTransform(R2T.getTransformid()));
//            }
//
//            //进行解绑
//            for(Rule2FilterKey R2F:filterRelations){//对rule2filter进行解绑
//                ruleService.unbindR2F(R2F);
//            }
//
//            for(Filter filter:filters){//对filter进行解绑
//                ruleService.unbindFilter(filter.getFilterid());
//            }
//
//            for(Rule2TransFormKey R2T:transformRelations){//对rule2transform进行解绑
//                ruleService.unbindR2T(R2T);
//            }
//
//            for(Transform transform:tranforms){//对transform进行解绑
//                ruleService.unbindTransform(transform.getTransformid());
//            }
//
//            for(Rule rule:rules){//对rule进行解绑
//                ruleService.unbindRule(rule.getRuleid());
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            result.setStatus("error");
//            result.setResultMsg("解除失败");
//        } finally {
//            return result;
//        }
//    }



}
