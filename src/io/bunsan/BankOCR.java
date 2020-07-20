package io.bunsan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BankOCR {

  final static List<String> zero = Arrays.asList(
      " _ ",
      "| |",
      "|_|");

  final static List<String> one = Arrays.asList(
      "   ",
      "  |",
      "  |");

  final static List<String> two = Arrays.asList(
      " _ ",
      " _|",
      "|_ ");

  final static List<String> three = Arrays.asList(
      " _ ",
      " _|",
      " _|");

  final static List<String> four = Arrays.asList(
      "   ",
      "|_|",
      "  |");

  final static List<String> five = Arrays.asList(
      " _ ",
      "|_ ",
      " _|");

  final static List<String> six = Arrays.asList(
      " _ ",
      "|_ ",
      "|_|");

  final static List<String> seven = Arrays.asList(
      " _ ",
      "  |",
      "  |");

  final static List<String> eight = Arrays.asList(
      " _ ",
      "|_|",
      "|_|");

  final static List<String> nine = Arrays.asList(
      " _ ",
      "|_|",
      " _|");

  final static List<List<String>> digits = Arrays.asList(
      zero, one, two, three, four, five, six, seven, eight, nine
  );

  static List<String> readRows = new ArrayList<>();
  static String accountNumber = "";

  public static void main(String[] args) {
    try {
      final Stream<String> stream = Files.lines(Paths.get(args[0]));
      final FileWriter fw = new FileWriter(args[1]);
      final PrintWriter pw = new PrintWriter(fw);

      AtomicInteger count = new AtomicInteger(0);
      stream.forEach(t -> {
        switch (count.incrementAndGet() % 4) {
          case 1:
          case 2:
          case 3:
            readRows.add(t);
            break;
          case 0:
            accountNumber = readAccount();
            readRows = new ArrayList<>();
            pw.println(accountNumber + " " +
                (accountNumber.contains("?") ?
                    "ILL" : (validateChecksum(accountNumber) ? "OK" : "ERR")));
            break;
          default:
            break;
        }
      });
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static String readAccount() {
    final StringBuffer number = new StringBuffer();
    IntStream.range(0, 9)
        .forEach(i -> number.append(readDigit(i * 3)));
    return number.toString();
  }

  static String readDigit(final int n) {
    for (int i = 0; i < 10; i++) {
      if (digits.get(i).get(0).equals(readRows.get(0).substring(n, n + 3)) &&
          digits.get(i).get(1).equals(readRows.get(1).substring(n, n + 3)) &&
          digits.get(i).get(2).equals(readRows.get(2).substring(n, n + 3))) {
        return i + "";
      }
    }
    return "?";
  }

  static boolean validateChecksum(final String account) {
    int checksum = 0;
    for (int i = 0; i < 9; i++) {
      checksum += (i + 1) * Integer.parseInt(account.substring(i, i + 1));
    }
    return checksum % 11 == 0;
  }
}
