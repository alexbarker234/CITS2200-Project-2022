package Fines;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class FinesTester {
  private static final long TIME_LIMIT_SECONDS = 2;
  private static final String INDENT = "    ";

  private enum TestState {
    PASS,
    FAIL,
    ERROR,
    TIME_LIMIT_EXCEEDED,
    UNKNOWN,
  }

  private interface Testable {
    public boolean run(boolean earlyExit);
    public EnumMap<TestState, Integer> getStats();
    public void buildReport(StringBuilder builder, String name, int indent);
  }

  private static class TestCase implements Testable {
    private int[] priorities;

    private int expected;

    private TestState state;
    private String reportBody;

    private TestCase() {
      state = TestState.UNKNOWN;
      reportBody = "Test not run";
    }

    public static TestCase fromFile(File file) throws FileNotFoundException {
      TestCase test = new TestCase();

      Scanner scanner = new Scanner(file);

      int length = scanner.nextInt();

      test.priorities = new int[length];
      for (int i = 0; i < test.priorities.length; i++) {
        test.priorities[i] = scanner.nextInt();
      }

      test.expected = scanner.nextInt();

      return test;
    }

    public boolean run(boolean earlyExit) {
      try {
        int received = runTimed(() -> {
          Fines fines = new FinesImpl();
          return fines.countFines(priorities);
        });

        if (expected == received) {
          state = TestState.PASS;
          reportBody = null;
          return true;
        } else {
          state = TestState.FAIL;
          reportBody = "expected " + expected + ", received " + received;
        }
      } catch (TimeoutException e) {
        state = TestState.TIME_LIMIT_EXCEEDED;
        reportBody = null;
      } catch (ExecutionException e) {
        Throwable cause = e.getCause();
        cause.printStackTrace(System.err);
        state = TestState.ERROR;
        reportBody = cause.getMessage();
      } catch (Throwable e) {
        e.printStackTrace(System.err);
        state = TestState.UNKNOWN;
        reportBody = "Failed to run test";
      }
      return false;
    }

    public EnumMap<TestState, Integer> getStats() {
      EnumMap<TestState, Integer> stats = new EnumMap<>(TestState.class);
      stats.put(state, 1);
      return stats;
    }

    public void buildReport(StringBuilder builder, String name, int indent) {
      for (int i = 0; i < indent; i++) {
        builder.append(INDENT);
      }

      if (name != null) {
        builder.append(name);
        builder.append(": ");
      }

      builder.append(state.toString());

      if (reportBody != null) {
        builder.append(": ");
        builder.append(reportBody);
      }

      builder.append(System.lineSeparator());
    }
  }

  private static class TestTree implements Testable {
    private Map<String, Testable> children;

    private TestTree() {
      children = new TreeMap<>();
    }

    public void add(String name, Testable testable) {
      children.put(name, testable);
    }

    public static TestTree fromFile(File file) {
      TestTree tree = new TestTree();

      for (File child : file.listFiles()) {
        if (child.isHidden()) continue;

        if (child.isFile()) {
          try {
            tree.add(child.getName(), TestCase.fromFile(child));
          } catch (FileNotFoundException e) {}
        } else if (child.isDirectory()) {
          tree.add(child.getName(), TestTree.fromFile(child));
        }
      }

      return tree;
    }

    public boolean run(boolean earlyExit) {
      boolean result = true;
      for (Testable child : children.values()) {
        result = child.run(earlyExit) && result;
        if (earlyExit && !result) break;
      }
      return result;
    }

    public EnumMap<TestState, Integer> getStats() {
      EnumMap<TestState, Integer> stats = new EnumMap<>(TestState.class);
      for (Testable child : children.values()) {
        for (Map.Entry<TestState, Integer> kv : child.getStats().entrySet()) {
          stats.put(kv.getKey(), stats.getOrDefault(kv.getKey(), 0) + kv.getValue());
        }
      }
      return stats;
    }

    public void buildReport(StringBuilder builder, String name, int indent) {
      if (name != null) {
        for (int i = 0; i < indent; i++) {
          builder.append(INDENT);
        }
        builder.append(name);
        builder.append(":");
        builder.append(System.lineSeparator());
      }

      for (Map.Entry<String, Testable> child : children.entrySet()) {
        child.getValue().buildReport(builder, child.getKey(), indent + 1);
      }

      for (int i = 0; i < indent; i++) {
        builder.append(INDENT);
      }

      EnumMap<TestState, Integer> stats = getStats();
      int numTests = 0;
      for (Integer count : stats.values()) {
        numTests += count;
      }

      int numPass = stats.getOrDefault(TestState.PASS, 0);
      if (numPass == numTests) {
        builder.append("All tests passed");
      } else {
        builder.append("PASS " + numPass + "/" + numTests + " tests");
        for (Map.Entry<TestState, Integer> stat : stats.entrySet()) {
          if (stat.getKey() == TestState.PASS) continue;
          if (stat.getValue() == 0) continue;
          builder.append(", " + stat.getValue() + " " + stat.getKey());
        }
      }

      builder.append(System.lineSeparator());
    }
  }

  public static <T> T runTimed(Supplier<T> supplier) throws TimeoutException, ExecutionException, InterruptedException {
    CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier);

    try {
      return future.get(TIME_LIMIT_SECONDS, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw e;
    } catch (InterruptedException e) {
      future.cancel(true);
      Thread.currentThread().interrupt();
      throw e;
    }
  }

  public static void main(String[] args) {
    Testable testable;
    try {
      testable = TestTree.fromFile(new File("testdata"));
    } catch (Throwable e) {
      throw new Error("Failed to read testdata", e);
    }

    boolean earlyExit = true;
    if (args.length == 1 && args[0].equals("all")) {
      earlyExit = false;
    }
    testable.run(earlyExit);

    StringBuilder builder = new StringBuilder();
    testable.buildReport(builder, "Testing Fines", 0);
    System.out.print(builder);
  } 
}