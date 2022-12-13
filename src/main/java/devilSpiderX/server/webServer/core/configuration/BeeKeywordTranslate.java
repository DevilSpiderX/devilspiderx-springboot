package devilSpiderX.server.webServer.core.configuration;

import org.springframework.stereotype.Component;
import org.teasoft.honey.osql.name.NameRegistry;
import org.teasoft.honey.osql.name.UnderScoreAndCamelName;

@Component
public class BeeKeywordTranslate extends UnderScoreAndCamelName {

    public BeeKeywordTranslate() {
        NameRegistry.registerNameTranslate(this);
    }

    @Override
    public String toColumnName(String fieldName) {
        return String.format("`%s`", super.toColumnName(fieldName));
    }
}
