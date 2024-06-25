### Java 语言不支持 Double Dispatch

> 假设 Java 语言支持 Double Dispatch，那下面的代码中 ```extractor.extract2txt(resourceFile);```
> 就不会报错。代码会在运行时，根据参数（resourceFile）的实际类型（PdfFile、PPTFile、WordFile），来决定使用 extract2txt
> 的三个重载函数中的哪一个。那下面的代码实现就能正常运行了，也就不需要访问者模式了。这也回答了为什么支持 Double Dispatch
> 的语言不需要访问者模式。

参考代码如下：
- [Extractor.java](Extractor.java)
- [PdfFile.java](PdfFile.java)
- [ResourceFile.java](ResourceFile.java)
- [ToolApplication.java](ToolApplication.java)