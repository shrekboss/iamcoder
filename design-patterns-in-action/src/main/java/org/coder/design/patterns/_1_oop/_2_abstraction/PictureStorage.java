package org.coder.design.patterns._1_oop._2_abstraction;

import org.coder.design.patterns._1_oop._2_abstraction.simulate.Image;
import org.coder.design.patterns._1_oop._2_abstraction.simulate.Picture;
import org.coder.design.patterns._1_oop._2_abstraction.simulate.PictureMetaInfo;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PictureStorage implements IPictureStorage {
    // ...省略其他属性...

    @Override
    public void savePicture(Picture picture) {}

    @Override
    public Image getPicture(String pictureId) {
        return null;
    }

    @Override
    public void deletePicture(String pictureId) {}

    @Override
    public void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo) {}
}
