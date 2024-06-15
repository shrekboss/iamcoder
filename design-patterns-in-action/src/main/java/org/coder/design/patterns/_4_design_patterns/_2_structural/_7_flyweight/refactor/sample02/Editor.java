package org.coder.design.patterns._4_design_patterns._2_structural._7_flyweight.refactor.sample02;

import org.coder.design.patterns._4_design_patterns._2_structural._7_flyweight.original.sample02.Font;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    private List<Character> chars = new ArrayList<>();

    public void appendCharacter(char c, Font font, int size, int colorRGB) {
        Character character = new Character(c, CharacterStyleFactory.getStyle(font, size, colorRGB));
        chars.add(character);
    }
}
