package com.shadow.web.service.authority;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shadow.web.mapper.security.UserMapper;
import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.params.CreateUserParams;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.result.UserInfo;
import com.shadow.web.model.security.User;
import com.shadow.web.model.security.UserExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.shadow.web.utils.ParamUtils.Constants.*;

@Service
@Slf4j
public class UserService {

    private static final String INITIAL_PWD = "123456"; // 初始密码

    @Resource
    UserMapper mapper;

    @Autowired
    UserPwdService userpwdService;

    /**
     * 根据用户登录名查询用户
     *
     * @param loginName 用户登陆账号
     * @return 用户user对象
     */
    public Result<User> findByUserName(String loginName) {
        UserExample example = new UserExample();
        example.createCriteria().andDeletedEqualTo(0).andUsernameEqualTo(loginName);
        try {
            Result<List<User>> findRet = findByExample(example);
            if (!findRet.success()) {
                log.error("findByUserName failed: {}", findRet.msg());
                return Result.returnError("findByUserName failed:" + findRet.msg());
            }
            List<User> list = findRet.value();
            if (null == list || list.isEmpty()) {
                log.error("findByUserName failed: can not find User by LoginName= {}", loginName);
                return Result.returnError("根据用户登录名[" + loginName + "]查不到用户");
            }
            return Result.returnSuccess(list.get(0));
        } catch (Exception e) {
            log.error("findByUserName error: {}", e);
            return Result.returnError("findByUserName error:" + e);
        }
    }

    public Result<Integer> countByUserName(String loginName) {
        UserExample example = new UserExample();
        example.createCriteria().andDeletedEqualTo(0).andUsernameEqualTo(loginName);
        return countByExample(example);
    }

    /**
     * @param userId 用户ID
     * @return 获取用户密码
     * @auther: wangzhendong
     * @date: 2018/11/14 15:23
     */
    public Result<String> findUserPwdByUserId(Integer userId) {
        Result<User> findRet = findById(userId);
        if (!findRet.success()) {
            log.error("findUserPwdByUserId failed: {}", findRet.msg());
            return Result.returnError("findUserPwdByUserId failed:" + findRet.msg());
        }
        User user = findRet.value();
        String password = user.getPassword();
        if (null == password) {
            log.error("findUserPwdByUserId failed: can not find password by userId= {}", userId);
            return Result.returnError("根据用户ID=" + userId + "查不到密码");
        }
        return Result.returnSuccess(password);
    }

    /**
     * @param userId   用户ID
     * @param password 明文密码
     * @return 修改用户密码
     * @auther wangzhendong
     * @date 2018/12/21 18:19
     */
    public Result<Boolean> changeUserPwd(Integer userId, String password) {
        /* step1: 明文密码转密文*/
        Result<String> encryptRet = encryptPassword(password);
        if (!encryptRet.success()) {
            log.error("changeUserPwd failed: {}", encryptRet.msg());
            return Result.returnError("修改密码失败:" + encryptRet.msg());
        }
        String encryptPwd = encryptRet.value();
        /* step2: 更新*/
        Result<User> findRet = findById(userId);
        if (!findRet.success()) {
            log.error("changeUserPwd failed: {}" , findRet.msg());
            return Result.returnError("修改密码失败:" + findRet.msg());
        }
        User user = findRet.value();
        user.setPassword(encryptPwd);
        Result<Boolean> updateRet = update(user);
        if (!updateRet.success()) {
            log.error("changeUserPwd failed: {}" , updateRet.msg());
            return Result.returnError("修改密码失败:" + updateRet.msg());
        }
        return Result.returnSuccess();
    }

    /**
     * @param password 需要加密的明文
     * @return BCrypt 加密
     * @auther wangzhendong
     * @date 2018/12/21 18:21
     */
    public Result<String> encryptPassword(String password) {
        try {
            return Result.returnSuccess(BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch (Exception e) {
            log.error("encryptPassword error: {}", e);
            return Result.returnError("encryptPassword error");
        }
    }


    /**
     * @return 校验用户密码是否正确
     * @param userId 用户ID
     * @param password 用户密码 明文
     * @auther wangzhendong
     * @date 2018/12/22 16:46
     */
    public Result<Boolean> checkUserPwd(Integer userId, String password){
        /** step1: 根据用户ID查询密码*/
        Result<String> queryRet = findUserPwdByUserId(userId);
        if(!queryRet.success()) {
            log.error("checkUserPwd failed:  {}" , queryRet.msg());
            return Result.returnError("checkUserPwd failed: " + queryRet.msg());
        }
        String userPwd = queryRet.value();
        /** step2: 检查密码是否匹配*/
        Result<Boolean> checkRet = checkPwd(password, userPwd);
        if(!checkRet.success()) {
            log.error("checkUserPwd failed: {}" , checkRet.msg());
            return Result.returnError("checkUserPwd failed: " + checkRet.msg());
        }
        /** step3: 返回比较结果*/
        return Result.returnSuccess(checkRet.value());
    }

    /**
     * 检查密码是否匹配
     * @param password  明文密码
     * @param pwdInDb   数据库里面经过BCrypt加密后的密码
     * @return
     */
    private Result<Boolean> checkPwd(String password, String pwdInDb){
        try {
            return Result.returnSuccess(BCrypt.checkpw(password, pwdInDb));
        }catch(Exception e) {
            log.error("checkPwd error: {}" , e);
            return Result.returnError("checkPwd error:" + e);
        }
    }

    /**
     * 查询用户，参数校验，初始化分页参数
     * @return
     * @auther 10413
     * @date 2021-04-10 14:24
     */
    public ListParamValidResult<Map<String, Object>> searchUsersParamValid(Map<String, Object> input) {
        ListParamValidResult<Map<String, Object>> ret = new ListParamValidResult<>(input);
        Map<String, Object> params = new HashMap<>();
        // 用户姓名
        String name = (String) input.get("userName");
        if (!StringUtils.isEmpty(name)) {
            params.put("userName", name);
        }
        // 手机号
        String phone = (String) input.get("telephone");
        if (!StringUtils.isEmpty(phone)) {
            params.put("phone", phone);
        }
        // 角色id
        Integer roleId = (Integer) input.get("roleId");
        if (null != roleId) {
            params.put("roleId", roleId);
        }
        // 当前登录用户的id
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Integer userId = userDetails.getId();
        params.put("userId", userId);
        User userInfo = mapper.selectByPrimaryKey(userId);

        ret.setExample(params);
        return ret;
    }

    /**
     * @return 按等级查询用户列表
     * @auther 10413
     * @date 2021-04-10 14:31
     */
    public Result<PageInfo<UserInfo>> searchUsersByClass(ListParamValidResult<Map<String, Object>> validRet) {
        PageHelper.startPage(validRet.getPageNum(), validRet.getPageSize(), validRet.getOrderBy());
        try {
            List<UserInfo> infoList = mapper.searchUsersByClass(validRet.getExample());
            PageInfo<UserInfo> pageInfo = new PageInfo<>(infoList);
            return Result.returnSuccess(pageInfo);
        } catch (Exception e) {
            log.error(MSG_DATABASE_OPERATION_ERROR + "\n" + e.toString());
            return Result.returnError(MSG_QUERY_FAILED);
        }
    }

    /**
     * @return 重置用户密码
     * @auther 10413
     * @date 2021-04-10 15:59
     */
    public Result resetPwd(int userId) {
        // 查找用户
        Result<User> userRet = findById(userId);
        if (!userRet.success()) {
            return Result.returnError(userRet.msg());
        }
        // 重置密码
        User user = userRet.value();
        Result<String> userPwd = userpwdService.encryptPassword(INITIAL_PWD);
        if (userPwd.success()) {
            user.setPassword(userPwd.value());
        } else {
            return Result.returnError(userPwd.msg());
        }
        // 更新
        Result updateRet = update(user);
        if (!updateRet.success()) {
            return Result.returnError(updateRet.msg());
        }
        return Result.returnSuccess();
    }

    /**
     * @return 批量删除用户
     * @auther 10413
     * @date 2021-04-10 16:49
     */
    @Transactional
    public Result batchDeleteUser(Integer[] userIdList) {
        boolean deleteSuccess = true;
        String errMsg = "";
        for (int id : userIdList) {
            Result result = delete(id);
            if (!result.success()) {
                deleteSuccess = false;
                errMsg = result.msg();
                break;
            }
        }
        if (deleteSuccess) {
            return Result.returnSuccess();
        } else {
            return Result.returnError(errMsg);
        }
    }

    //获取用户下属用户
    public Result<List<User>> getUserSonUser(){
        UserExample example = new UserExample();
        example.createCriteria().andDeletedEqualTo(0).
                andStateEqualTo(1);
        Result<List<User>> userRet = findByExample(example);
        if (!userRet.success()){
            return Result.returnError(userRet.msg());
        }
        return Result.returnSuccess(userRet.value());
    }

    /**
     * @return 校验登录名是否存在
     * @auther 10413
     * @date 2021-04-10 22:41
     */
    public Result validLoginName(String loginName) {
        try {
            int exist = mapper.validLoginName(loginName);
            if (0 == exist) {
                return Result.returnSuccess();
            } else {
                return Result.returnError(MSG_USER_LOGIN_NAME_EXIST);
            }
        } catch (Exception e) {
            log.error(MSG_DATABASE_OPERATION_ERROR + "\n" + e.toString());
            return Result.returnError(MSG_QUERY_FAILED);
        }
    }

    /**
     * @return 创建用户
     * @auther 10413
     * @date 2021-04-11 13:39
     */
    @Transactional
    public Result createUser(CreateUserParams params) {
        User newUser = params.generateUser();
        // 生成初始密码
        Result<String> userPwd = userpwdService.encryptPassword(params.getPwd());
        if (userPwd.success()) {
            newUser.setPassword(userPwd.value());
        } else {
            return Result.returnError(userPwd.msg());
        }
        // 创建用户
        Result<Integer> userRet = create(newUser);
        if (!userRet.success()) {
            return Result.returnError(userRet.msg());
        }
        return Result.returnSuccess();
    }

    /**
     * 查询当前登录用户的个人中心所需的信息
     * @return
     */
    public Result<UserInfo> findCurUserInfo() {
        /** step1: 查询除了未读消息条数之外的基本信息*/
        Result<UserInfo> ret = findCurUserBasicInfo();
        if(!ret.success()) {
            log.error("findCurUserInfo failed: {}" , ret.msg());
            return Result.returnError(ret.msg());
        }
        UserInfo info = ret.value();
        /** step2: 查询用户未读消息条数*/
//        Result<Integer> countNoticeRet = alarmService.countUnreaded();
//        if(!countNoticeRet.success()) {
//            log.error("findCurUserInfo failed:" + countNoticeRet.msg());
//            return Result.returnError("findCurUserInfo failed");
//        }
        info.setNotifyCount(0);
        /** step3: 返回*/
        return Result.returnSuccess(info);
    }

    /**
     * @return: 获取当前用户的基本信息
     * @auther: wangzhendong
     * @date: 2018/11/22 12:02
     */
    public Result<UserInfo> findCurUserBasicInfo(){
        /** step1 : 获取当前用户ID*/
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Integer userId = userDetails.getId();
        /** step2 : 查询当前用户*/
        try{
            UserInfo userInfo = mapper.queryUserInfoByPrimaryKey(userId);
            if(null == userInfo){
                log.error("find current UserInfo failed: result is null, userId ={}",userId);
                return Result.returnError("获取id="+userId+"的用户信息为空");
            }
            return Result.returnSuccess(userInfo);
        }catch (Exception e){
            log.error("findCurUserInfo failed： {}",e.getMessage());
            return Result.returnError("获取用户基本信息失败");
        }
    }

    /**
     * 获取当前登录用户id
     *
     * @author szh
     * @since 2021/4/27 10:51
     */
    public int getCurrentUserId() {
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getId();
    }

    //----------------------------------------------基本方法--------------------
    public Result<List<User>> findByExample(UserExample example) {
        try {
            return Result.returnSuccess(mapper.selectByExample(example));
        }catch(Exception e) {
            log.error("findByExample error:" + e);
            return Result.returnError("findByExample error:" + e);
        }
    }

    public Result<User> findById(Integer id) {
        try {
            return Result.returnSuccess(mapper.selectByPrimaryKey(id));
        }catch(Exception e) {
            log.error("findById error:" + e);
            return Result.returnError("findById error");
        }
    }

    public Result<Integer> countByExample(UserExample example){
        try {
            return Result.returnSuccess(mapper.countByExample(example));
        }catch(Exception e) {
            log.error("countByExample error:" + e);
            return Result.returnError("countByExample error:" + e);
        }
    }

    public Result<Integer> create(User record) {
        try {
            int ret = mapper.insertSelective(record);
            if(-1 == ret) {
                log.error("create failed: insert return -1");
                return Result.returnError("create failed: insert return -1");
            }
            return Result.returnSuccess(record.getId());
        }catch(Exception e) {
            log.error("create error:" + e);
            return Result.returnError("create error:" + e);
        }
    }

    public Result<Boolean> update(User record) {
        try {
            int ret = mapper.updateByPrimaryKeySelective(record);
            if(-1 == ret) {
                log.error("update failed: update return -1");
                return Result.returnError("update failed: update return -1");
            }
            return Result.returnSuccess();
        }catch(Exception e) {
            log.error("update error:" + e);
            return Result.returnError("update error:" + e);
        }
    }

    public Result<Boolean> delete(Integer id) {
        try {
            int ret = mapper.deleteByPrimaryKey(id);
            if(-1 == ret) {
                log.error("delete failed: delete return -1");
                return Result.returnError("delete failed: delete return -1");
            }
            return Result.returnSuccess();
        }catch(Exception e) {
            log.error("delete error:" + e);
            return Result.returnError("delete error:" + e);
        }
    }

}
