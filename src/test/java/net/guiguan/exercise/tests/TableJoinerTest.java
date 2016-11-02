/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T17:46:17+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T06:29:39+11:00
*/

package net.guiguan.exercise.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;
import com.google.gson.*;
import net.guiguan.exercise.*;
import java.util.HashMap;
import java.util.HashSet;

import java.math.BigDecimal;

/**
 * Unit test for TableJoiner.
 */
public class TableJoinerTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *         name of the test case
     */
    public TableJoinerTest(String testName) { super(testName); }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() { return new TestSuite(TableJoinerTest.class); }

    private int expectedTotalRowCount = 0;

    /**
     * Test case that validates the total number of rows returned and the number
     * of distinct values of x expected
     */
    public void testTableJoiner() {
        Path t1JsonPath = Paths.get("./data/t1.json");
        Path t2JsonPath = Paths.get("./data/t2.json");
        int doublePrecision = 6;

        // calculate expected values
        HashMap<Double, T1> t1Zs = new HashMap<Double, T1>();
        this.expectedTotalRowCount = 0;
        HashSet<Double> t1Xs = new HashSet<Double>();

        DoubleAdapter dA = new DoubleAdapter(doublePrecision);
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(double.class, dA)
                        .registerTypeAdapter(Double.class, dA)
                        .create();

        try (Stream<String> stream = Files.lines(t1JsonPath)) {
            stream.forEach(line -> {
                T1 t1 = gson.fromJson(line, T1.class);
                if (t1.z > 0) {
                    t1Zs.put(t1.z, t1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> stream = Files.lines(t2JsonPath)) {
            stream.forEach(line -> {
                T2 t2 = gson.fromJson(line, T2.class);
                if (t1Zs.containsKey(t2.z)) {
                    // this t2 row should be joined to the matched t1 row
                    this.expectedTotalRowCount++;
                    t1Xs.add(t1Zs.get(t2.z).x);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        int expectedUniqueXCount = t1Xs.size();

        // calculate actual values
        TableJoiner tj =
            new TableJoiner(t1JsonPath, t2JsonPath, null, doublePrecision);
        tj.join();

        int actualTotalRowCount = tj.getTotalRowCount();
        int actualUniqueXCount = tj.getUniqueXCount();

        assertTrue(this.expectedTotalRowCount == actualTotalRowCount &&
                   expectedUniqueXCount == actualUniqueXCount);
    }

    /**
     * Test case that validates the double adapter for GSON that can round
     * doubles to desired decimal places
     */
    public void testDoubleAdapter() {
        DoubleAdapter dA = new DoubleAdapter(6);
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(double.class, dA)
                        .registerTypeAdapter(Double.class, dA)
                        .create();

        assertTrue(gson.fromJson("46.199999", double.class) == 46.199999);
        assertTrue(gson.fromJson("46.1999999", double.class) == 46.2);
        assertTrue(gson.fromJson("46.1999995", double.class) == 46.2);
        assertTrue(gson.fromJson("46.1999994", double.class) == 46.199999);
        assertTrue(gson.toJson(46.1999991).equals("46.199999"));
        assertTrue(gson.toJson(46.19999999).equals("46.2"));
    }

    /**
     * Test case that validates the deserialized table json string is a valid
     * table object (T1 or T2)
     */
    public void testTableJsonDeserialization() {
        DoubleAdapter dA = new DoubleAdapter(6);
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(double.class, dA)
                        .registerTypeAdapter(Double.class, dA)
                        .create();

        T1 t1 = gson.fromJson("{\"_id\":10.0,\"x\":3.0,\"y\":15.0,\"z\":10.0}",
                              T1.class);
        assertTrue(t1.id == 10 && t1.x == 3 && t1.y == 15 && t1.z == 10);

        T2 t2 = gson.fromJson(
            "{\"_id\":80.0,\"y\":46.199999999999996,\"z\":31.0,\"zz\":\"gupmrlirvuxzjvkqwjfqxgaroxgvhabvgrddxyyjhoeriwxdhwqbclscvttaasgjjgewpvajm\"}",
            T2.class);

        assertTrue(t2.id == 80 && t2.y == 46.2 && t2.z == 31 &&
                   !gson.toJson(t2).contains("\"zz\":"));
    }
}
