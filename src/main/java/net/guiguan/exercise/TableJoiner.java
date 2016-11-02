/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T17:32:22+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T03:27:20+11:00
*/

package net.guiguan.exercise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;
import com.google.gson.*;
import java.util.HashMap;

public class TableJoiner {
    private Gson gson;
    private int totalRowCount;
    private Path t1JsonPath;
    private Path t2JsonPath;
    private Path outputPath;
    private HashMap<Double, T1> t1Zs;
    private HashMap<Double, Result> t1Xs;

    public TableJoiner(Path t1JsonPath, Path t2JsonPath, Path outputPath,
                       int doublePrecision) {
        this.gson =
            new GsonBuilder()
                .registerTypeAdapter(double.class,
                                     new DoubleDeserializer(doublePrecision))
                .create();
        this.totalRowCount = 0;
        this.t1JsonPath = t1JsonPath;
        this.t2JsonPath = t2JsonPath;
        this.outputPath = outputPath;
        this.t1Zs = new HashMap<Double, T1>();
        this.t1Xs = new HashMap<Double, Result>();
    }

    public TableJoiner(Path t1JsonPath, Path t2JsonPath, Path outputPath) {
        this(t1JsonPath, t2JsonPath, outputPath,
             DoubleDeserializer.DEFAULT_PRECISION);
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
                        this.t1Xs.put(t1.x, new Result(t1.x));
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateSortedResult() {}

    private void outputJsonFile() {}

    private void generateOutput() {
        generateSortedResult();
        outputJsonFile();
    }

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

    public int getTotalRowCount() { return this.totalRowCount; }

    public int getUniqueXCount() { return this.t1Xs.size(); }

    public static void main(String[] argvs) {
        String a = "Che";
        System.out.println("Hello World " + a + "!");
    }
}
