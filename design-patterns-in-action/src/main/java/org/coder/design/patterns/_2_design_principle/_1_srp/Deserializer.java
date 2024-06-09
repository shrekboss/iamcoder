package org.coder.design.patterns._2_design_principle._1_srp;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Deserializer {

    private static final String IDENTIFIER_STRING = "UEUEUE;";
    private Gson gson;

    public Deserializer() {
        this.gson = new Gson();
    }

    public Map deserialize(String text) {
        if (!text.startsWith(IDENTIFIER_STRING)) {
            return Collections.emptyMap();
        }
        String gsonStr = text.substring(IDENTIFIER_STRING.length());
        return gson.fromJson(gsonStr, Map.class);
    }
}
