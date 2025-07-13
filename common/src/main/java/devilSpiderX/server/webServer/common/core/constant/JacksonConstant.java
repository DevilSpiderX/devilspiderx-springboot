package devilSpiderX.server.webServer.common.core.constant;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DevilSpiderX
 */
public class JacksonConstant {
    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";
    public static final String STANDARD_TIME_FORMAT = "HH:mm:ss.SSS";
    public static final String STANDARD_DATE_TIME_FORMAT = MessageFormat.format(
            "{0} {1}",
            STANDARD_DATE_FORMAT,
            STANDARD_TIME_FORMAT
    );

    public static final JavaTimeModule STANDARD_TIME_MODULE = new JavaTimeModule();

    static {
        final var dateFormatter = DateTimeFormatter.ofPattern(STANDARD_DATE_FORMAT);
        final var timeFormatter = DateTimeFormatter.ofPattern(STANDARD_TIME_FORMAT);
        final var dateTimeFormatter = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_FORMAT);

        STANDARD_TIME_MODULE.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        STANDARD_TIME_MODULE.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        STANDARD_TIME_MODULE.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));

        STANDARD_TIME_MODULE.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        STANDARD_TIME_MODULE.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        STANDARD_TIME_MODULE.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
    }
}
