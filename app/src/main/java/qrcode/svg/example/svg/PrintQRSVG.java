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

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public abstract class PrintQRSVG {
    protected static final int FINDER_PATTERN_SIZE = 7; //finderPatternSize = 7;
    protected static final int QR_SIZE_BASE = 21;

    protected final String content;
    protected final int qrCodeVersion;
    protected ErrorCorrectionLevel qrCodErrorCorrectionLevel;
    protected final float shapeSizeRatio;
    protected final String fileOutputPath;
    protected final String onColour;
    protected final String offColour;
    protected final int quietZoneSize;
    protected final String fillShape;
    protected final int scaling;
    protected final int qrCodeSize;
    
    /**
     * Get QR Code specs from application.properties.
     */
    protected PrintQRSVG() {
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
        // generate SVG format QR Code (square)
        svgText.append(renderQRImage(code));
        svgText.append("</svg>");
        Files.writeString(Paths.get(fileOutputPath + qrCodeVersion + "-" + "x" + String.valueOf(scaling) + "-" + fillShape + ".svg"), svgText);
    }

    protected abstract StringBuilder renderQRImage(QRCode code);
}
