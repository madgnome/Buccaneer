package com.madgnome.stash.plugins.buccaneer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to handle path.
 */
public class PathUtil
{
  private final static String SEPARATOR = "/";
  private final static Pattern WINDOWS_SEPARATOR = Pattern.compile("\\\\");
  private final static Pattern SEPARATOR_PATTERN = Pattern.compile(SEPARATOR);

  public static String shortenPath(String path, int maxLength)
  {
    path = normalize(path);

    if (path.length() <= maxLength)
    {
      return path;
    }

    StringBuilder builder = new StringBuilder(path.length());
    path = normalize(path);

    String[] parts = SEPARATOR_PATTERN.split(path);
    int partsNumber = parts.length;
    String lastPart = parts[partsNumber - 1];
    int minSizeWithoutEllipsis = (partsNumber - 1) * 2 + lastPart.length();

    if (minSizeWithoutEllipsis <= maxLength)
    {
      builder.append(new StringBuilder(SEPARATOR).append(lastPart).reverse());
      boolean compressing = false;
      for (int i =  partsNumber - 2; i >= 0; i--)
      {
        final String currentPart = parts[i];
        int remainingParts = i != 0 ? (i - 1) : 0;
        if (!compressing && builder.length() + (currentPart.length() + 1) + (remainingParts * 2) <= maxLength)
        {
          builder.append(new StringBuilder(currentPart).reverse());
        }
        else
        {
          builder.append(shortenPart(currentPart));
          compressing = true;
        }

        if (i != 0)
        {
          builder.append(SEPARATOR);
        }
      }

      builder.reverse();
    }
    else
    {
      // We must use an ellipsis
      builder.append("...").append(SEPARATOR).append(lastPart);
    }

    return builder.toString();
  }

  private static char shortenPart(String currentPart)
  {
    return currentPart.charAt(0);
  }

  public static String normalize(String path)
  {
    Matcher matcher = WINDOWS_SEPARATOR.matcher(path);
    return matcher.replaceAll(SEPARATOR);
  }
}
