import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mahdi on 5/1/2017.
 * Generating Http Response Header
 * Extract directory of file which requested by HttpRequest
 */
class HttpHelper {

    private final static String ROOT_DIR = "root";
    private final static String CONTENT_LENGTH_KEY = "Content-Length";
    private final static String CONTENT_TYPE_KEY = "Content-Type";
    private final static String HTTP_VERSION = "HTTP/1.1";
    private final static String CONTENT_TYPE_IMAGE_PNG = "image/png";
    private final static String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
    private final static String CONTENT_TYPE_IMAGE_GIF = "image/gif";
    private final static String CONTENT_TYPE_IMAGE_ICO = "image/ico";
    private final static String CONTENT_TYPE_IMAGE_BITMAP = "image/bmp";
    private final static String CONTENT_TYPE_HTML = "text/html";
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String CONTENT_TYPE_XML = "text/xml";
    private final static String CONTENT_TYPE_JAVA_SCRIPT = "application/javascript";
    private final static String CONTENT_TYPE_PLAIN = "text/plain";
    private final static String CONTENT_TYPE_CSS = "text/css";
    private final static String CONTENT_TYPE_EOT = "application/vnd.ms-fontobject";
    private final static String CONTENT_TYPE_SVG = "image/svg+xml";
    private final static String CONTENT_TYPE_TTF = "application/font-sfnt";
    private final static String CONTENT_TYPE_WOFF = "application/font-woff";
    private final static String CONTENT_TYPE_WOFF2 = "application/x-font-ttf";
    private final static String CONTENT_TYPE_RAR = "application/x-rar-compressed, application/octet-stream";
    private final static String CONTENT_TYPE_ZIP = "application/zip, application/octet-stream";
    private final static String CR_LF = "\r\n";
    private final static int RESPONSE_CODE_OK = 200;
    private final static String RESPONSE_OK = "OK";
    private final static int RESPONSE_CODE_BAD_REQUEST = 400;
    private final static String RESPONSE_BAD_REQUEST = "Bad Request";
    private final static int RESPONSE_CODE_NOT_FOUND = 404;
    private final static String RESPONSE_NOT_FOUND = "Not Found";

    ResponseEntity getResponseEntity(String httpRequest) {
        String fullPath = extractFullDir(httpRequest);
        if(fullPath != null){
            File file = new File(fullPath);
            int code = RESPONSE_CODE_OK;
            String message = RESPONSE_OK;
            String type = CONTENT_TYPE_HTML;
            if(!file.exists()){
                code = RESPONSE_CODE_NOT_FOUND;
                message = RESPONSE_NOT_FOUND;
                return new ResponseEntity(null, responseHeaderBuilder(code, message, type, 0));
            }
            String fileExtension = getFileExtension(fullPath);
            type = getContentType(fileExtension);
            int length = (int) file.length();
            return new ResponseEntity(new File(fullPath),
                    responseHeaderBuilder(code, message, type, length));
        }else {
            return new ResponseEntity(null, responseHeaderBuilder(RESPONSE_CODE_BAD_REQUEST,
                    RESPONSE_BAD_REQUEST, CONTENT_TYPE_HTML, 0));
        }
    }

    private String extractFullDir(String request){
        String[] lines = request.split("[\n)]");
        boolean isHeaderCorrect = false;
        String subDir = "";
        for (String line :
                lines) {
            Matcher m1 = Pattern.compile(".*GET (.+) HTTP/.*").matcher(line);  // Directory of requested file
            if(m1.matches()){
                isHeaderCorrect = true;
                subDir = m1.group(1);
                if (subDir.equals("/")) {
                    subDir = "/index.html";
                }
                Matcher m2 = Pattern.compile("(/[a-zA-Z_0-9-]*)+(/?)").matcher(subDir);  // if Absolute directory requested
                if(m2.matches()){
                    subDir += "/index.html";
                }
            }
            String portNum = String.valueOf(ServerSocketBroker.PORT_NUMBER);
            Matcher m3 = Pattern.compile("Referer: .*:"+portNum+"((/[a-zA-Z_0-9-]*)+(/?))").matcher(line);
            if(m3.matches()){
                String referer = m3.group(1);
                Matcher m4 = Pattern.compile("(.+)/([a-zA-Z_0-9-]+)").matcher(referer);
                if(m4.matches()){
                    String part1 = m4.group(1);
                    String part2 = m4.group(2);
                    subDir = subDir.replace(part1, part1 + "/" + part2);
                }
                Matcher m5 = Pattern.compile("/([a-zA-Z_0-9-]+)").matcher(referer);
                if(m5.matches()){
                    subDir = "/" + m5.group(1) + subDir;
                }
            }
        }
        String fullPath = ROOT_DIR + subDir;
        fullPath = fullPath.replace("/", "\\");
        if(isHeaderCorrect){
            return fullPath;
        }else {
            return null;
        }
    }

    private String responseHeaderBuilder(int code, String message, String type, int length) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTTP_VERSION);
        builder.append(" ");
        builder.append(code);
        builder.append(" ");
        builder.append(message);
        builder.append(CR_LF);
        builder.append(CONTENT_TYPE_KEY);
        builder.append(": ");
        builder.append(type);
        builder.append(CR_LF);
        builder.append(CONTENT_LENGTH_KEY);
        builder.append(": ");
        builder.append(length);
        builder.append(CR_LF);
        builder.append(CR_LF);
        return builder.toString();
    }

    private String getFileExtension(String fullPath) {
        String fileName = new File(fullPath).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private String getContentType(String extension){
        String type = CONTENT_TYPE_HTML;
        switch (extension){
            case "png":
                type = CONTENT_TYPE_IMAGE_PNG;
                break;
            case "jpg":
            case "jpeg":
                type = CONTENT_TYPE_IMAGE_JPEG;
                break;
            case "gif":
                type = CONTENT_TYPE_IMAGE_GIF;
                break;
            case "bmp":
                type = CONTENT_TYPE_IMAGE_BITMAP;
                break;
            case "ico":
                type = CONTENT_TYPE_IMAGE_ICO;
                break;
            case "json":
                type = CONTENT_TYPE_JSON;
                break;
            case "xml":
                type = CONTENT_TYPE_XML;
                break;
            case "js":
                type = CONTENT_TYPE_JAVA_SCRIPT;
                break;
            case "md":
            case "txt":
                type = CONTENT_TYPE_PLAIN;
                break;
            case "css":
            case "less":
            case "map":
                type = CONTENT_TYPE_CSS;
                break;
            case "eot":
                type = CONTENT_TYPE_EOT;
                break;
            case "svg":
                type = CONTENT_TYPE_SVG;
                break;
            case "ttf":
                type = CONTENT_TYPE_TTF;
                break;
            case "woff":
                type = CONTENT_TYPE_WOFF;
                break;
            case "woff2":
                type = CONTENT_TYPE_WOFF2;
                break;
            case "zip":
                type = CONTENT_TYPE_ZIP;
                break;
            case "rar":
                type = CONTENT_TYPE_RAR;
                break;
        }
        return type;
    }


}
