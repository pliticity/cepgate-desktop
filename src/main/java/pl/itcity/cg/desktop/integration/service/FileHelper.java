package pl.itcity.cg.desktop.integration.service;

import javafx.stage.FileChooser;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * @author Patryk Majchrzycki
 */
public class FileHelper {

    public static FileChooser initFileChooser(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(fileName);
        String property = System.getProperty("user.home");
        fileChooser.setInitialDirectory(Paths.get(property)
                .toFile());
        return fileChooser;
    }

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
                            return splittedFilenameheader[1].replace("\"","");
                        } else {
                            return null;
                        }
                    })
                            .orElse(null);
                })
                .orElse(null);
    }

}
