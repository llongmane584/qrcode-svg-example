package qrcode.svg.example.svg;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;

import lombok.Getter;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVG {
    public static final int FINDER_PATTERN_SIZE = 7; //finderPatternSize = 7;
    public static final int QR_SIZE_BASE = 21;

    @Getter
    private final String content;
    @Getter
    private final int qrCodeVersion;
    @Getter
    private ErrorCorrectionLevel qrCodErrorCorrectionLevel;
    @Getter
    private final float shapeSizeRatio;
    @Getter
    private final String fileOutputPath;
    @Getter
    private final String onColour;
    @Getter
    private final String offColour;
    @Getter
    private final int quietZoneSize;
    @Getter
    private final String fillShape;
    @Getter
    private final int scaling;
    @Getter
    private final int qrCodeSize;
    
    /**
     * Get QR Code specs from application.properties.
     */
    public PrintQRSVG() {
        ResourceBundle props = ResourceBundle.getBundle("application");
        this.content = props.getString("qrcode.content");
        this.qrCodeVersion =  Integer.parseInt(props.getString("qrcode.version"));
        this.qrCodErrorCorrectionLevel = ErrorCorrectionLevel.valueOf(props.getString("qrcode.errorCorrectionLevel"));
        this.shapeSizeRatio = Float.parseFloat(props.getString("qrcode.shapeSizeRatio"));
        this.fileOutputPath = props.getString("qrcode.fileOutputPath");
        this.onColour = props.getString("qrcode.onColour");
        this.offColour = props.getString("qrcode.offColour");
        this.quietZoneSize = Integer.parseInt(props.getString("qrcode.quietZoneSize"));

        this.scaling = Integer.parseInt(props.getString("qrcode.scaling"));
        this.qrCodeSize = QR_SIZE_BASE + (qrCodeVersion - 1) * 4;

        // initialise suffix of output file name from subclass fuffix (PrintQRSVGCircle -> circle, PrintQRSVGSquare -> square)
        this.fillShape = getClass().getSimpleName().replace(getClass().getSuperclass().getSimpleName(), "").toLowerCase();
    }


    /**
     * Generate an SVG format QR Code file.
     * @throws Exception
     */
    public void write() throws Exception {
        final ConcurrentHashMap<EncodeHintType, Object> hints = new ConcurrentHashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.QR_VERSION, qrCodeVersion);
        QRCode code = Encoder.encode(content, qrCodErrorCorrectionLevel, hints);

        StringBuilder svgText = new StringBuilder();
        svgText.append("<?xml version ='1.0'?>" + System.lineSeparator());
        svgText.append("<svg xmlns='http://www.w3.org/2000/svg'>" + System.lineSeparator());
        // draw SVG canvas
        int canvasSize = qrCodeSize * scaling + quietZoneSize * scaling * 2;
        svgText.append("<rect id='background' x='0' y='0' width='" + String.valueOf(canvasSize) + "' height='" + String.valueOf(canvasSize) + "' stroke='rgb(" + offColour + ")' fill='rgb(" + offColour + ")' stroke-width='2' />");
        svgText.append("\n");
        // draw QR Code content
        svgText.append(renderQRImage(code));
        svgText.append("</svg>");
        Files.writeString(Paths.get(fileOutputPath + qrCodeVersion + "-" + "x" + String.valueOf(scaling) + "-" + fillShape + ".svg"), svgText);
    }

    public StringBuilder renderQRImage(QRCode code){
        return new StringBuilder();
    };
}
