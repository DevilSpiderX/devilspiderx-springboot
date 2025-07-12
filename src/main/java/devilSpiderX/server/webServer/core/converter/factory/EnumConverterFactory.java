package devilSpiderX.server.webServer.core.converter.factory;

import devilSpiderX.server.webServer.core.converter.EnumConverter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author DevilSpiderX
 */
public class EnumConverterFactory implements ConverterFactory<String, EnumConverter> {
    private final Map<Class<? extends EnumConverter>, Converter<String, ? extends EnumConverter>> converterMap = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EnumConverter> @Nonnull Converter<String, T> getConverter(final @Nonnull Class<T> targetType) {
        return (Converter<String, T>) converterMap.computeIfAbsent(targetType, StringToEnumConverter::new);
    }

    public static class StringToEnumConverter<T extends EnumConverter> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnumConverter(final @Nonnull Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public @Nullable T convert(final @Nonnull String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }

            for (T enumConstant : enumType.getEnumConstants()) {
                if (Objects.equals(enumConstant.getStringValue(), source)) {
                    return enumConstant;
                }
            }
            return null;
        }
    }
}
