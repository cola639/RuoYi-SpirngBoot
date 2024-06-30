package com.colaclub.common.utils.uuid;

import java.security.SecureRandom;

public class CombinationGenerator {
  private static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
  private static final SecureRandom RANDOM = new SecureRandom();

  /**
   * 生成指定长度的随机组合，包含至少4个数字。
   *
   * @param length 组合的长度
   * @return 随机组合字符串
   */
  public static String generateRandomCombination(int length) {
    StringBuilder combination = new StringBuilder(length);
    int digitCount = 0;

    while (combination.length() < length) {
      char c = CHARACTERS[RANDOM.nextInt(CHARACTERS.length)];
      combination.append(c);
      if (Character.isDigit(c)) {
        digitCount++;
      }

      // 如果组合已满，但数字数量不足，则重新生成
      if (combination.length() == length && digitCount < 4) {
        combination.setLength(0);
        digitCount = 0;
      }
    }

    return combination.toString();
  }
}
