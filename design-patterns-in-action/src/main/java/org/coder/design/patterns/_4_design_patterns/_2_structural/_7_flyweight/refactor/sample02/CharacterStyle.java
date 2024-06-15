package org.coder.design.patterns._4_design_patterns._2_structural._7_flyweight.refactor.sample02;

import org.coder.design.patterns._4_design_patterns._2_structural._7_flyweight.original.sample02.Font;

public class CharacterStyle {
    private Font font;
    private int size;
    private int colorRGB;

    public CharacterStyle(Font font, int size, int colorRGB) {
        this.font = font;
        this.size = size;
        this.colorRGB = colorRGB;
    }

    @Override
    public boolean equals(Object o) {
        CharacterStyle otherStyle = (CharacterStyle) o;
        return font.equals(otherStyle.font)
                && size == otherStyle.size
                && colorRGB == otherStyle.colorRGB;
    }
}

