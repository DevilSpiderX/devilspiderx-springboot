package devilSpiderX.server.webServer.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import devilSpiderX.server.webServer.core.jackson.exception.JacksonUtilException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JacksonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));

        MAPPER.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.FAIL_ON_EMPTY_BEANS
        );

        MAPPER.disable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );
    }

    /**
     * 解析Json字符串
     *
     * @param jsonString 要解析的Json字符串
     * @param clazz      类型
     * @return 解析字符串后的类，类型为{@code clazz}
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Json字符串转为自定义对象失败", e);
        }
    }

    /**
     * 解析Json字符串
     *
     * @param jsonString 要解析的Json字符串
     * @param typeRef    复杂类型
     * @return 解析字符串后的类，类型为{@code typeRef}
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(jsonString, typeRef);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Json字符串转为自定义对象失败", e);
        }
    }

    /**
     * 解析Json字符串
     *
     * @param jsonString 要解析的Json字符串
     * @param type       复杂类型
     * @return 解析字符串后的类，类型为{@code type}
     */
    public static <T> T parseObject(String jsonString, JavaType type) {
        try {
            return MAPPER.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Json字符串转为自定义对象失败", e);
        }
    }


    /**
     * 从文件中解析Json
     *
     * @param file  要解析的文件
     * @param clazz 类型
     * @return 解析文件后的类，类型为clazz
     */
    public static <T> T parseObject(File file, Class<T> clazz) {
        try {
            return MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw new JacksonUtilException("从文件中读取json字符串转为自定义对象失败", e);
        }
    }

    /**
     * 解析Json数组字符串
     *
     * @param jsonArray 要解析的Json数组字符串
     * @param clazz     数组内的泛型类型
     * @return 解析文件后的List类，内部类型为clazz
     */
    public static <T> List<T> parseJSONArray(String jsonArray, Class<T> clazz) {
        final var typeRef = TypeFactory.defaultInstance()
                .constructCollectionType(ArrayList.class, clazz);
        try {
            return MAPPER.readValue(jsonArray, typeRef);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("JSONArray字符串转为List失败", e);
        }
    }

    public static String toJSONString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Object转JSONString失败", e);
        }
    }

    public static String toPrettyJSONString(Object object) {
        try {
            return MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Object转JSONString失败", e);
        }
    }

    public static byte[] toBytes(Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException("Object转ByteArray失败", e);
        }
    }

    public static void objectToFile(File file, Object object) {
        try {
            MAPPER.writeValue(file, object);
        } catch (IOException e) {
            throw new JacksonUtilException("Object写入文件失败", e);
        }
    }

}
