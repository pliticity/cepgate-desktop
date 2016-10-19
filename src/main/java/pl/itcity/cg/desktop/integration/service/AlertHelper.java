package pl.itcity.cg.desktop.integration.service;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * @author Patryk Majchrzycki
 */
public class AlertHelper {

    public static String getFileName(ResponseEntity<byte[]> response) {
        return Optional.ofNullable(response.getHeaders()
                .get("Content-Disposition"))
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(filenameHeader -> {
                    Optional<String> filename = Arrays.stream(filenameHeader.split(";"))
                            .filter(s -> s.contains("filename"))
                            .findFirst();
                    return filename.map(filenameHeaderValue -> {
                        String[] splittedFilenameheader = filenameHeaderValue.split("=");
                        if (splittedFilenameheader.length == 2) {
                            return splittedFilenameheader[1].replace("\"", "");
                        } else {
                            return null;
                        }
                    })
                            .orElse(null);
                })
                .orElse(null);
    }

}
