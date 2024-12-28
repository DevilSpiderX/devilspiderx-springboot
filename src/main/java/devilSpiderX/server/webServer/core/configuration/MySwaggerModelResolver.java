package devilSpiderX.server.webServer.core.configuration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.PrimitiveType;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MySwaggerModelResolver extends ModelResolver {

    public MySwaggerModelResolver(final ObjectMapper mapper) {
        super(mapper, new MySwaggerTypeNameResolver());
    }

    public static class MySwaggerTypeNameResolver extends TypeNameResolver {

        @Override
        protected String nameForGenericType(final JavaType type, final Set<Options> options) {
            final StringBuilder generic = new StringBuilder(nameForClass(type, options));
            generic.append("＜");
            final int count = type.containedTypeCount();
            for (int i = 0; i < count; i++) {
                final JavaType arg = type.containedType(i);
                final String argName = PrimitiveType.fromType(arg) != null ? nameForClass(arg, options) :
                        nameForType(arg, options);
                generic.append(WordUtils.capitalize(argName));
                if (i + 1 < count) {
                    generic.append(", ");
                }
            }
            return generic.append("＞").toString();
        }
    }

}
