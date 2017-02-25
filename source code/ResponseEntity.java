import java.io.File;

/**
 * Created by Mahdi on 5/1/2017.
 * Information which needed to access a file and it's type
 */
class ResponseEntity {

    private String header;
    private File file;

    ResponseEntity(File f, String h){
        this.file = f;
        this.header = h;
    }

    String getHeader() {
        return header;
    }

    File getFile() {
        return file;
    }

}
