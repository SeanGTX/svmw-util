import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Менедженер создания и загрузки сохранений в игру<br/>
 * Создает bundle-файлы .svmw и загружает их<br/>
 * Умеет загружать обычные .sb сохранения в игру
 */
public class SaveManager {

    private static final String LOG_TAG = "SaveManager";
    private static final byte[] svmw_header = "SVMW".getBytes(StandardCharsets.UTF_8);
    private static final byte[] save_header = "SBIN".getBytes(StandardCharsets.UTF_8);
    private ByteBuffer byteBuffer;
    private int bufferSize = 8;

    public static final String dateFormatStr = "dd.MM.yy HH:mm:ss";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);

    private Long wrap(byte[] array){
        return ByteBuffer
                .wrap(array)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getLong();
    }

    private ByteBuffer putLong(Long long1){
        return (ByteBuffer) ByteBuffer
                .allocate(bufferSize)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(long1)
                .flip();
    }

    public File createBundleFile(String description, File fileToSave, File save) {

        try {

            FileOutputStream fos = new FileOutputStream(fileToSave, false);

            Date date = new Date();

            Long time = date.getTime();
            byteBuffer = putLong(time);
            fos.write(svmw_header);
            fos.write(byteBuffer.array());
            fos.write(description.getBytes(StandardCharsets.UTF_8));
            fos.write(fileAsByteArray(save));
        } catch (IOException e) {
            //fileToSave.getAbsolutePath()
            e.printStackTrace();
        }

        return fileToSave;
    }

    public String getDescriptionOf(File svmw){
        if(isSvmwFile(svmw)){
            int offset = svmw_header.length + bufferSize;
            byte[] byteFile = fileAsByteArray(svmw);
            int headerInByteFile = findHeaderInByteFile(byteFile, save_header);
            return new String(byteFile, offset, headerInByteFile - offset);
        }
        System.out.println(LOG_TAG + " getDescription(), this is not a svmw file((( Return empty description(((");
        return "";
    }

    public Date getDateOfCreateOf(File svmw){
        if(isSvmwFile(svmw)){
            int offset = svmw_header.length;
            byte[] byteFile = fileAsByteArray(svmw);

            byte[] dateInBytes = new byte[bufferSize];
            System.arraycopy(byteFile, offset, dateInBytes, 0, bufferSize);

            Long dateMs = wrap(dateInBytes);

            Date date = new Date();
            date.setTime(dateMs);

            return date;
        }
        System.out.println(LOG_TAG + " getDateOfCreateOf, this is not a svmw file((( Return null date(((");
        return null;
    }

    public String getFormattedDate(File svmw){
        return dateFormat.format(getDateOfCreateOf(svmw));
    }

    public boolean isSaveFile(File save) {
        if(!save.exists() | isEmptyFile(save)) return false;
        byte[] arr = fileAsByteArray(save);
        for (int i = 0; i < save_header.length; i++)
            if (save_header[i] != arr[i]) return false;
        return true;
    }

    public boolean isSvmwFile(File svmw) {
        if(!svmw.exists() | isEmptyFile(svmw)) return false;
        byte[] arr = fileAsByteArray(svmw);
        for (int i = 0; i < svmw_header.length; i++)
            if (svmw_header[i] != arr[i]) return false;
        return true;
    }


    public boolean isEmptyFile(File file){
        return fileAsByteArray(file).length == 0;
    }

    public static byte[] fileAsByteArray(File file){
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
        } catch (Exception e) {
            return null;
        }
        return b;
    }

    private int findHeaderInByteFile(byte[] byteFile, byte[] header){
        int[] pf = prefix(header);
        int index = 0;
        for (int i = 0; i < byteFile.length; i++){
            while (index > 0 && header[index] != byteFile[i]) index = pf[index - 1];
            if (header[index] == byteFile[i]) index++;
            if (index == header.length) return i - index + 1;
        }
        return -1;
    }
    /**
     * Префикс функция для алгоритма КМП
     */
    private static int[] prefix(byte[] s) {
        int[] result = new int[s.length];
        result[0] = 0;
        int index = 0;
        for (int i = 1; i < s.length; i++) {
            while (index >= 0 && s[index] != s[i]) index--;
            index++;
            result[i] = index;
        }
        return result;
    }
}
