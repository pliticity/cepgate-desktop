package pl.itcity.cg.desktop.concurrent;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javafx.concurrent.Task;
import pl.itcity.cg.desktop.model.DocumentInfo;

/**
 * @author Michal Adamczyk
 */
@Component
@Scope("prototype")
public class DocumentListService extends BaseRestService<List<DocumentInfo>>{


    @Override
    protected Task<List<DocumentInfo>> createTask() {
        return new Task<List<DocumentInfo>>() {
            @Override
            protected List<DocumentInfo> call() throws Exception {
                HttpEntity<Void> httpEntity = new HttpEntity<>(null, serviceInvoker.getAuthHeaders());
                ResponseEntity<DocumentInfo[]> getDocumentsResult = serviceInvoker.exchange("document/query?my=true", HttpMethod.GET, httpEntity, DocumentInfo[].class);
                return Arrays.asList(getDocumentsResult.getBody());
            }
        };
    }
}
