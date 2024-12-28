package devilSpiderX.server.webServer.module.fjrc.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import devilSpiderX.server.webServer.module.fjrc.dao.FjrcMapper;
import devilSpiderX.server.webServer.module.fjrc.dao.FjrcUserMapper;
import devilSpiderX.server.webServer.module.fjrc.entity.Fjrc;
import devilSpiderX.server.webServer.module.fjrc.entity.FjrcUser;
import devilSpiderX.server.webServer.module.fjrc.vo.HistoryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FjrcService {
    private static final Logger logger = LoggerFactory.getLogger(FjrcService.class);

    private final FjrcMapper fjrcMapper;
    private final FjrcUserMapper fjrcUserMapper;

    public FjrcService(FjrcMapper fjrcMapper, FjrcUserMapper fjrcUserMapper) {
        this.fjrcMapper = fjrcMapper;
        this.fjrcUserMapper = fjrcUserMapper;
    }

    public static final Map<String, String> ITEM_BANK_NAME = Map.ofEntries(
            Map.entry("A", "2023年运营岗位资质考试理论题库（财务会计部）"),
            Map.entry("B", "2023年运营岗位资质考试理论题库（运营管理部）"),
            Map.entry("C", "2023年运营岗位资质考试理论题库（办公室）"),
            Map.entry("D", "2023年运营岗位资质考试理论题库（法律合规部）"),
            Map.entry("E", "2023年运营岗位资质考试理论题库（风险管理部）"),
            Map.entry("F", "2023年运营岗位资质考试理论题库（金融市场部）"),
            Map.entry("G", "2023年运营岗位资质考试理论题库（普惠金融部）"),
            Map.entry("H", "2023年运营岗位资质考试理论题库（审计部）"),
            Map.entry("I", "财务会计类(客户经理)"),
            Map.entry("J", "法律合规类(客户经理)"),
            Map.entry("K", "风险管理类(客户经理)"),
            Map.entry("L", "纪检类(客户经理)"),
            Map.entry("M", "金融市场类(客户经理)"),
            Map.entry("N", "普惠金融类(客户经理)"),
            Map.entry("O", "审计类(客户经理)"),
            Map.entry("P", "运营管理类(客户经理)")
    );

    public Fjrc getTopic(String bank, int id) {
        final var wrapper = new LambdaQueryWrapper<Fjrc>();
        if (ITEM_BANK_NAME.containsKey(bank)) {
            wrapper.eq(Fjrc::getItemBank, ITEM_BANK_NAME.get(bank));
        } else {
            wrapper.eq(Fjrc::getItemBank, ITEM_BANK_NAME.get("A"));
        }

        final List<Fjrc> list = fjrcMapper.selectList(new Page<>(id, 1), wrapper);
        if (!list.isEmpty()) {
            return list.getFirst();
        }
        return null;
    }

    public long getCount(String bank) {
        final var wrapper = new LambdaQueryWrapper<Fjrc>();
        if (ITEM_BANK_NAME.containsKey(bank)) {
            wrapper.eq(Fjrc::getItemBank, ITEM_BANK_NAME.get(bank));
        } else {
            wrapper.eq(Fjrc::getItemBank, ITEM_BANK_NAME.get("A"));
        }

        return fjrcMapper.selectCount(wrapper);
    }

    private final Timer onlineTimer = new Timer("Online Timer", true);
    private final Map<String, TimerTask> online = new ConcurrentHashMap<>();


    public int getOnlineCount(String fingerprint) {
        if (fingerprint == null) throw new NullPointerException("fingerprint can't be null");
        final var task = new TimerTask() {
            @Override
            public void run() {
                try {
                    _run();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            private void _run() {
                final var lastTask = online.remove(fingerprint);
                if (lastTask != null) {
                    lastTask.cancel();
                }
            }
        };

        if (online.containsKey(fingerprint)) {
            final var lastTask = online.get(fingerprint);
            lastTask.cancel();
        }
        onlineTimer.schedule(task, 360_000);
        online.put(fingerprint, task);
        return online.size();
    }

    public boolean uploadHistory(String key, String value) {
        if (key == null || value == null) return false;
        final var uid = SaSecureUtil.sha256(key);
        final var fjrcUser = new FjrcUser();
        fjrcUser.setUid(uid);

        final var wrapper = new LambdaQueryWrapper<FjrcUser>();
        wrapper.eq(FjrcUser::getUid, uid);

        final var one = fjrcUserMapper.selectOne(wrapper);
        final var nowDate = new Date();
        if (one != null) {
            final var lastDate = one.getTime();
            if (nowDate.getTime() - lastDate.getTime() < 60_000) {
                return false;
            }
        }

        fjrcUser.setValue(value);
        fjrcUser.setTime(nowDate);

        final var n = (one != null) ? fjrcUserMapper.update(fjrcUser, wrapper) : fjrcUserMapper.insert(fjrcUser);
        return n > 0;
    }

    public HistoryVo downloadHistory(String key) {
        if (key == null) return null;
        final var uid = SaSecureUtil.sha256(key);

        final var result = fjrcUserMapper.selectOne(
                new LambdaQueryWrapper<FjrcUser>().eq(FjrcUser::getUid, uid)
        );
        if (result == null) return null;
        return new HistoryVo(
                key,
                result.getTime(),
                result.getValue()
        );
    }

}
