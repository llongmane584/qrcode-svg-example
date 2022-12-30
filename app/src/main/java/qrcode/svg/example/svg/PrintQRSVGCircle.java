package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGCircle extends PrintQRSVG{
    /**
     * Convert QR Code squares to circles, then generate SVG text of QR Code.
     * 
     * Original source is published by Curtis Yallop in Stack Overflow.
     * Title: "Generate QR codes with custom dot shapes using zxing"
     * https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing
     *
     *  @param code source QR Code
     *  @return dot converted SVG format QR Code text
     */
    @Override
    protected StringBuilder renderQRImage(QRCode code) {

        final String DOT = "<circle cx='$x' cy='$y' r='$r' stroke='rgb(" + onColour + ")' fill='rgb(" + onColour + ")' stroke-width='0' />";

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append("<rect id='background' x='0' y='0' width='" + String.valueOf(CANVAS_SIZE) + "' height='" + String.valueOf(CANVAS_SIZE) + "' stroke='rgb(" + offColour + ")' fill='rgb(" + offColour + ")' stroke-width='2' />");
        qrSvg.append("\n");

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
                    if (!(inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                        inputX >= qrMatrixSize - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                        inputX <= FINDER_PATTERN_SIZE && inputY >= qrMatrixSize - FINDER_PATTERN_SIZE)) {
                            dots.append(DOT
                                .replace("$x", String.valueOf(outputX + quietZoneSize * 1.7))
                                .replace("$y", String.valueOf(outputY + quietZoneSize * 1.7))
                                .replace("$r", String.valueOf(circleRadius)));
                            dots.append("\n");
                    }
                }
            }
        }
        qrSvg.append(dots);

        // draw finder pattern circles (circle + dot)
        int circleDiameter = Math.round(multiple * FINDER_PATTERN_SIZE);
        // top-left
        int renderingArea = CANVAS_SIZE - padding * 2;
        int x = Math.round(padding + circleDiameter / 2);
        int y = Math.round(padding + circleDiameter / 2);

        StringBuilder finderPatterns = new StringBuilder();        
        finderPatterns.append(drawFinderPattern(x, y, circleDiameter));

        // top-right
        x = Math.round(padding + renderingArea - circleDiameter / 2);
        finderPatterns.append(drawFinderPattern(x, y, circleDiameter));
        
        // bottom-left
        x = Math.round(padding + circleDiameter / 2);
        y = Math.round(padding + renderingArea - circleDiameter / 2);
        finderPatterns.append(drawFinderPattern(x, y, circleDiameter));

        qrSvg.append(finderPatterns);
        qrSvg.append("\n");
        
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
    private StringBuilder drawFinderPattern(int x, int y, int circleDiameter) {
        final int finderInnerCircleDiameter = circleDiameter * 5 / FINDER_PATTERN_SIZE;
        final int finderDotDiameter = circleDiameter * 3 / FINDER_PATTERN_SIZE;
        final String OUTER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";
        final String INNER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + offColour + ")' />";
        final String DOT = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer circle
        finderPatterns.append(OUTER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(circleDiameter / 2)) + "\n"
            );
        // draw inner circle
        finderPatterns.append(INNER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(finderInnerCircleDiameter / 2)) + "\n"
            );
        // draw dot
        finderPatterns.append(DOT
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(finderDotDiameter / 2)) + "\n"
            );

        return finderPatterns;
    }
}
