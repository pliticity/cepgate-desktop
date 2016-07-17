package pl.itcity.cg.desktop.backend.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for checksum calculation
 *
 * @author Michal Adamczyk
 */
public class ChecksumUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChecksumUtil.class);
    /**
     * default chekcsum algorythm (md5)
     */
    public static final String MD5 = "MD5";

    /**
     * private sonstructor since this is a utility class
     */
    private ChecksumUtil() {
    }

    /**
     * calculates hash of given file with given algorythm.
     *
     * @param file
     *         path to file
     * @param algorythm
     *         algorythm
     * @return calculated checksum or empty byte array if checksum algorythm is not supported
     * @throws IOException
     */
    public static byte[] calculateChecksum(Path file, String algorythm) throws IOException {
        byte[] checksum = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance(algorythm);
            try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                readBytesAndUpdateDigest(digest, fileInputStream);
            }
            return digest.digest();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("unable to calculate checksum", e);
        }
        return checksum;
    }

    /**
     * reads bytes of fileInputStream and updates digest
     *
     * @param digest
     *         digest
     * @param fileInputStream
     *         input stream
     * @throws IOException
     */
    private static void readBytesAndUpdateDigest(MessageDigest digest, FileInputStream fileInputStream) throws
                                                                                                        IOException {
        byte[] buffer = new byte[1024];
        int numRead;
        do {
            numRead = fileInputStream.read(buffer);
            if (numRead > 0) {
                digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
    }
}
