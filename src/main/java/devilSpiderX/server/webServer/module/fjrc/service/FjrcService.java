package devilSpiderX.server.webServer.module.fjrc.service;

import devilSpiderX.server.webServer.module.fjrc.entity.Fjrc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FjrcService {
    private final Logger logger = LoggerFactory.getLogger(FjrcService.class);
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    public static final Map<String, String> ITEM_BANK_NAME = Map.of(
            "A", "2023年运营岗位资质考试理论题库（财务会计部）",
            "B", "2023年运营岗位资质考试理论题库（运营管理部）",
            "C", "2023年运营岗位资质考试理论题库（办公室）",
            "D", "2023年运营岗位资质考试理论题库（法律合规部）",
            "E", "2023年运营岗位资质考试理论题库（风险管理部）",
            "F", "2023年运营岗位资质考试理论题库（金融市场部）",
            "G", "2023年运营岗位资质考试理论题库（普惠金融部）",
            "H", "2023年运营岗位资质考试理论题库（审计部）"
    );

    public Fjrc getTopic(String bank, int id) {
        final Fjrc fjrc = new Fjrc();
        if (ITEM_BANK_NAME.containsKey(bank)) {
            fjrc.setItemBank(ITEM_BANK_NAME.get(bank));
        } else {
            fjrc.setItemBank(ITEM_BANK_NAME.get("A"));
        }

        final List<Fjrc> list = suid.select(fjrc, id, 1);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public int getCount(String bank) {
        final Fjrc fjrc = new Fjrc();
        if (ITEM_BANK_NAME.containsKey(bank)) {
            fjrc.setItemBank(ITEM_BANK_NAME.get(bank));
        } else {
            fjrc.setItemBank(ITEM_BANK_NAME.get("A"));
        }

        return suid.count(fjrc);
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
}