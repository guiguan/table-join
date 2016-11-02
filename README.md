<!--
@Author: Guan Gui <guiguan>
@Date:   2016-11-02T02:59:58+11:00
@Email:  root@guiguan.net
@Last modified by:   guiguan
@Last modified time: 2016-11-02T21:19:20+11:00
-->



# table-join

# Installation & Usage

1. `git clone https://github.com/guiguan/table-join.git && cd table-join`
2. `mvn clean install && java -jar target/*.jar`

## Build and run without running unit test

`mvn clean install -DskipTests && java -jar target/*.jar`

# Assumptions

1. Values of `z` are unique in `t1`, but not in `t2`
2. `t2` has about 3 times as many rows as `t1`
3. Each row of json input files is a small JSON object

# Design

1. [GSON](https://github.com/google/gson) is used for JSON parsing according to this [benchmark](http://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/) for large number of small JSON objects
2. `t1` is processed line by line first and a hashtable is constructed for `t1.z`, which is then used to fast check join criteria of `t2` line by line.
3. When processing each line of `t2`, sum aggregation is updated on the fly and no info of `t2` is stored otherwise.
4. Finally, results from 3 is sorted according to `SUM(t2.y)`
