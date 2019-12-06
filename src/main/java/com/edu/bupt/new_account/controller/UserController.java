package com.edu.bupt.new_account.controller;

import com.edu.bupt.new_account.model.*;
import com.edu.bupt.new_account.service.RuleService;
import com.edu.bupt.new_account.service.UserService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
@RequestMapping("/api/v1/account")
@Transactional(rollbackFor = Exception.class)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RuleService ruleService;


    //（tenant）登陆
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestBody String loginInfo) {
        JsonObject login = new JsonParser().parse(loginInfo).getAsJsonObject();
        Result result = new Result();
        try {
            String tenantName = login.get("tenantName").getAsString();
            String passwd = login.get("password").getAsString();
            Tenant tenant = userService.findTenantByNameAndPasswd(tenantName, passwd);
            if (tenant == null)
                result.setStatus("error");
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
        } finally {
            return result;
        }
    }

    /**
     * @param info
     * @return
     * @Description 用户注册
     */
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ResponseBody
    public Result createUser(@RequestBody String info) {
        JsonObject jsonInfo = new JsonParser().parse(info).getAsJsonObject();
        Result result = new Result();
        String openid = jsonInfo.get("openid").getAsString();
        String email = jsonInfo.get("email").getAsString();
        String phone = jsonInfo.get("phone").getAsString();
        String address = jsonInfo.get("address").getAsString();
        try {
            User u = userService.findUserByOpenid(openid);
            if (u != null) {
                result.setStatus("error");
                result.setResultMsg("user has already existed!");
                return result;
            }
            User u2 = userService.findUserByphone(phone);
            if(u2 != null){
                result.setStatus("error");
                result.setResultMsg("phone number already exist!");
                return result;
            }
            User u3 = userService.findUserByemail(email);
            if(u3!=null){
                result.setStatus("error");
                result.setResultMsg("email already exist!");
                return result;
            }
            User user = new User();
            user.setOpenid(openid);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);

            userService.saveUser(user);
            int customerid = userService.findUserByOpenid(openid).getId();
            user.setId(customerid);
            result.setResultMsg("create success");
            result.setData(user);
        } catch (Exception e) {
            userService.deleteUserById(userService.findUserByOpenid(openid).getId());
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("some error!");
        } finally {
            return result;
        }
    }


    //判断openid是否在表内
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    @ResponseBody
    public Result userLogin(@RequestBody String loginInfo) {
        JsonObject login = new JsonParser().parse(loginInfo).getAsJsonObject();
        Result result = new Result();
        try {
            String openid = login.get("openid").getAsString();
            User user = userService.findUserByOpenid(openid);
            if (user == null) {
                result.setStatus("error");
                result.setResultMsg("用户不存在");
                return result;
            }
            result.setResultMsg("用户存在");
            result.setData(user);

        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("用户不存在");
        } finally {
            return result;
        }
    }

    // TODO: 2019/12/5 已完成
    //绑定所属主用户及其网关   A 分享给 B
    @RequestMapping(value = "/bindGate", method = RequestMethod.POST)
    @ResponseBody
    public Result bindGate(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            String phone = info.get("phone").getAsString();
            String gateids = info.get("gateids").getAsString();
            String remark = info.get("remark").getAsString();
            User user = userService.findUserByphone(phone);
            if (user == null) {
                result.setStatus("error");
                result.setResultMsg("该用户不存在");
                return result;
            }

            //检查是否已经有该绑定关系
            Relation re = userService.findRelationByBinderAndBinded(user.getId(), Integer.parseInt(binded));
            if (re != null) {
                if (userService.is_shared(re.getGateid(), gateids)){
                    result.setStatus("error");
                    result.setResultMsg("网关重复分享");
                    return result;
                } else {  // 添加新分享的网关
                    String old_gatewayids = re.getGateid();
                    re.setGateid(old_gatewayids + "," + gateids);
                    userService.updateRelation(re);
                    result.setResultMsg("绑定成功");
                }
            } else {
                Relation relation = new Relation();
                relation.setBinded(Integer.parseInt(binded));
                relation.setBinder(user.getId());
                relation.setGateid(gateids);
                relation.setRemark(remark);
                userService.saveRelation(relation);
                result.setResultMsg("绑定成功");
            }
        } catch (Exception e) {
            result.setStatus("error");
            result.setResultMsg("插入失败");
            e.printStackTrace();
        } finally {
            return result;
        }
    }


    // TODO: 2019/12/5 已完成
    
    //获取绑定者对应被绑定者电话及其网关  A分享给B， B要获取A信息
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

    // TODO: 2019/12/5 已完成

    //解绑被绑定者及其网关  A 分享给 B， A 要取消对 B 的分享
    @RequestMapping(value = "/unBindedGate", method = RequestMethod.POST)
    @ResponseBody
    public Result unBindedGate(@RequestBody String Info) {
        JsonObject info = new JsonParser().parse(Info).getAsJsonObject();
        Result result = new Result();
        try {
            String binded = info.get("customerid").getAsString();
            String phone = info.get("phone").getAsString();
            String gateids = info.get("gateids").getAsString();
            User user = userService.findUserByphone(phone);
            int bindedId = Integer.parseInt(binded);
            int binderId = user.getId();
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

    // TODO: 2019/12/5 已完成

    //解绑被绑定者及其网关 只有一个网关id  网关拥有者取消所有分享
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

    // TODO: 2019/12/5 未完成

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

    // TODO: 2019/12/5 已完成

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

    // TODO: 2019/12/5 已完成

    //解绑绑定者及其网关
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

    // TODO: 2019/12/5 已完成

    //查询被绑定用户所对应的网关
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
                User user = userService.findUserById(relation.getBinder());
                Map map = new HashMap();
                map.put("phone", user.getPhone());
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


    //用户（user）修改
    @RequestMapping(value = "/userModify", method = RequestMethod.POST)
    @ResponseBody
    public Result userModify(@RequestBody String loginInfo) {
        JsonObject jsonInfo = new JsonParser().parse(loginInfo).getAsJsonObject();
        Result result = new Result();
        try {
            String openid = jsonInfo.get("openid").getAsString();
            String email = jsonInfo.get("email").getAsString();
            String phone = jsonInfo.get("phone").getAsString();
            String address = jsonInfo.get("address").getAsString();

            User user = new User();
            user.setOpenid(openid);
            user.setPhone(phone);
            user.setEmail(email);
            user.setAddress(address);

            userService.updateUserInfo(user);

        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
            result.setResultMsg("用户不存在");
        } finally {
            return result;
        }
    }


    // 搜索所有用户
    @RequestMapping(value = "/searchAllUser", method = RequestMethod.POST)
    @ResponseBody
    public Result searchAllUser(@RequestBody String info) {
        Result result = new Result();
        try {
            List<User> list = userService.findAllUser();
            result.setData(list);
        } catch (Exception e) {
            System.out.println(e);
            result.setStatus("error");
        } finally {
            return result;
        }
    }
}
