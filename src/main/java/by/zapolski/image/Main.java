package by.zapolski.image;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

public class Main {

    public static final String DESTINATION_PATH = "d:/Test/images";
    public static final String SOURCE_PATH = "C:/Users/Siarhei_Zapolski/AppData/Local/Packages/Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy/LocalState/Assets";
    public static final long SIZE_RANGE_BYTES = 100 * 1024L;
    public static final String ALGORITHM = "MD5";

    static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        File destDir = new File(DESTINATION_PATH);
        checkExisting(destDir);
        File sourceDir = new File(SOURCE_PATH);
        checkExisting(sourceDir);

        File[] existImgFiles = destDir.listFiles();
        List<String> checkSumsList = new ArrayList<>();
        if (existImgFiles != null) {
            for (File file : existImgFiles) {
                String fileName = file.getName();
                String checkSum = fileName.substring(fileName.indexOf('[') + 1, fileName.indexOf(']'));
                checkSumsList.add(checkSum);
            }
        } else {
            LOG.info(String.format("Destination directory %s is empty", DESTINATION_PATH));
        }

        File[] newImgFiles = sourceDir.listFiles();
        if (newImgFiles != null) {
            List<File> imgFiles = Arrays.stream(newImgFiles).filter(f -> f.length() > SIZE_RANGE_BYTES).collect(Collectors.toList());
            for (File file : imgFiles) {
                String checkSum = getCheckSum(ALGORITHM, file);
                if (!checkSumsList.contains(checkSum)) {
                    //LOG.log(Level.INFO, "\tFound out a new file {0}!", file.getName());

                    String newName = getNewFileName(file, checkSum);
                    String newPath = DESTINATION_PATH + File.separator + newName;
                    File newFile = new File(newPath);
                    Files.copy(file.toPath(), newFile.toPath());

                    //LOG.log(Level.INFO, "\tNew file has already copied with name: {0}", newFile.getName());
                } else {
                    //LOG.log(Level.INFO, "File {0} has already existed!", file.getName());
                }
            }
        } else {
            //LOG.log(Level.INFO, "Source directory {0} is empty.", SOURCE_PATH);
        }
    }

    private static void checkExisting(File file) {
        if (!file.exists()) {
            LOG.info(String.format("Directory or file %s does not exist.", file.getAbsolutePath()));
            System.exit(0);
        }
    }

    private static String getNewFileName(File file, String checkSum) throws IOException {
        StringBuilder newName = new StringBuilder();
        Image img = ImageIO.read(file);
        if (isImageLandscape(img)) {
            newName.append("A ");
        } else {
            newName.append("Ap ");
        }
        newName.append("[").append(checkSum).append("].jpg");
        return newName.toString();
    }

    public static String getCheckSum(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fis = new FileInputStream(file)) {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] dataBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(dataBytes)) > 0) {
                md.update(dataBytes, 0, bytesRead);
            }
            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
    }

    public static boolean addCheckSumToFileName(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
        String newName = file.getName().replace(".", " [" + getCheckSum(algorithm, file) + "].");
        String newPath = file.getParent() + File.separator + newName;
        File newFile = new File(newPath);
        return file.renameTo(newFile);
    }

    private static boolean isImageLandscape(Image image) {
        int height = image.getHeight(null);
        int weight = image.getWidth(null);
        return weight > height;
    }

}

