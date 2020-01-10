package top.niunaijun.livedata.core.utils;

import java.util.Random;

public class VoucherCodes {

    private static final Random RND = new Random(System.currentTimeMillis());

    /**
     * Generates a random code according to given config.
     *
     * @return Generated code.
     */
    public static String generate(int count) {
        CodeConfig config = CodeConfig.length(count).withCharset("abcdefghijklmnopqrstuvwxyz");
        StringBuilder sb = new StringBuilder();
        char[] chars = config.getCharset().toCharArray();
        char[] pattern = config.getPattern().toCharArray();

        if (config.getPrefix() != null) {
            sb.append(config.getPrefix());
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] == CodeConfig.PATTERN_PLACEHOLDER) {
                sb.append(chars[RND.nextInt(chars.length)]);
            } else {
                sb.append(pattern[i]);
            }
        }

        if (config.getPostfix() != null) {
            sb.append(config.getPostfix());
        }

        return sb.toString();
    }
}
