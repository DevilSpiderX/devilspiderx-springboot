package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.response.ResultArray;
import devilSpiderX.server.webServer.controller.response.ResultBody;
import devilSpiderX.server.webServer.controller.response.ResultData;
import devilSpiderX.server.webServer.service.MyPasswordsService;
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
     * 0 成功；1 空值；
     * 100 没有权限;
     * </p>
     */
    @PostMapping("/get")
    @ResponseBody
    private ResultBody<?> queryPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        var resultArray = new ResultArray<>();
        String[] keys = new String[0];
        if (reqBody.containsKey("key")) {
            String keysStr = reqBody.getString("key").trim();
            keys = keysStr.split("\\s|\\.");
        }
        String uid = (String) session.getAttribute("uid");
        logger.info("用户{}查询记录：{}", uid, Arrays.toString(keys));
        JSONArray myPwdArray = myPasswordsService.query(keys, uid);

        if (myPwdArray.isEmpty()) {
            resultArray.setCode(1);
            resultArray.setMsg("空值");
        } else {
            resultArray.setCode(0);
            resultArray.setMsg("成功");
            resultArray.setData(myPwdArray);
        }
        return resultArray;
    }

    /**
     * <b>添加密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * name, account, password, remark
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；2 name参数不能为空或不存在； 100 没有权限；
     * </p>
     */
    @PostMapping("/add")
    @ResponseBody
    private ResultBody<?> addPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        var resultData = new ResultData<>();
        if (!reqBody.containsKey("name")) {
            resultData.setCode(2);
            resultData.setMsg("name参数不能为空或不存在");
        } else {
            String name = reqBody.getString("name");
            String account = reqBody.getString("account");
            String password = reqBody.getString("password");
            String remark = reqBody.getString("remark");
            String owner = (String) session.getAttribute("uid");
            logger.info("用户{}添加记录：{}", owner, name);
            if (myPasswordsService.add(name, account, password, remark, owner)) {
                resultData.setCode(0);
                resultData.setMsg("添加成功");
            } else {
                resultData.setCode(1);
                resultData.setMsg("添加失败");
            }
        }
        return resultData;
    }

    /**
     * <b>修改密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * id, [name, account, password, remark]
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；2 id参数不能为空或不存在； 100 没有权限；
     * </p>
     */
    @PostMapping("/update")
    @ResponseBody
    private ResultBody<?> updatePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        var resultData = new ResultData<>();
        if (!reqBody.containsKey("id")) {
            resultData.setCode(2);
            resultData.setMsg("id参数不能为空或不存在");
        } else {
            int id = reqBody.getInteger("id");
            String name = reqBody.getString("name");
            String account = reqBody.getString("account");
            String password = reqBody.getString("password");
            String remark = reqBody.getString("remark");
            logger.info("用户{}修改记录 id：{}", session.getAttribute("uid"), id);
            if (myPasswordsService.update(id, name, account, password, remark)) {
                resultData.setCode(0);
                resultData.setMsg("修改成功");
            } else {
                resultData.setCode(1);
                resultData.setMsg("修改失败");
            }
        }
        return resultData;
    }

    /**
     * <b>删除密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * id
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 删除成功；1 删除失败；2 id参数不能为空或不存在； 100 没有权限；
     * </p>
     */
    @PostMapping("/delete")
    @ResponseBody
    private ResultBody<?> deletePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        var resultData = new ResultData<>();
        if (!reqBody.containsKey("id")) {
            resultData.setCode(2);
            resultData.setMsg("id参数不能为空或不存在");
        } else {
            int id = reqBody.getInteger("id");
            logger.info("用户{}删除记录 id：{}", session.getAttribute("uid"), id);
            if (myPasswordsService.delete(id)) {
                resultData.setCode(0);
                resultData.setMsg("删除成功");
            } else {
                resultData.setCode(1);
                resultData.setMsg("删除失败");
            }
        }
        return resultData;
    }
}
