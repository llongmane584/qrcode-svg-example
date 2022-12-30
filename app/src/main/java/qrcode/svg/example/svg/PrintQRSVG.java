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
abstract class PrintQRSVG {
    // this output is optimised to 600 (actually, this is an excuse, though :))
    protected static final int CANVAS_SIZE = 600;

    protected final String content;
    protected final int qrCodeVersion;
    protected ErrorCorrectionLevel qrCodErrorCorrectionLevel;
    protected final float shapeSizeRatio;
    protected final String fileOutputPath;
    protected final String onColour;
    protected final String offColour;
    protected final int finderPatternSize;
    protected final int quietZoneSize;
    protected final String fillShape;
    
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
        this.finderPatternSize = Integer.parseInt(props.getString("qrcode.finderPatternSize"));
        this.quietZoneSize = Integer.parseInt(props.getString("qrcode.quietZoneSize"));

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
        Files.writeString(Paths.get(fileOutputPath + qrCodeVersion + "-" + CANVAS_SIZE + "-" + fillShape + ".svg"), svgText);
    }

    protected abstract StringBuilder renderQRImage(QRCode code);
}
