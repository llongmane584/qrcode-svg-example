package qrcode.svg.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVG {
    // this output is optimised to 600 (actually, this is an excuse, though :))
    private static final int CANVAS_SIZE = 600;

    private final String content;
    private final int qrCodeVersion;
    private ErrorCorrectionLevel qrCodErrorCorrectionLevel;
    private final float shapeSizeRatio;
    private final String fileOutputPath;
    private final String onColour;
    private final String offColour;
    private final int finderPatternSize;
    private final int quietZoneSize;

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
        this.finderPatternSize = Integer.parseInt(props.getString("qrcode.finderPatternSize"));
        this.quietZoneSize = Integer.parseInt(props.getString("qrcode.quietZoneSize"));
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
        // generate SVG format QR Code (circle)
        svgText.append(renderSquareQRImage(code));
        svgText.append("</svg>");
        Files.writeString(Paths.get(fileOutputPath + qrCodeVersion + "-" + CANVAS_SIZE + "-square.svg"), svgText);

        svgText = new StringBuilder();
        svgText.append("<?xml version ='1.0'?>" + System.lineSeparator());
        svgText.append("<svg xmlns='http://www.w3.org/2000/svg'>" + System.lineSeparator());
        // generate SVG format QR Code (square)
        svgText.append(renderCircleQRImage(code));
        svgText.append("</svg>");
        Files.writeString(Paths.get(fileOutputPath + qrCodeVersion + "-" + CANVAS_SIZE + "-circle.svg"), svgText);
    }

    /**
     * Convert QR Code squares to circles, then generate SVG text of QR Code.
     * 
     * Original source is published by Curtis Yallop in Stack Overflow.
     * Title: "Generate QR codes with custom dot shapes using zxing"
     * https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    private StringBuilder renderCircleQRImage(QRCode code) {

        final String DOT = "<circle cx='$x' cy='$y' r='$r' stroke='rgb(" + onColour + ")' fill='rgb(" + onColour + ")' stroke-width='0' />";

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append("<rect id='background' x='0' y='0' width='" + String.valueOf(CANVAS_SIZE) + "' height='" + String.valueOf(CANVAS_SIZE) + "' stroke='rgb(" + offColour + ")' fill='rgb(" + offColour + ")' stroke-width='2' />");
        qrSvg.append(System.lineSeparator());

        ByteMatrix qrByteMatrix = code.getMatrix();
        if (qrByteMatrix == null) {
            throw new IllegalStateException();
        }

        int qrMatrixSize = qrByteMatrix.getHeight();
        int qrSize = qrMatrixSize + (quietZoneSize);
        int outputSize = Math.max(CANVAS_SIZE, qrSize);

        int multiple = Math.round(outputSize / qrSize);
        int padding = Math.round((outputSize - (qrMatrixSize * multiple)) / 2);
        
        int circleRadius = Math.round(multiple * shapeSizeRatio / 2);

        StringBuilder dots = new StringBuilder();
        for (int inputY = 0, outputY = padding; inputY < qrMatrixSize; inputY++, outputY += multiple) {
            for (int inputX = 0, outputX = padding; inputX < qrMatrixSize; inputX++, outputX += multiple) {
                if (qrByteMatrix.get(inputX, inputY) == 1) {
                    if (!(inputX <= finderPatternSize && inputY <= finderPatternSize ||
                        inputX >= qrMatrixSize - finderPatternSize && inputY <= finderPatternSize ||
                        inputX <= finderPatternSize && inputY >= qrMatrixSize - finderPatternSize)) {
                            dots.append(DOT
                                .replace("$x", String.valueOf(outputX + quietZoneSize * 1.7))
                                .replace("$y", String.valueOf(outputY + quietZoneSize * 1.7))
                                .replace("$r", String.valueOf(circleRadius)));
                            dots.append(System.lineSeparator());
                    }
                }
            }
        }
        qrSvg.append(dots);

        // draw finder pattern circles (circle + dot)
        int circleDiameter = Math.round(multiple * finderPatternSize);
        // top-left
        int renderingArea = CANVAS_SIZE - padding * 2;
        int x = Math.round(padding + circleDiameter / 2);
        int y = Math.round(padding + circleDiameter / 2);


        StringBuilder finderPatterns = new StringBuilder();        
        finderPatterns.append(drawFinderPatternCircleStyle(x, y, circleDiameter));

        // top-right
        x = Math.round(padding + renderingArea - circleDiameter / 2);
        finderPatterns.append(drawFinderPatternCircleStyle(x, y, circleDiameter));
        
        // bottom-left
        x = Math.round(padding + circleDiameter / 2);
        y = Math.round(padding + renderingArea - circleDiameter / 2);
        finderPatterns.append(drawFinderPatternCircleStyle(x, y, circleDiameter));

        qrSvg.append(finderPatterns);
        qrSvg.append(System.lineSeparator());
        
        return qrSvg;
    }

    /**
     * draw QR Code Finder Patterns
     * 
     * Original source is published by Curtis Yallop in Stack Overflow.
     * Title: "Generate QR codes with custom dot shapes using zxing"
     * https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing
     * @param x
     * @param y
     * @param circleDiameter
     * @return
     */
    private StringBuilder drawFinderPatternCircleStyle(int x, int y, int circleDiameter) {
        final int finderInnerCircleDiameter = circleDiameter * 5 / finderPatternSize;
        final int finderDotDiameter = circleDiameter * 3 / finderPatternSize;
        final String OUTER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";
        final String INNER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + offColour + ")' />";
        final String DOT = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer circle
        finderPatterns.append(OUTER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(circleDiameter / 2)) + System.lineSeparator()
            );
        // draw inner circle
        finderPatterns.append(INNER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(finderInnerCircleDiameter / 2)) + System.lineSeparator()
            );
        // draw dot
        finderPatterns.append(DOT
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(finderDotDiameter / 2)) + System.lineSeparator()
            );

        return finderPatterns;
    }

    /**
     * Convert QR Code squares to squares, then generate SVG text of QR Code.
     * 
     * Original source is published by Curtis Yallop in Stack Overflow.
     * Title: "Generate QR codes with custom dot shapes using zxing"
     * https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing
     *
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    private StringBuilder renderSquareQRImage(QRCode code) {

        final String CELL = "<rect x='$x' y='$y' width='$r' height='$r' stroke='rgb(" + onColour + ")' fill='rgb(" + onColour + ")' stroke-width='0' />";

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append("<rect id='background' x='0' y='0' width='" + String.valueOf(CANVAS_SIZE) + "' height='" + String.valueOf(CANVAS_SIZE) + "' stroke='rgb(" + offColour + ")' fill='rgb(" + offColour + ")' stroke-width='2' />");
        qrSvg.append(System.lineSeparator());

        ByteMatrix qrByteMatrix = code.getMatrix();
        if (qrByteMatrix == null) {
            throw new IllegalStateException();
        }

        int qrMatrixSize = qrByteMatrix.getHeight();
        int qrSize = qrMatrixSize + (quietZoneSize);
        int outputSize = Math.max(CANVAS_SIZE, qrSize);

        int multiple = outputSize / qrSize;
        int padding = (outputSize - (qrMatrixSize * multiple)) / 2;

        int sideSize = Math.round(multiple * shapeSizeRatio);

        StringBuilder cells = new StringBuilder();
        for (int inputY = 0, outputY = padding; inputY < qrMatrixSize; inputY++, outputY += multiple) {
            for (int inputX = 0, outputX = padding; inputX < qrMatrixSize; inputX++, outputX += multiple) {
                if (qrByteMatrix.get(inputX, inputY) == 1) {
                    if (!(inputX <= finderPatternSize && inputY <= finderPatternSize ||
                        inputX >= qrMatrixSize - finderPatternSize && inputY <= finderPatternSize ||
                        inputX <= finderPatternSize && inputY >= qrMatrixSize - finderPatternSize)) {
                            cells.append(CELL
                                .replace("$x", String.valueOf(outputX))
                                .replace("$y", String.valueOf(outputY))
                                .replace("$r", String.valueOf(sideSize)));
                                cells.append(System.lineSeparator());
                    }
                }
            }
        }
        qrSvg.append(cells);

        // draw finder pattern squares
        int squareSideSize = multiple * finderPatternSize;
        // top-left
        int renderingArea = CANVAS_SIZE - padding * 2;
        int x = padding;
        int y = padding;
        StringBuilder finderPatterns = new StringBuilder();
        finderPatterns.append(drawFinderPatternSquareStyle(x, y, squareSideSize));

        // top-right
        x = padding + renderingArea - squareSideSize;
        finderPatterns.append(drawFinderPatternSquareStyle(x, y, squareSideSize));

        // bottom-left
        x = padding;
        y = padding + renderingArea - squareSideSize;
        finderPatterns.append(drawFinderPatternSquareStyle(x, y, squareSideSize));

        qrSvg.append(finderPatterns);
        qrSvg.append(System.lineSeparator());

        return qrSvg;
    }

    /**
     * draw QR Code Finder Patterns
     * 
     * Original source is published by Curtis Yallop in Stack Overflow.
     * Title: "Generate QR codes with custom dot shapes using zxing"
     * https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing
     * @param x
     * @param y
     * @param sideSize
     * @return
     */
    private StringBuilder drawFinderPatternSquareStyle(int x, int y, int sideSize) {
        final int finderMiddleSquareWidth = sideSize * 5 / finderPatternSize;
        final int finderInnerSquareWidth = sideSize * 3 / finderPatternSize;
        final String OUTER_SQUARE = "<rect x='$x' y='$y' width='$r' height='$r' fill='rgb(" + onColour + ")' />";
        final String MIDDLE_SQUARE = "<rect x='$x' y='$y' width='$r' height='$r' fill='rgb(" + offColour + ")' />";
        final String INNER_SQUARE = "<rect x='$x' y='$y' width='$r' height='$r' fill='rgb(" + onColour + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer square
        finderPatterns.append(OUTER_SQUARE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(sideSize)) + System.lineSeparator()
            );
        // draw middle square
        finderPatterns.append(MIDDLE_SQUARE
            .replace("$x", String.valueOf(x + (sideSize / 7)))
            .replace("$y", String.valueOf(y + (sideSize / 7)))
            .replace("$r", String.valueOf(finderMiddleSquareWidth)) + System.lineSeparator()
            );
        // draw inner square
        finderPatterns.append(INNER_SQUARE
            .replace("$x", String.valueOf((x + (sideSize * 2 / 7))))
            .replace("$y", String.valueOf((y + (sideSize * 2 / 7))))
            .replace("$r", String.valueOf(finderInnerSquareWidth)) + System.lineSeparator()
            );

        return finderPatterns;
    }
}
