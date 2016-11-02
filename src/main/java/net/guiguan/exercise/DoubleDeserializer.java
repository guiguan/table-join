/**
* @Author: Guan Gui <guiguan>
* @Date:   2016-11-02T23:33:02+11:00
* @Email:  root@guiguan.net
* @Last modified by:   guiguan
* @Last modified time: 2016-11-03T00:27:24+11:00
*/

package net.guiguan.exercise;

import java.lang.reflect.Type;
import com.google.gson.*;
import java.math.BigDecimal;

public class DoubleDeserializer implements JsonDeserializer<Double> {
    public static final int DEFAULT_PRECISION = 6;

    /**
     * Number of decimal places to keep when dealing with doubles
     */
    public int precision;

    public DoubleDeserializer(int precision) {
      this.precision = precision;
    }

    public DoubleDeserializer() {
      // default precision
      this.precision = this.DEFAULT_PRECISION;
    }

    public Double deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context)
        throws JsonParseException {
        // use BigDecimal for rounding a double to desired decimal places
        return new BigDecimal(json.getAsJsonPrimitive().getAsString())
                               .setScale(this.precision, BigDecimal.ROUND_HALF_UP)
                               .doubleValue();
    }
}
