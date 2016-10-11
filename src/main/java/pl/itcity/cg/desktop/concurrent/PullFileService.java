package pl.itcity.cg.desktop.concurrent;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.text.MessageFormat;
import java.util.Collections;

/**
 * Service responsible for pulling and opening the file initiated through jms
 *
 * @author Patryk Majchrzycki
 */
@Component
@Scope("prototype")
public class PullFileService extends BaseRestService<ResponseEntity<byte[]>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullFileService.class);

    private String fileSymbol;

    @Override
    protected Task<ResponseEntity<byte[]>> createTask() {
        return new Task<ResponseEntity<byte[]>>() {
            @Override
            protected ResponseEntity<byte[]> call() throws Exception {
                LOGGER.info(MessageFormat.format("Pulling file with symbol {0}", getFileSymbol()));
                HttpHeaders headers = serviceInvoker.getAuthHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                HttpEntity<byte[]> downloadFileEnity = new HttpEntity<>(headers);
                String path = MessageFormat.format("file/{0}", getFileSymbol());
                ResponseEntity<byte[]> response = serviceInvoker.exchange(path, HttpMethod.GET, downloadFileEnity, byte[].class);
                if(!HttpStatus.OK.equals(response.getStatusCode())){
                    throw new HttpServerErrorException(response.getStatusCode());
                }else{
                    return response;
                }
            }
        };
    }

    public String getFileSymbol() {
        return fileSymbol;
    }

    public void setFileSymbol(String fileSymbol) {
        this.fileSymbol = fileSymbol;
    }
}
