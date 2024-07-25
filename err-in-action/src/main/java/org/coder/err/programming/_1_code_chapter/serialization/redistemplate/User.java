package org.coder.err.programming._1_code_chapter.serialization.redistemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
class User implements Serializable {
    private String name;
    private int age;
}
