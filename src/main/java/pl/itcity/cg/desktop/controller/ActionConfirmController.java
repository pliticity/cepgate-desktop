package pl.itcity.cg.desktop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.controller.common.AfterCloseExecutor;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;

/**
 * Controller handling confirmation process
 *
 * @author Michal Adamczyk
 */
public class ActionConfirmController implements ParentNodeAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConfirmController.class);

    @FXML
    private Parent view;
    @FXML
    private Label detailsLabel;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private AfterCloseExecutor afterConfirmExecutor;

    private AfterCloseExecutor afterNotConfirmExecutor;

    @FXML
    protected void handleOk() {
        CgApplication.getInstance().closePopup();
        if (afterConfirmExecutor != null) {
            afterConfirmExecutor.execute();
            afterConfirmExecutor = null;
        } else {
            LOGGER.warn("'ok' action confirmed, but no executor found");
        }
    }

    @FXML
    protected void handleCancel() {
        CgApplication.getInstance()
                .closePopup();
        if (afterNotConfirmExecutor != null) {
            afterNotConfirmExecutor.execute();
            afterNotConfirmExecutor = null;
        } else {
            LOGGER.warn("'cancel' action confirmed, but no executor found");
        }
    }

    public void setAfterConfirmExecutor(AfterCloseExecutor afterConfirmExecutor) {
        this.afterConfirmExecutor = afterConfirmExecutor;
    }

    public void setAfterNotConfirmExecutor(AfterCloseExecutor afterNotConfirmExecutor) {
        this.afterNotConfirmExecutor = afterNotConfirmExecutor;
    }

    @Override
    public Parent getView() {
        return view;
    }

    /**
     * updates details label value
     *
     * @param value
     *         value
     */
    public void updateDetailsValue(String value) {
        detailsLabel.setText(value);
    }

    public void setConfirmButtonLabel(String label){
        this.okButton.setText(label);
    }

    public void setNotConfirmButtonLabel(String label){
        this.cancelButton.setText(label);
    }
}
