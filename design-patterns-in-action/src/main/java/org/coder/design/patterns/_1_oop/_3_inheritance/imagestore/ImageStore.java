package org.coder.design.patterns._1_oop._3_inheritance.imagestore;

import org.coder.design.patterns._1_oop._3_inheritance.imagestore.pojo.Image;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface ImageStore {
    String upload(Image image, String bucketName);

    Image download(String url);
}
