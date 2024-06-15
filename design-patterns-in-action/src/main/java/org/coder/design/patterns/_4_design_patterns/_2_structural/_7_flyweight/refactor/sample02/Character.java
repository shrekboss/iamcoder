package org.coder.design.patterns._4_design_patterns._2_structural._7_flyweight.refactor.sample02;

public class Character {
    private char c;
    private CharacterStyle style;

    public Character(char c, CharacterStyle style) {
        this.c = c;
        this.style = style;
    }
}
