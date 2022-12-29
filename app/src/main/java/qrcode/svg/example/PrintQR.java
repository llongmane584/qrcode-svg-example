package qrcode.svg.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class PrintQR {
    private final String content;
    private final int qrCodeVersion;
    private ErrorCorrectionLevel qrCodErrorCorrectionLevel;
    private final String fileOutputPath;
    private final int canvasSize;
    private final int quietZoneSize;
    private final String outputFileName;

    /**
     * Get QR Code specs from application.properties.
     */
    public PrintQR() {
        ResourceBundle props = ResourceBundle.getBundle("application");
        this.outputFileName = props.getString("qrcode.version") + "-" + props.getString("qrcode.canvasSize") + ".png";
        this.quietZoneSize = Integer.parseInt(props.getString("qrcode.quietZoneSize"));
        this.content = props.getString("qrcode.content");
        this.qrCodeVersion =  Integer.parseInt(props.getString("qrcode.version"));
        this.qrCodErrorCorrectionLevel = ErrorCorrectionLevel.valueOf(props.getString("qrcode.errorCorrectionLevel"));
        this.fileOutputPath = props.getString("qrcode.fileOutputPath");
        this.canvasSize = Integer.parseInt(props.getString("qrcode.canvasSize"));
    }

    /**
     * Generate a PNG format QR Code file.
     * @throws Exception
     */
    public void write() throws Exception {
        final ConcurrentHashMap<EncodeHintType, Object> hints = new ConcurrentHashMap<>();
        hints.put(EncodeHintType.QR_VERSION, qrCodeVersion);
        hints.put(EncodeHintType.ERROR_CORRECTION, qrCodErrorCorrectionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.MARGIN, quietZoneSize);

        QRCodeWriter qw = new QRCodeWriter();
        BitMatrix bm = qw.encode(content, BarcodeFormat.QR_CODE, canvasSize, canvasSize, hints);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(bm);
        ImageIO.write(img, "png", new File(fileOutputPath + outputFileName));

        
    }
}
