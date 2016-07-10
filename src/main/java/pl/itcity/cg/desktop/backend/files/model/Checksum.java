package pl.itcity.cg.desktop.backend.files.model;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Checksum metadata object
 *
 * @author Michal Adamczyk
 */
public class Checksum {
    /**
     * checksum algorythm
     */
    private String algorythm;
    /**
     * checksum value
     */
    private byte[] value;
    /**
     * checksum calculation date
     */
    private Date lastModified;

    public String getAlgorythm() {
        return algorythm;
    }

    public void setAlgorythm(String algorythm) {
        this.algorythm = algorythm;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * factory method for checksum creation
     *
     * @param algorythm
     *         algorythm
     * @param value
     *         value
     * @param lastModified
     *         creation date
     * @return checkum with given params
     */
    public static Checksum valueOf(String algorythm, byte[] value, Date lastModified) {
        Checksum checksum = new Checksum();
        checksum.setAlgorythm(algorythm);
        checksum.setLastModified(lastModified);
        checksum.setValue(value);
        return checksum;
    }

    /**
     * checks if this checksum is same as other, i.e. if it has same algorythm and checksum value
     *
     * @param other
     *         other, not null
     * @return true iff checksum is same as other
     */
    public boolean isSameAs(Checksum other) {
        return StringUtils.equals(algorythm, other.algorythm) && Arrays.equals(value, other.value);
    }
}
