package org.coder.err.programming._2_design_chapter.apidesgin.apiasyncsyncmode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RequestMapping("apiasyncsyncmode")
@RestController
public class APIAsyncSyncModeController {

    @Resource
    private FileService fileService;

    @GetMapping("wrong")
    public UploadResponse upload() {
        UploadRequest request = new UploadRequest();
        return fileService.upload(request);
    }

    @GetMapping("syncUpload")
    public SyncUploadResponse syncUpload() {
        SyncUploadRequest request = new SyncUploadRequest();
        return fileService.syncUpload(request);
    }

    @GetMapping("asyncUpload")
    public AsyncUploadResponse asyncUpload() {
        AsyncUploadRequest request = new AsyncUploadRequest();
        return fileService.asyncUpload(request);
    }

    @GetMapping("syncQuery")
    public SyncQueryUploadTaskResponse syncQuery(@RequestParam("taskId") String taskId) {
        SyncQueryUploadTaskRequest request = new SyncQueryUploadTaskRequest(taskId);
        return fileService.syncQueryUploadTask(request);
    }

}
