package me.techstreet.cdp.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static Text colorCodesToTextComponent(String message) {
        MutableText result = new LiteralText("");

        try {
            Pattern pattern = Pattern.compile("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})");
            Matcher matcher = pattern.matcher(message);

            Style s = Style.EMPTY;

            int lastIndex = 0;
            while (matcher.find()) {
                int start = matcher.start();
                String text = message.substring(lastIndex, start);
                if (text.length() != 0) {
                    MutableText t = new LiteralText(text);
                    t.setStyle(s);
                    result.append(t);
                }
                String col = matcher.group();

                if (col.length() == 2) {
                    s = s.withExclusiveFormatting(Formatting.byCode(col.charAt(1)));
                } else {
                    s = Style.EMPTY.withColor(
                            TextColor.parse("#" + col.replaceAll("§", "").substring(1)));
                }
                lastIndex = matcher.end();
            }
            String text = message.substring(lastIndex);
            if (text.length() != 0) {
                MutableText t = new LiteralText(text);
                t.setStyle(s);
                result.append(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return new LiteralText("Text Error");
        }

        return result;
    }
}
