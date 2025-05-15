package devilSpiderX.server.webServer.core.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import devilSpiderX.server.webServer.core.langExtend.Bytes;
import devilSpiderX.server.webServer.core.jackson.JacksonUtil;
import jakarta.annotation.Nonnull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FormToJSONHttpMessageConverter extends AbstractHttpMessageConverter<Map<String, Object>> {

    public FormToJSONHttpMessageConverter() {
        super(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    protected boolean supports(@Nonnull Class<?> clazz) {
        return Map.class == clazz;
    }

    @Override
    protected @Nonnull Map<String, Object> readInternal(
            @Nonnull Class<? extends Map<String, Object>> clazz,
            @Nonnull HttpInputMessage inputMessage
    ) throws IOException, HttpMessageNotReadableException {
        final var in = inputMessage.getBody();
        final var bytes = new Bytes();
        final var buffer = new byte[1024 * 64];
        while (true) {
            final var readCount = in.read(buffer);
            if (readCount == -1) {
                break;
            }
            bytes.append(buffer, 0, readCount);
        }
        final var formStr = bytes.getString(StandardCharsets.UTF_8);

        final var result = JacksonUtil.MAPPER.createObjectNode();
        for (final var kAv : formStr.split("&")) {
            final var kv = kAv.split("=");
            if (kv.length != 2) continue;
            final var key = urlDecode(kv[0]);
            final var value = urlDecode(kv[1]);

            if (result.get(key) != null) {
                if (result.get(key) instanceof final ArrayNode array) {
                    array.add(value);
                } else {
                    final var array = JacksonUtil.MAPPER.createArrayNode();
                    array.add(result.get(key));
                    array.add(value);
                    result.set(key, array);
                }
            } else {
                result.put(key, value);
            }
        }
        return JacksonUtil.MAPPER.convertValue(
                result,
                TypeFactory
                        .defaultInstance()
                        .constructMapType(HashMap.class, String.class, Object.class)
        );
    }

    private String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeInternal(
            @Nonnull Map<String, Object> object,
            @Nonnull HttpOutputMessage outputMessage
    ) throws IOException, HttpMessageNotWritableException {
        final var stringBuilder = new StringBuilder();

        object.forEach((key, value) -> {
            if (value == null) return;
            if (value instanceof final Collection<?> array) {
                for (final var obj : array) {
                    if (obj == null) continue;
                    stringBuilder.append(urlEncode(key))
                            .append('=')
                            .append(urlEncode(obj.toString()))
                            .append('&');
                }
            } else {
                stringBuilder.append(urlEncode(key))
                        .append('=')
                        .append(urlEncode(value.toString()))
                        .append('&');
            }
        });
        final var value = stringBuilder.substring(0, stringBuilder.length() - 1).getBytes(StandardCharsets.UTF_8);

        final var headers = outputMessage.getHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentLength(value.length);

        final var out = outputMessage.getBody();
        out.write(value);
        out.flush();
    }

    private String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
