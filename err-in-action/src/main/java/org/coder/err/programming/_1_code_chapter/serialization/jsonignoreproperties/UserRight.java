package org.coder.err.programming._1_code_chapter.serialization.jsonignoreproperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRight {
    private String name;
}
