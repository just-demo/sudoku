package self.ed.main;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataDirs {

  public static final Path DATA_DIR = Paths.get("data");
  public static final Path READY_DIR = DATA_DIR.resolve("ready");
  public static final Path REDUCER_FAILED_DIR = DATA_DIR.resolve("reducer-failed");
  public static final Path REDUCER_FIXED_DIR = DATA_DIR.resolve("reducer-fixed");
}
