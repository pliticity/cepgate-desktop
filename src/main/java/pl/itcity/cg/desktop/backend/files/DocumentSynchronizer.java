package pl.itcity.cg.desktop.backend.files;

import java.io.IOException;

/**
 * interface encapsulating document synchronization
 *
 * @author Michal Adamczyk
 */
public interface DocumentSynchronizer {

    /**
     * registers document change watchers for given directory
     *
     */
    void registerDocumentChangeWatchers() throws IOException;
}
