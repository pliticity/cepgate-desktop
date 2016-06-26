package pl.itcity.cg.desktop.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import javafx.scene.control.TableCell;

/**
 * @author Michal Adamczyk
 */
public class FormattedDateTableCell<T> extends TableCell<T,Date> {

    /**
     * date format pattern
     */
    private final String formatPattern;

    /**
     * @param formatPattern
     *         valid date format pattern
     */
    public FormattedDateTableCell(String formatPattern) {
        Preconditions.checkArgument(StringUtils.isNotBlank(formatPattern), "format pattern can not be empty");
        this.formatPattern = formatPattern;
    }

    @Override
    protected void updateItem(Date value, boolean empty) {
        super.updateItem(value, empty);
        if (value != null) {
            String formattedDate = new SimpleDateFormat(formatPattern).format(value);
            setText(formattedDate);
        }
    }
}
