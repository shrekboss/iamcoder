package org.coder.design.patterns._1_oop._2_abstraction;

import org.coder.design.patterns._1_oop._2_abstraction.simulate.Image;
import org.coder.design.patterns._1_oop._2_abstraction.simulate.Picture;
import org.coder.design.patterns._1_oop._2_abstraction.simulate.PictureMetaInfo;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface IPictureStorage {

    void savePicture(Picture picture);

    Image getPicture(String pictureId);

    void deletePicture(String pictureId);

    void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo);
}
