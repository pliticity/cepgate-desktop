package pl.itcity.cg.desktop.controller.common;

import javafx.scene.Parent;

/**
 * Controller interface enabling access to parent node of supported view
 *
 * @author Michal Adamczyk
 */
public interface ParentNodeAware {

    /**
     * gets root node of supported view
     * @return parent node
     */
    Parent getView();
}
