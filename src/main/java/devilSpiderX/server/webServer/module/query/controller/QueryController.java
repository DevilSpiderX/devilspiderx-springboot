package devilSpiderX.server.webServer.module.query.controller;

import com.alibaba.fastjson2.JSONArray;
import devilSpiderX.server.webServer.module.query.request.AddRequest;
import devilSpiderX.server.webServer.module.query.request.DeleteRequest;
import devilSpiderX.server.webServer.module.query.request.GetRequest;
import devilSpiderX.server.webServer.module.query.request.UpdateRequest;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private AjaxResp<?> get(@RequestBody GetRequest reqBody, @SessionAttribute() String uid) {
        String[] keys = new String[0];
        if (reqBody.getKey() != null) {
            String keysStr = reqBody.getKey().trim();
            keys = keysStr.split("\\s|\\.");
        }
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
    private AjaxResp<?> add(@RequestBody AddRequest reqBody, @SessionAttribute("uid") String owner) {
        if (reqBody.getName() == null) {
            return AjaxResp.error("name参数不能为空或不存在");
        }
        String name = reqBody.getName();
        String account = reqBody.getAccount();
        String password = reqBody.getPassword();
        String remark = reqBody.getRemark();
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
    private AjaxResp<?> update(@RequestBody UpdateRequest reqBody, @SessionAttribute() String uid) {
        if (reqBody.getId() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.getId();
        String name = reqBody.getName();
        String account = reqBody.getAccount();
        String password = reqBody.getPassword();
        String remark = reqBody.getRemark();
        logger.info("用户{}修改记录 id：{}", uid, id);
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
    private AjaxResp<?> delete(@RequestBody DeleteRequest reqBody, @SessionAttribute() String uid) {
        if (reqBody.getId() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        int id = reqBody.getId();
        logger.info("用户{}删除记录 id：{}", uid, id);
        return myPasswordsService.delete(id) ?
                AjaxResp.success()
                :
                AjaxResp.failure();
    }
}
