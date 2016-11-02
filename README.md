@Last modified by:   guiguan

 # Installation & Usage

1. `git clone https://github.com/guiguan/table-join.git`
2. `cd table-join`

## Build and run unit test

`mvn clean install`

## Build only

`mvn clean install -DskipTests`

## Run

`java -jar target/*.jar`

## Run with given t1JsonPath, t2JsonPath and outputPath

`java -jar target/*.jar t1JsonPath t2JsonPath outputPath`

# Assumptions

1. Values of `z` are unique in `t1`, but not in `t2`
2. `t2` has about 3 times as many rows as `t1`
3. Each row of json input files is a small JSON object
4. Precision of double is defined in `DoubleAdapter.precision`, which is set to 6, so `46.1999999` is considered equivalent to `46.2`, but not `46.199999`. Double is used in this project instead of float because `Float.parseFloat` can only keep up to 5 decimal places for a floating number. Double gives more freedom on the value of `DoubleAdapter.precision`

# Design

1. [GSON](https://github.com/google/gson) is chosen for JSON parsing according to this [benchmark](http://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/) for large number of small JSON objects
2. `t1` is processed line by line first and a hashtable is constructed for `t1.z`, which is then used to fast check join criteria of `t2` line by line.
3. When processing each line of `t2`, `zz` is ignored, and sum aggregation is updated on the fly and no info of `t2` is stored otherwise.
4. Finally, results from 3 is sorted according to `SUM(t2.y)`
