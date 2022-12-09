package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.service.MyPasswordsService;
import devilSpiderX.server.webServer.util.AjaxResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@RequestMapping("/api/query")
public class QueryController {
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
    @Resource(name = "myPasswordsService")
    private MyPasswordsService myPasswordsService;

    /**
     * <b>查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * key
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("/get")
    @ResponseBody
    private AjaxResp<?> queryPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        String[] keys = new String[0];
        if (reqBody.containsKey("key")) {
            String keysStr = reqBody.getString("key").trim();
            keys = keysStr.split("\\s|\\.");
        }
        String uid = (String) session.getAttribute("uid");
        logger.info("用户{}查询记录：{}", uid, Arrays.toString(keys));
        JSONArray myPwdArray = myPasswordsService.query(keys, uid);

        return AjaxResp.success(myPwdArray);
    }

    /**
     * <b>添加密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * name, account, password, remark
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；
     * </p>
     */
    @PostMapping("/add")
    @ResponseBody
    private AjaxResp<?> addPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        if (!reqBody.containsKey("name")) {
            return AjaxResp.error("name参数不能为空或不存在");
        }
        String name = reqBody.getString("name");
        String account = reqBody.getString("account");
        String password = reqBody.getString("password");
        String remark = reqBody.getString("remark");
        String owner = (String) session.getAttribute("uid");
        logger.info("用户{}添加记录：{}", owner, name);
        return myPasswordsService.add(name, account, password, remark, owner) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }

    /**
     * <b>修改密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * id, [name, account, password, remark]
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；
     * </p>
     */
    @PostMapping("/update")
    @ResponseBody
    private AjaxResp<?> updatePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        if (!reqBody.containsKey("id")) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.getInteger("id");
        String name = reqBody.getString("name");
        String account = reqBody.getString("account");
        String password = reqBody.getString("password");
        String remark = reqBody.getString("remark");
        logger.info("用户{}修改记录 id：{}", session.getAttribute("uid"), id);
        return myPasswordsService.update(id, name, account, password, remark) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }

    /**
     * <b>删除密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * id
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 删除成功；1 删除失败；
     * </p>
     */
    @PostMapping("/delete")
    @ResponseBody
    private AjaxResp<?> deletePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        if (!reqBody.containsKey("id")) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.getInteger("id");
        logger.info("用户{}删除记录 id：{}", session.getAttribute("uid"), id);
        return myPasswordsService.delete(id) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }
}
