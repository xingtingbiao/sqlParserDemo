package util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by xWX522916 on 2017/11/24.
 */
public class IdGenerator {
  private static SecureRandom random = new SecureRandom();

  public static String createId() {
    return new BigInteger(32, random).toString();
  }

  public static String createUUID() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString().replaceAll("-", "");
  }

}
