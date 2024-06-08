package org.coder.design.patterns._1_oop._3_inheritance.imagestore;

import org.coder.design.patterns._1_oop._3_inheritance.imagestore.pojo.Image;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PrivateImageStore implements ImageStore {

    //...省略属性、构造函数等...

    @Override
    public String upload(Image image, String bucketName) {
        createBucketIfNotExisting(bucketName);
        //...上传图片到私有云...
        //...返回图片的url...
        return "";
    }

    @Override
    public Image download(String url) {
        //...从私有云下载图片...
        return new Image();
    }

    private void createBucketIfNotExisting(String bucketName) {
        // ...创建bucket...
        //...失败会抛出异常..
    }
}
