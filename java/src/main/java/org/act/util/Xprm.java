package org.act.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dashoptimization.XPRM;
import com.dashoptimization.XPRMCompileException;
import com.dashoptimization.XPRMModel;

/**
 * Provides access to the XPRM singleton.
 */
public final class Xprm {

    /**
     * Logger for creating the {@code XPRM} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Xprm.class);

    /**
     * Instance of the {@code XPRM}.
     */
    private static final XPRM INSTANCE = new XPRM();

    /**
     * Path of the compiled BIM file.
     */
    private static final String BIM_FILE_PATH;

    /**
     * Path of the mos source file.
     */
    private static final String MOS_FILE_PATH = "org/act/mosel/shadow_test.mos";

    static {
        try {
            BIM_FILE_PATH = compileBIM(MOS_FILE_PATH);
        } catch (IOException e) {
            String message = "Exception loading BIM from path:" + MOS_FILE_PATH;
            throw new IllegalStateException(message, e);
        }
    }

    /**
     * Makes the constructor private so that this class cannot be instantiated.
     */
    private Xprm() {
    }

    /**
     * Gets a new XPRMModel.
     *
     * @return an {@link XPRMModel}
     * @throws IOException if there is an io failure
     */
    public static XPRMModel newModel() throws IOException {
        return INSTANCE.loadModel(BIM_FILE_PATH);
    }

    /**
     * Compiles the Mosel source file.
     *
     * @param sourcePath the path to the Mosel source (.mos) file
     * @param destPath the path to the Mosel destination (.bim) file
     * @throws XPRMCompileException if there is a compilation failure
     */
    private static void compile(String sourcePath, String destPath) throws XPRMCompileException {
        INSTANCE.compile(null, sourcePath, destPath);
    }

    /**
     * Compiles the Mosel source file (.mos) to the binary file (.bim).
     *
     * @param sourceFileResource the path to the Mosel source file
     * @return the path to the Mosel binary file
     * @throws IOException if there is an IO failure
     */
    private static String compileBIM(String sourceFileResource) throws IOException {
        File tempFile = File.createTempFile("model", ".mos");
        tempFile.deleteOnExit();
        ClassLoader classLoader = Xprm.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(sourceFileResource);
        FileUtils.copyInputStreamToFile(stream, tempFile);
        String outputPath = tempFile.getPath().replaceFirst("mos", "bim");
        try {
            compile(tempFile.getPath(), outputPath);
        } catch (XPRMCompileException e) {
            LOGGER.error("Exception compiling BIM", e);
        }
        return outputPath;
    }

}
