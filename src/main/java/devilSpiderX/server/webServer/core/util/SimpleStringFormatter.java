package devilSpiderX.server.webServer.core.util;

import java.util.Objects;

public class SimpleStringFormatter {
    private static final char escape = '\\';
    private static final char placeholderLeft = '{';
    private static final char placeholderRight = '}';


    /**
     * 格式化字符串方法，插入参数时，只是简单的调用{@link String#valueOf(Object)}
     *
     * @param template 格式字符串，如 "Hello {}, you have {0} new messages"
     * @param args     参数数组，用于替换占位符
     * @return 格式化后的字符串
     */
    public static String format(final String template, final Object... args) {
        if (template == null) return null;
        final var _args = Objects.requireNonNullElse(args, new Object[0]);
        final var result = new StringBuilder();
        var index = 0;   // 用于无编号占位符的参数索引
        final var length = template.length();

        for (int i = 0; i < length; ) {
            final var c = template.charAt(i);

            if (c == escape) {
                // 转义处理
                if (i + 1 < length) {
                    final var next = template.charAt(i + 1);
                    if (next == escape || next == placeholderLeft) {
                        // \\ -> \
                        // \{ -> {
                        result.append(next);
                        i += 2;
                    } else {
                        // 不是转义字符，单独输出 \
                        result.append(c);
                        i++;
                    }
                } else {
                    // \ 是最后一个字符，原样输出
                    result.append(c);
                    i++;
                }
            } else if (c == placeholderLeft) {
                // 查找匹配的 }
                final var closeIndex = template.indexOf(placeholderRight, i);
                if (closeIndex == -1) {
                    // 没有闭合，按普通字符输出{
                    result.append(c);
                    i++;
                } else {
                    final var inside = template.substring(i + 1, closeIndex);
                    if (inside.isEmpty()) {
                        // {} 占位符
                        if (index < _args.length) {
                            result.append(_args[index]);

                            index++;
                        } else {
                            // 超出参数范围，不替换
                            result.append(placeholderLeft)
                                    .append(placeholderRight);
                        }
                    } else {
                        // {数字} 占位符
                        if (isNumber(inside)) {
                            final var argIndex = Integer.parseInt(inside);
                            if (argIndex >= 0 && argIndex < _args.length) {
                                result.append(_args[argIndex]);
                            } else {
                                // 参数数组越界，不替换
                                result.append(placeholderLeft)
                                        .append(inside)
                                        .append(placeholderRight);
                            }
                        } else {
                            // 不符合格式的占位符，原样输出
                            result.append(placeholderLeft)
                                    .append(inside)
                                    .append(placeholderRight);
                        }
                    }
                    i = closeIndex + 1;
                }
            } else {
                // 普通字符，直接输出
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    private static boolean isNumber(String s) {
        // 判断字符串是否全部为数字
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

}
