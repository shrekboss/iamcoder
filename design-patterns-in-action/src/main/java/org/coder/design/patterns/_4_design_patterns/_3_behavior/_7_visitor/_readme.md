## 访问者模式

原始示例代码如下：

```java
import java.util.ArrayList;
import java.util.List;

public abstract class ResourceFile {
    protected String filePath;

    public ResourceFile(String filePath) {
        this.filePath = filePath;
    }

    public abstract void accept(Extractor extractor);

    public abstract void accept(Compressor compressor);
}

public class PPTFile extends ResourceFile {
    public PPTFile(String filePath) {
        super(filePath);
    }

    @Override
    public void accept(Extractor extractor) {
        extractor.extract2txt(this);
    }

    @Override
    public void accept(Compressor extractor) {
        extractor.compress(this);
    }
}

public class PdfFile extends ResourceFile {
    public PdfFile(String filePath) {
        super(filePath);
    }

    @Override
    public void accept(Extractor extractor) {
        extractor.extract2txt(this);
    }

    @Override
    public void accept(Compressor extractor) {
        extractor.compress(this);
    }
}

public class WordFile extends ResourceFile {
    public WordFile(String filePath) {
        super(filePath);
    }

    @Override
    public void accept(Extractor extractor) {
        extractor.extract2txt(this);
    }

    @Override
    public void accept(Compressor extractor) {
        extractor.compress(this);
    }
}

public class Extractor {
    public void extract2txt(PPTFile pptFile) {
        //...
        System.out.println("Extract PPT.");
    }

    public void extract2txt(PdfFile pdfFile) {
        //...
        System.out.println("Extract PDF.");
    }

    public void extract2txt(WordFile wordFile) {
        //...
        System.out.println("Extract WORD.");
    }
}

public class Compressor {
    public void compress(PPTFile pptFile) {
        //...
        System.out.println("Compress PPT.");
    }

    public void compress(PdfFile pdfFile) {
        //...
        System.out.println("Compress PDF.");
    }

    public void compress(WordFile wordFile) {
        //...
        System.out.println("Compress WORD.");
    }
}

// 运行结果是：
// Extract PDF.
// Extract WORD.
// Extract PPT.
// Compress PDF.
// Compress WORD.
// Compress PPT.
public class ToolApplication {
    public static void main(String[] args) {

        String resourceDirectory = args.length != 0 ? args[0] : "123";
        List<ResourceFile> resourceFiles = listAllResourceFiles(resourceDirectory);

        Extractor extractor = new Extractor();
        for (ResourceFile resourceFile : resourceFiles) {
            resourceFile.accept(extractor);
        }

        Compressor compressor = new Compressor();
        for (ResourceFile resourceFile : resourceFiles) {
            resourceFile.accept(compressor);
        }
    }

    private static List<ResourceFile> listAllResourceFiles(String resourceDirectory) {
        List<ResourceFile> resourceFiles = new ArrayList<>();
        //...根据后缀(pdf/ppt/word)由工厂方法创建不同的类对象(PdfFile/PPTFile/WordFile)
        resourceFiles.add(new PdfFile("a.pdf"));
        resourceFiles.add(new WordFile("b.word"));
        resourceFiles.add(new PPTFile("c.ppt"));
        return resourceFiles;
    }
}
```

一般来说，访问者模式针对的是一组类型不同的对象（PdfFile、PPTFile、WordFile）。不过，尽管这组对象的类型是不同的，但是，它们继承相同的父类（ResourceFile）或者实现相同的接口。在不同的应用场景下，我们需要对这组对象进行一系列不相关的业务操作（抽取文本、压缩等），但为了避免不断添加功能导致类（PdfFile、PPTFile、WordFile）不断膨胀，职责越来越不单一，以及避免频繁地添加功能导致的频繁代码修改，我们使用访问者模式，将对象与操作解耦，将这些业务操作抽离出来，定义在独立细分的访问者类（Extractor、Compressor）中。

优化后的代码如下：

- [ResourceFile.java](ResourceFile.java)
    - [PdfFile.java](PdfFile.java)
    - [PPTFile.java](PPTFile.java)
    - [WordFile.java](WordFile.java)
- [Visitor.java](Visitor.java)
    - [Compressor.java](Compressor.java)
    - [Extractor.java](Extractor.java)
- [ToolApplication.java](ToolApplication.java)

### 为什么支持双分派的语言不需要访问者模式呢？

参考代码：

- [double_dispatch](double_dispatch)
- [single_dispatch](single_dispatch)

### 除了访问者模式，该例子还有其他实现方案吗？

参考代码：

- [Extractor.java](alternative_solution%2FExtractor.java)
    - [WordExtractor.java](alternative_solution%2FWordExtractor.java)
    - [PdfExtractor.java](alternative_solution%2FPdfExtractor.java)
    - [PPTExtractor.java](alternative_solution%2FPPTExtractor.java)
- [ExtractorFactory.java](alternative_solution%2FExtractorFactory.java)
- [ResourceFile.java](alternative_solution%2FResourceFile.java)
    - [PdfFile.java](alternative_solution%2FPdfFile.java)
    - [PPTFile.java](alternative_solution%2FPPTFile.java)
    - [WordFile.java](alternative_solution%2FWordFile.java)
- [ResourceFileType.java](alternative_solution%2FResourceFileType.java)
- [ToolApplication.java](alternative_solution%2FToolApplication.java)

当需要添加新的功能的时候，比如压缩资源文件，类似抽取文本内容功能的代码实现，只需要添加一个 Compressor
接口，PdfCompressor、PPTCompressor、WordCompressor 三个实现类，以及创建它们的 CompressorFactory 工厂类即可。唯一需要修改的只有最上层的
ToolApplication 类。基本上符合“对扩展开放、对修改关闭”的设计原则。


