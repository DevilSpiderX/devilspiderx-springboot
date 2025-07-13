package devilSpiderX.server.webServer.common.core.configuration;

import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import devilSpiderX.server.webServer.common.core.util.SimpleStringFormatter;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author DevilSpiderX
 */
@Component
public class ResultCodeChecker {

    @PostConstruct
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

}
