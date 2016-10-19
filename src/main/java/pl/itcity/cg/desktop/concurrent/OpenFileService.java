package pl.itcity.cg.desktop.concurrent;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.awt.*;
import java.io.File;

public class OpenFileService extends Service<Void>{

    private File file;

    public OpenFileService(File file) {
        this.file = file;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Desktop.getDesktop().open(file);
                return null;
            }
        };
    }
}
