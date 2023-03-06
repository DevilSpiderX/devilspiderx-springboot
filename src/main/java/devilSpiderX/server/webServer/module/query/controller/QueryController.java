package devilSpiderX.server.webServer.module.query.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/query")
@SaCheckLogin
public class QueryController {
    private final Logger logger = LoggerFactory.getLogger(QueryController.class);
    @Resource(name = "myPasswordsService")
    private MyPasswordsService myPasswordsService;

    /**
     * 查询密码记录请求参数
     *
     * @param key 查询值
     */
    record GetRequest(String key) {
        /**
         * 分割查询值,使用空格和<code>.</code>来分割
         *
         * @return 分割后的查询值
         */
        public String[] keys() {
            if (key != null) {
                String keysStr = key.trim();
                return keysStr.split("\\s|\\.");
            }
            return new String[0];
        }
    }

    /**
     * <b>查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link GetRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("/get")
    @ResponseBody
    private AjaxResp<?> get(@RequestBody GetRequest reqBody) {
        String uid = StpUtil.getLoginIdAsString();
        String[] keys = reqBody.keys();
        logger.info("用户{}查询记录：{}", uid, Arrays.toString(keys));
        List<Map<String, Serializable>> myPwdArray = myPasswordsService.query(keys, uid);

        return AjaxResp.success(myPwdArray);
    }

    /**
     * 添加密码记录请求参数
     *
     * @param name     名称
     * @param account  账号
     * @param password 密码
     * @param remark   备注
     */
    record AddRequest(String name, String account, String password, String remark) {
    }

    /**
     * <b>添加密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link AddRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；
     * </p>
     */
    @PostMapping("/add")
    @ResponseBody
    private AjaxResp<?> add(@RequestBody AddRequest reqBody) {
        if (reqBody.name() == null) {
            return AjaxResp.error("name参数不能为空或不存在");
        }
        String name = reqBody.name();
        String account = reqBody.account();
        String password = reqBody.password();
        String remark = reqBody.remark();
        String owner = StpUtil.getLoginIdAsString();
        logger.info("用户{}添加记录：{}", owner, name);
        return myPasswordsService.add(name, account, password, remark, owner) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }

    /**
     * 修改密码记录请求参数
     *
     * @param id       记录id
     * @param name     名称(可选)
     * @param account  账号(可选)
     * @param password 密码(可选)
     * @param remark   备注(可选)
     */
    record UpdateRequest(Integer id, String name, String account, String password, String remark) {
    }

    /**
     * <b>修改密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link UpdateRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；
     * </p>
     */
    @PostMapping("/update")
    @ResponseBody
    private AjaxResp<?> update(@RequestBody UpdateRequest reqBody) {
        if (reqBody.id() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.id();
        String uid = StpUtil.getLoginIdAsString();
        String name = reqBody.name();
        String account = reqBody.account();
        String password = reqBody.password();
        String remark = reqBody.remark();
        logger.info("用户{}修改记录 id：{}", uid, id);
        return myPasswordsService.update(id, name, account, password, remark) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }

    /**
     * 删除密码记录请求参数
     *
     * @param id 记录id
     */
    record DeleteRequest(Integer id) {
    }

    /**
     * <b>删除密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link DeleteRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 删除成功；1 删除失败；
     * </p>
     */
    @PostMapping("/delete")
    @ResponseBody
    private AjaxResp<?> delete(@RequestBody DeleteRequest reqBody) {
        if (reqBody.id() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.id();
        String uid = StpUtil.getLoginIdAsString();
        logger.info("用户{}删除记录 id：{}", uid, id);
        return myPasswordsService.delete(id) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }
}
