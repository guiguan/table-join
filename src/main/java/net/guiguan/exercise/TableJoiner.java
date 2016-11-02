/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T17:32:22+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T06:37:11+11:00
*/

package net.guiguan.exercise;

import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;
import com.google.gson.*;
import java.util.HashMap;
import java.util.Arrays;

public class TableJoiner {
    private Gson gson;
    private int totalRowCount;
    private Path t1JsonPath;
    private Path t2JsonPath;
    private Path outputPath;
    private HashMap<Double, T1> t1Zs;
    private HashMap<Double, Result> t1Xs;

    /**
     * Construct with precision
     *
     * @param t1JsonPath (required) t1's JSON file path
     * @param t2JsonPath (required) t2's JSON file path
     * @param outputPath (required) output file path
     * @param doublePrecision (required) number of decimal places to keep
     */
    public TableJoiner(Path t1JsonPath, Path t2JsonPath, Path outputPath,
                       int doublePrecision) {
        DoubleAdapter dA = new DoubleAdapter(doublePrecision);
        this.gson = new GsonBuilder()
                        .registerTypeAdapter(double.class, dA)
                        .registerTypeAdapter(Double.class, dA)
                        .create();
        this.totalRowCount = 0;
        this.t1JsonPath = t1JsonPath;
        this.t2JsonPath = t2JsonPath;
        this.outputPath = outputPath;
        this.t1Zs = new HashMap<Double, T1>();
        this.t1Xs = new HashMap<Double, Result>();
    }

    /**
     * Construct with default precision (DoubleAdapter.DEFAULT_PRECISION)
     *
     * @param t1JsonPath (required) t1's JSON file path
     * @param t2JsonPath (required) t2's JSON file path
     * @param outputPath (required) output file path
     */
    public TableJoiner(Path t1JsonPath, Path t2JsonPath, Path outputPath) {
        this(t1JsonPath, t2JsonPath, outputPath,
             DoubleAdapter.DEFAULT_PRECISION);
    }

    private void processT1() {
        try (Stream<String> stream = Files.lines(this.t1JsonPath)) {
            stream.forEach(line -> {
                T1 t1 = this.gson.fromJson(line, T1.class);
                if (t1.z > 0) {
                    this.t1Zs.put(t1.z, t1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processT2() {
        try (Stream<String> stream = Files.lines(this.t2JsonPath)) {
            stream.forEach(line -> {
                T2 t2 = gson.fromJson(line, T2.class);
                if (this.t1Zs.containsKey(t2.z)) {
                    // this t2 row should be joined to the matched t1 row
                    T1 t1 = this.t1Zs.get(t2.z);

                    this.totalRowCount++;
                    if (this.t1Xs.containsKey(t1.x)) {
                        // update sums
                        Result t1XRe = this.t1Xs.get(t1.x);
                        t1XRe.sumT1Y += t1.y;
                        t1XRe.sumT2Y += t2.y;
                    } else {
                        // create new result entry
                        this.t1Xs.put(t1.x, new Result(t1.x, t1.y, t2.y));
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Result[] generateSortedResult() {
        Result[] results = this.t1Xs.values().toArray(new Result[0]);
        Arrays.sort(results,
                    (Result r1, Result r2)
                        -> - (new Double(r1.sumT2Y)).compareTo(r2.sumT2Y));
        return results;
    }

    private void outputJsonFile(Result[] results) {
        if (this.outputPath != null) {
            try (BufferedWriter writer =
                     Files.newBufferedWriter(this.outputPath)) {
                for (Result r : results) {
                    writer.write(this.gson.toJson(r));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateOutput() { outputJsonFile(generateSortedResult()); }

    /**
     * Performs inner join on provided two tables. This operation is idempotent.
     */
    public void join() {
        // init vars
        this.totalRowCount = 0;
        if (this.t1Zs.size() > 0)
            this.t1Zs.clear();
        if (this.t1Xs.size() > 0)
            this.t1Xs.clear();

        processT1();
        processT2();
        generateOutput();
    }

    /**
     * Get total number of joined row
     *
     * @return total number of joined row
     */
    public int getTotalRowCount() { return this.totalRowCount; }

    /**
     * Get number of unique t1.x
     *
     * @return number of unique t1.x
     */
    public int getUniqueXCount() { return this.t1Xs.size(); }

    /**
     * Entrypoint
     */
    public static void main(String[] argvs) {
        Path t1JsonPath;
        Path t2JsonPath;
        Path outputPath;

        if (argvs.length == 0) {
            t1JsonPath = Paths.get("./data/t1.json");
            t2JsonPath = Paths.get("./data/t2.json");
            outputPath = Paths.get("./output.json");
        } else if (argvs.length == 3) {
            t1JsonPath = Paths.get(argvs[0]);
            t2JsonPath = Paths.get(argvs[1]);
            outputPath = Paths.get(argvs[2]);
        } else {
            System.err.println(
                "Error: please provide 3 arguments (t1JsonPath, t2JsonPath, outputPath) or none to use default paths");
            return;
        }

        TableJoiner tj = new TableJoiner(t1JsonPath, t2JsonPath, outputPath);
        System.out.printf("Processing %s and %s...  ", t1JsonPath, t2JsonPath);
        tj.join();
        System.out.println("Done");
        System.out.printf(
            "Total joined row count: %d\nUnique t1.x count: %d\nOutput saved to %s\n",
            tj.getTotalRowCount(), tj.getUniqueXCount(), outputPath);
    }
}
