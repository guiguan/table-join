/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T23:33:02+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T05:46:42+11:00
*/

package net.guiguan.exercise;

import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.stream.*;
import java.math.BigDecimal;

public class DoubleAdapter extends TypeAdapter<Double> {
    public static final int DEFAULT_PRECISION = 6;

    /**
     * Number of decimal places to keep when dealing with doubles
     */
    public int precision;

    public DoubleAdapter(int precision) { this.precision = precision; }

    public DoubleAdapter() {
        // default precision
        this.precision = this.DEFAULT_PRECISION;
    }

    public Double read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return new BigDecimal(reader.nextString())
            .setScale(this.precision, BigDecimal.ROUND_HALF_UP)
            .doubleValue();
    }

    public void write(JsonWriter writer, Double value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(new BigDecimal(value)
                         .setScale(this.precision, BigDecimal.ROUND_HALF_UP)
                         .doubleValue());
    }
}
