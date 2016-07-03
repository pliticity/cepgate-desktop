package pl.itcity.cg.desktop.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import pl.itcity.cg.desktop.concurrent.DocumentListService;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;
import pl.itcity.cg.desktop.helper.FormattedDateTableCell;
import pl.itcity.cg.desktop.model.Classification;
import pl.itcity.cg.desktop.model.DocumentInfo;
import pl.itcity.cg.desktop.model.FileInfo;
import pl.itcity.cg.desktop.model.Principal;

/**
 * Controller for document list view
 *
 * @author Michal Adamczyk
 */
public class DocumentListController extends BaseController implements ParentNodeAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentListController.class);
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final double LIST_CELL_HEIGHT = 30d;

    @Resource
    private DocumentListService documentListService;

    @FXML
    private Parent documentListView;

    @FXML
    private TableView<DocumentInfo> documentList;

    @FXML
    private TableColumn<DocumentInfo,String> classificationIdColumn;

    @FXML
    private TableColumn<DocumentInfo,String> classificationNameColumn;

    @FXML
    private TableColumn<DocumentInfo,String> documentNumberColumn;

    @FXML
    private TableColumn<DocumentInfo,String> documentTypeColumn;

    @FXML
    private TableColumn<DocumentInfo,String> documentNameColumn;

    @FXML
    private TableColumn<DocumentInfo,Date> creationDateColumn;

    @FXML
    private TableColumn<DocumentInfo,String> createdByColumn;

    @FXML
    private TableColumn<DocumentInfo,String> filesCountColumn;

    @FXML
    //private TableColumn<DocumentInfo,List<FileInfo>> selectedFilesColumn;
    private TableColumn<DocumentInfo,String> selectedFilesColumn;

    @FXML
    private Button refreshButton;

    @Override
    public Parent getView() {
        return documentListView;
    }

    @PostConstruct
    private void init(){
        String code = "document.list.noDocuments";
        documentList.setPlaceholder(new Text(getMessage(code)));
        classificationIdColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            String classificationId = Optional.ofNullable(value.getClassification())
                    .map(Classification::getClassificationId)
                    .orElse(StringUtils.EMPTY);
            return new ReadOnlyObjectWrapper<>(classificationId);
        });
        classificationIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        classificationNameColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            String classifiationName = Optional.ofNullable(value.getClassification())
                    .map(Classification::getName)
                    .orElse(StringUtils.EMPTY);
            return new ReadOnlyObjectWrapper<>(classifiationName);
        });
        classificationNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        documentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));
        documentNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        documentTypeColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            String type = Optional.ofNullable(value.getType())
                    .map(Enum::name)
                    .orElse(StringUtils.EMPTY);
            return new ReadOnlyObjectWrapper<>(type);
        });
        documentTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        documentNameColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        documentNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        creationDateColumn.setCellFactory(param -> new FormattedDateTableCell<>(DD_MM_YYYY));
        creationDateColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            return new ReadOnlyObjectWrapper<>(value.getCreationDate());
        });
        createdByColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            String createdBy = Optional.ofNullable(value.getCreatedBy())
                    .map(Principal::getEmail)
                    .orElse(StringUtils.EMPTY);
            return new ReadOnlyObjectWrapper<>(createdBy);
        });
        createdByColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        filesCountColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            long count = Optional.ofNullable(value.getFiles())
                    .orElse(Collections.emptyList())
                    .stream()
                    .count();
            return new ReadOnlyObjectWrapper<>(String.valueOf(count));
        });
        filesCountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
/*        selectedFilesColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            List<FileInfo> fileInfos = Optional.ofNullable(value.getFiles())
                    .orElse(Collections.emptyList());
            return new ReadOnlyObjectWrapper<>(fileInfos);
        });
        selectedFilesColumn.setCellFactory(param -> new TableCell<DocumentInfo, List<FileInfo>>(){
            @Override
            protected void updateItem(List<FileInfo> fileInfos,boolean empty){
                ListView<FileInfo> fileInfoListView = getFileInfoListView();
                super.updateItem(fileInfos,empty);
                if (fileInfos == null || empty) {
                    //setGraphic(null);
                    //setText(StringUtils.EMPTY);
                    //setHeight(0d);
                    fileInfoListView.setPlaceholder(new Text(StringUtils.EMPTY));
                    fileInfoListView.setPrefHeight(30d);
                } else {
                    fileInfoListView.getItems()
                            .addAll(fileInfos);
                    fileInfoListView.prefHeightProperty()
                            .bind(Bindings.size(FXCollections.observableArrayList(fileInfos))
                                          .multiply(LIST_CELL_HEIGHT).add(30d));
                }
                setGraphic(fileInfoListView);
            }
        });*/
        selectedFilesColumn.setCellValueFactory(param -> {
            DocumentInfo value = param.getValue();
            String fileListAsString = Optional.ofNullable(value.getFiles())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(FileInfo::getName)
                    .collect(Collectors.joining("\n"));
            return new ReadOnlyObjectWrapper<>(fileListAsString);
        });
        selectedFilesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private ListView<FileInfo> getFileInfoListView() {
        ListView<FileInfo> fileInfoListView = new ListView<>();
        fileInfoListView.setCellFactory(fileInfo -> new ListCell<FileInfo>(){
            @Override
            protected void updateItem(FileInfo item,boolean empty){
                super.updateItem(item,empty);
                if (empty || item == null) {
                    setText(StringUtils.EMPTY);
                } else {
                    setText(item.getName());
                }
            }
        });
        return fileInfoListView;
    }

    public void fetchDocuments() {
        documentList.getItems().clear();
        if (documentListService.isRunning()){
            LOGGER.warn("documentListService allready running");
        } else {
            refreshButton.setDisable(true);
            documentList.setPlaceholder(new Text(getMessage("document.list.gettingDocuments")));
            documentListService.setOnSucceeded(event -> {
                List<DocumentInfo> documentInfos = documentListService.getValue();
                documentList.getItems().clear();
                documentList.getItems().addAll(documentInfos);
                refreshButton.setDisable(false);
            });
            documentListService.setOnFailed(event -> {
                Throwable exception = documentListService.getException();
                LOGGER.error("exception while getting documents: ",exception);
                documentList.setPlaceholder(new Text(getMessage("document.list.error",new Object[]{exception.getMessage()})));
                refreshButton.setDisable(false);
            });
            documentListService.restart();
        }
    }

}
