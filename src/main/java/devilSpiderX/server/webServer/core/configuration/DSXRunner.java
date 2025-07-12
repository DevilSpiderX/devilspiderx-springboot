package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.resp.ResultCode;
import devilSpiderX.server.webServer.core.util.SimpleStringFormatter;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DSXRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DSXRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        check();
    }

    public void check() {
        final var codeMap = new HashMap<Integer, ResultCode>();
        for (final var resultCode : ResultCode.values()) {
            if (codeMap.containsKey(resultCode.getCode())) {
                final var innerCode = codeMap.get(resultCode.getCode());
                throw new IllegalStateException(SimpleStringFormatter.format(
                        "业务代码重复:code={}, name0={}, name1={}",
                        innerCode.getCode(),
                        innerCode.name(),
                        resultCode.name()
                ));
            }
            codeMap.put(resultCode.getCode(), resultCode);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("关闭服务器");
    }
}
