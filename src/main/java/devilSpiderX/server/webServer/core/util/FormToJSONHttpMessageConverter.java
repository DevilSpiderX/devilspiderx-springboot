package devilSpiderX.server.webServer.core.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.core.langExtend.Bytes;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FormToJSONHttpMessageConverter extends AbstractHttpMessageConverter<JSONObject> {

    public FormToJSONHttpMessageConverter() {
        super(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.equals(JSONObject.class);
    }

    @Override
    protected @NotNull JSONObject readInternal(@NotNull Class<? extends JSONObject> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        Bytes bytes = new Bytes();
        byte[] buffer = new byte[1024 * 64];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount == -1) {
                break;
            }
            bytes.append(buffer, 0, readCount);
        }
        String formStr = bytes.getString(StandardCharsets.UTF_8);

        JSONObject result = new JSONObject();
        String[] kAvList = formStr.split("&");
        for (String kAv : kAvList) {
            String[] kv = kAv.split("=");
            if (kv.length != 2) continue;
            String key = urlDecode(kv[0]);
            String value = urlDecode(kv[1]);
            if (result.containsKey(key)) {
                if (result.get(key) instanceof JSONArray) {
                    result.getJSONArray(key).add(value);
                } else {
                    JSONArray array = new JSONArray();
                    array.add(result.get(key));
                    array.add(value);
                    result.put(key, array);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    private String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeInternal(JSONObject object, @NotNull HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        StringBuilder stringBuilder = new StringBuilder();
        object.forEach((key, value) -> stringBuilder.append(urlEncode(key)).append('=')
                .append(urlEncode(value.toString())).append('&'));
        byte[] value = stringBuilder.substring(0, stringBuilder.length() - 1).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentLength(value.length);

        OutputStream out = outputMessage.getBody();
        out.write(value);
        out.flush();
    }

    private String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
