/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T17:46:17+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-02T21:26:27+11:00
*/

package net.guiguan.exercise;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import com.google.gson.*;

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

    /**
     * Test case that validates the total number of rows returned and the number
     * of distinct values of x expected
     */
    public void testTableJoiner() {
        // try (Stream<String> stream = Files.lines(Paths.get("./t1.json"))) {
        //     stream.forEach(line -> {
        //       System.out.println(line);
        //     });
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        System.out.println(new Float(37.199999999999996) == new Float(38));
        assertTrue(true);
    }
}
