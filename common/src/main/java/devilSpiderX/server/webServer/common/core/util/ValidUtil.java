package devilSpiderX.server.webServer.common.core.util;

import devilSpiderX.server.webServer.common.core.exception.BaseException;
import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author DevilSpiderX
 */
@RequiredArgsConstructor
@Component
public final class ValidUtil {
    private static final Logger logger = LoggerFactory.getLogger(ValidUtil.class);

    private final Validator validator;

    /**
     * 校验{@code obj}是否满足约束。<br/>
     * 对于容器类（{@code Array} {@link Collection} {@link Map}）的递归校验，会跳过为{@code null}的元素。
     *
     * @param obj 需要校验的对象，可以是：{@code Bean} {@code Array} {@link Collection} {@link Map}。不可以为{@code null}
     */
    public <T> void validate(final @Nonnull T obj) {
        Assert.notNull(obj, "用于校验的参数不能为null");

        final var clazz = obj.getClass();
        if (clazz.isArray()) {
            _validateArray((Object[]) obj);
        } else if (obj instanceof Collection<?> collection) {
            _validateCollection(collection);
        } else if (obj instanceof Map<?, ?> map) {
            _validateMap(map);
        } else {
            _validate(obj);
        }
    }

    private <T> void _validate(final @Nonnull T obj) {
        final Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (CollectionUtils.isEmpty(violations)) {
            return;
        }

        final var sb = new StringBuilder();
        for (final var violation : violations) {
            final var property = violation.getPropertyPath()
                    .toString();
            sb.append(property)
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        }
        logger.debug(sb.toString());
        violations.stream()
                .findFirst()
                .ifPresent(violation -> {
                    throw new BaseException(ResultCode.IllegalArgument, violation.getMessage());
                });
    }

    private <T> void _validateArray(final @Nonnull T[] arr) {
        for (final var t : arr) {
            if (t == null) {
                // 为null则跳过检查
                continue;
            }
            this.validate(t);
        }
    }

    private <T> void _validateCollection(final @Nonnull Collection<T> collection) {
        collection.stream()
                .filter(Objects::nonNull) // 为null则跳过检查
                .forEach(this::validate);
    }

    private <K, V> void _validateMap(final @Nonnull Map<K, V> map) {
        map.forEach((key, value) -> {
            if (key != null) {
                // 为null则跳过检查
                this.validate(key);
            }
            if (value != null) {
                // 为null则跳过检查
                this.validate(value);
            }
        });
    }

}
