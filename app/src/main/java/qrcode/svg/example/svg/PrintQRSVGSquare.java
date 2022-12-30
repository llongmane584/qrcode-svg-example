package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGSquare extends PrintQRSVG {
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
    protected StringBuilder renderQRImage(QRCode code) {

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
        finderPatterns.append(drawFinderPattern(x, y, squareSideSize));

        // top-right
        x = padding + renderingArea - squareSideSize;
        finderPatterns.append(drawFinderPattern(x, y, squareSideSize));

        // bottom-left
        x = padding;
        y = padding + renderingArea - squareSideSize;
        finderPatterns.append(drawFinderPattern(x, y, squareSideSize));

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
    private StringBuilder drawFinderPattern(int x, int y, int sideSize) {
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
