package org.coder.design.patterns._1_oop._3_inheritance.imagestore;

import org.coder.design.patterns._1_oop._3_inheritance.imagestore.pojo.Image;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ImageProcessingJob {
    private static final String BUCKET_NAME = "ai_images_bucket";

    //...省略其他无关代码...
    public void process() {
        //处理图片，并封装为Image对象
        Image image = new Image();
        //************************************************************************************************
        // 在项目中很多地方，都是通过下面第 20 行的方式来使用接口的。这就会产生一个问题，那就是，如果要替换图片存储方式，还是需要修改很多
        // 类似第 20 行那样的代码。这样的设计还是不够完美，对此，你有更好的实现思路吗？ ----> 工厂模式
        //************************************************************************************************
        ImageStore imageStore = new PrivateImageStore();
        imageStore.upload(image, BUCKET_NAME);
    }
}
