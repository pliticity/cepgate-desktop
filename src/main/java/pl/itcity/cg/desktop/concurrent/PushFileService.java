package pl.itcity.cg.desktop.concurrent;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import pl.itcity.cg.desktop.model.FileInfo;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;

/**
 * Service responsible for pushing file initiated through jms
 *
 * @author Patryk Majchrzycki
 */
@Component
@Scope("prototype")
public class PushFileService extends BaseRestService<ResponseEntity<FileInfo>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushFileService.class);

    private Path path;

    private String fileId;

    private String dicId;

    public PushFileService(Path path, String fileId, String dicId) {
        this.path = path;
        this.fileId = fileId;
        this.dicId = dicId;
    }

    public PushFileService() {
    }

    @Override
    protected Task<ResponseEntity<FileInfo>> createTask() {
        return new Task<ResponseEntity<FileInfo>>() {
            @Override
            protected ResponseEntity<FileInfo> call() throws Exception {
                HttpHeaders headers = serviceInvoker.getAuthHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
                multipartMap.add("file", new UrlResource(path.toAbsolutePath()
                        .toUri()));
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartMap, headers);
                String url = MessageFormat.format("/document/{0}/reupload/{1}",dicId,fileId);
                ResponseEntity<FileInfo> response = serviceInvoker.exchange(url, HttpMethod.POST, requestEntity, FileInfo.class);
                return response;
            }
        };
    }


}
