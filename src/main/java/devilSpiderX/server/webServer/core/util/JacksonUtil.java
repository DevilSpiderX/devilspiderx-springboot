package devilSpiderX.server.webServer.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import devilSpiderX.server.webServer.core.exception.JacksonUtilException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static devilSpiderX.server.webServer.core.constant.JacksonConstant.STANDARD_DATE_TIME_FORMAT;
import static devilSpiderX.server.webServer.core.constant.JacksonConstant.STANDARD_TIME_MODULE;


/**
 * @author DevilSpiderX
 */
public final class JacksonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_DATE_TIME_FORMAT));
        MAPPER.registerModule(STANDARD_TIME_MODULE);

        MAPPER.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.FAIL_ON_EMPTY_BEANS
        );

        MAPPER.disable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );
    }

    private JacksonUtil() {
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
            throw new JacksonUtilException(e, "Json字符串转为{0}失败", clazz.getTypeName());
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
            throw new JacksonUtilException(
                    e,
                    "Json字符串转为{0}失败",
                    typeRef.getType()
                            .getTypeName()
            );
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
            throw new JacksonUtilException(e, "Json字符串转为{0}失败", type.getTypeName());
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
            throw new JacksonUtilException(e, "从文件中读取json字符串转为{0}失败", clazz.getTypeName());
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
            throw new JacksonUtilException(e, "Json Array字符串转为List<{0}>失败", typeRef.getTypeName());
        }
    }

    public static String toJSONString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException(
                    e,
                    "{0}转Json字符串失败",
                    object.getClass()
                            .getTypeName()
            );
        }
    }

    public static String toPrettyJSONString(Object object) {
        try {
            return MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException(
                    e,
                    "{0}转Json字符串失败",
                    object.getClass()
                            .getTypeName()
            );
        }
    }

    public static byte[] toBytes(Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JacksonUtilException(
                    e,
                    "{0}转Byte Array失败",
                    object.getClass()
                            .getTypeName()
            );
        }
    }

    public static void objectToFile(File file, Object object) {
        try {
            MAPPER.writeValue(file, object);
        } catch (IOException e) {
            throw new JacksonUtilException(
                    e,
                    "{0}写入文件失败",
                    object.getClass()
                            .getTypeName()
            );
        }
    }

}
