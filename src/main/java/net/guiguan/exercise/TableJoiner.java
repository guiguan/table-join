/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T17:32:22+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T01:44:42+11:00
*/

package net.guiguan.exercise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;
import com.google.gson.*;

public class TableJoiner {
    private Gson gson;
    private int totalRowCount;
    private Path t1JsonPath;
    private Path t2JsonPath;
    private Path outputPath;

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
    }

    public TableJoiner(Path t1JsonPath, Path t2JsonPath, Path outputPath) {
        this(t1JsonPath, t2JsonPath, outputPath,
             DoubleDeserializer.DEFAULT_PRECISION);
    }

    public void join() {}

    public int getTotalRowCount() {
        return this.totalRowCount;
    }

    public int getUniqueXCount() {
        return -1;
    }

    public static void main(String[] argvs) {
        String a = "Che";
        System.out.println("Hello World " + a + "!");
    }
}
