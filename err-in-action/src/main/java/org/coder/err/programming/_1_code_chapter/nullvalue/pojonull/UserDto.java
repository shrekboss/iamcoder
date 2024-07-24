package org.coder.err.programming._1_code_chapter.nullvalue.pojonull;

import lombok.Data;

import java.util.Optional;

@Data
public class UserDto {
    private Long id;
    private Optional<String> name;
    private Optional<Integer> age;
}
