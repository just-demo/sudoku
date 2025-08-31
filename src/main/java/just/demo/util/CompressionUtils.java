package just.demo.util;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompressionUtils {

  public static String compress(String str) {
    for (char i = 'z'; i >= 'a'; i--) {
      str = str.replaceAll("\\.{" + (i - 'a' + 1) + "}", String.valueOf(i));
    }
    return str;
  }

  public static String decompress(String str) {
    for (char i = 'z'; i >= 'a'; i--) {
      str = str.replaceAll(String.valueOf(i), StringUtils.repeat('.', i - 'a' + 1));
    }
    return str;
  }
}
