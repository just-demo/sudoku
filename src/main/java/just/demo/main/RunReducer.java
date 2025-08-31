package just.demo.main;

import static java.lang.System.currentTimeMillis;

import static just.demo.main.DataDirs.READY_DIR;
import static just.demo.main.DataDirs.REDUCER_FAILED_DIR;
import static just.demo.main.DataDirs.REDUCER_FIXED_DIR;
import static just.demo.util.FileUtils.appendFile;
import static just.demo.util.FileUtils.listFiles;
import static just.demo.util.FileUtils.readFile;
import static just.demo.util.FileUtils.writeFile;
import static just.demo.util.SudokuUtils.countOpen;
import static just.demo.util.SudokuUtils.fromString2D;
import static just.demo.util.SudokuUtils.toString1D;
import static just.demo.util.SudokuUtils.toString2D;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import just.demo.generator.Reducer;

public class RunReducer {

  public static void main(String[] args) {
    reduceFailedByGeneratorTimeout();
//    copyReducedToReady();
  }

  private static void reduceFailedByGeneratorTimeout() {
    Reducer reducer = new Reducer();
    AtomicLong minimizedCount = new AtomicLong();
    List<File> files = listFiles(REDUCER_FAILED_DIR);
    for (File file : files) {
      System.out.println(file.getName());
      int[][] input = fromString2D(readFile(file));
      long startTime = currentTimeMillis();
      int[][] output = reducer.reduce(input);
      System.out.println("Time: " + (currentTimeMillis() - startTime) / 1000d + "s");
      long inputCount = countOpen(input);
      long outputCount = countOpen(output);
      String outFile = outputCount + "-" + file.getName().split("-", 2)[1];
      writeFile(REDUCER_FIXED_DIR.resolve(outFile), toString2D(output));
      if (outputCount != inputCount) {
        minimizedCount.incrementAndGet();
        System.out.println("Minimized " + file.getName() + ":" + inputCount + " => " + outputCount);
      }
      file.delete();
    }
    System.out.println("Minimized " + minimizedCount.get() + " of " + files.size());
  }

  private static void copyReducedToReady() {
    List<File> files = listFiles(REDUCER_FIXED_DIR);
    for (File file : files) {
      System.out.println(file.getName());
      int[][] result = fromString2D(readFile(file));
      Long openCount = countOpen(result);
      Path readyFile = READY_DIR.resolve(openCount + ".txt");
      appendFile(readyFile.toFile(), toString1D(result) + "\n");
      file.delete();
    }
  }
}
