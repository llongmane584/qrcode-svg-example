package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGCircle{
    private final PrintQRSVG printQrSvg;
    public PrintQRSVGCircle() {
        printQrSvg = new PrintQRSVG(getClass().getSimpleName().replace(getClass().getSuperclass().getSimpleName(), "").toLowerCase());
    }

    /**
     * Convert QR Code squares to squares, then generate SVG text of QR Code.
     * 
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    private StringBuilder renderQRImage(QRCode code) {
        final String CELL = "<circle cx='$x' cy='$y' r='$r' stroke='rgb(" + printQrSvg.onColour + ")' fill='rgb(" + printQrSvg.onColour + ")' stroke-width='0' />";

        int padding = printQrSvg.quietZoneSize * printQrSvg.scaling; // quietZoneSize * scaling;
        // fine adjustment for circle QR code rendering
        padding *= 1.1;

        ByteMatrix qrByteMatrix = code.getMatrix();
        if (qrByteMatrix == null) {
            throw new IllegalStateException();
        }

        // finder pattern position
        // Left-Top
        int finderLeftTopXFrom = 0;
        int finderLeftTopYFrom = 0;
        int finderLeftTopXTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        int finderLeftTopYTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        // Right-Top
        int finderRightTopXFrom = printQrSvg.qrCodeSize - PrintQRSVG.FINDER_PATTERN_SIZE;
        int finderRightTopYFrom = 0;
        int finderRightTopXTo = printQrSvg.qrCodeSize - 1;
        int finderRightTopYTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        // Left-Bottom
        int finderLeftBottomXFrom = 0;
        int finderLeftBottomYFrom = printQrSvg.qrCodeSize - PrintQRSVG.FINDER_PATTERN_SIZE;
        int finderLeftBottomXTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        int finderLeftBottomYTo = printQrSvg.qrCodeSize - 1;

        int fillSize = Math.round(printQrSvg.scaling * printQrSvg.shapeSizeRatio / 2);
        StringBuilder cells = new StringBuilder();
        int qrMatrixSize = qrByteMatrix.getWidth();
        for(int y=0; y<qrMatrixSize; ++y) {
            for(int x=0; x<qrMatrixSize; ++x) {
                // fill a cell when the cell colour is on
                if(qrByteMatrix.get(x, y) == 1) {
                    // skip rendering finder pattern cells here
                    if(!(
                        ((y >= finderLeftTopYFrom && y <= finderLeftTopYTo)
                        && (x >= finderLeftTopXFrom && x <= finderLeftTopXTo))
                        ||
                        ((y >= finderRightTopYFrom && y <= finderRightTopYTo)
                        && (x >= finderRightTopXFrom && x <= finderRightTopXTo))
                        ||
                        ((y >= finderLeftBottomYFrom && y <= finderLeftBottomYTo)
                            && (x >= finderLeftBottomXFrom && x <= finderLeftBottomXTo))                      
                    )) {
                        cells.append(CELL
                            .replace("$x", String.valueOf(x * printQrSvg.scaling + padding))
                            .replace("$y", String.valueOf(y * printQrSvg.scaling + padding))
                            .replace("$r", String.valueOf(fillSize)));
                        cells.append("\n");
                    }
                }
            }
        }

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append(drawFinderPattern(padding + (finderLeftTopXFrom + 3) * printQrSvg.scaling, padding + (finderLeftTopYFrom + 3) * printQrSvg.scaling));
        qrSvg.append(drawFinderPattern(padding + (finderRightTopXFrom + 3) * printQrSvg.scaling, padding + (finderRightTopYFrom + 3) * printQrSvg.scaling));
        qrSvg.append(drawFinderPattern(padding + (finderLeftBottomXFrom + 3) * printQrSvg.scaling, padding + (finderLeftBottomYFrom + 3) * printQrSvg.scaling));
        return qrSvg.append(cells);
    }

    /**
     * convert finder patterns (square to circle)
     * @param x
     * @param y
     * @return
     */
    private StringBuilder drawFinderPattern(int x, int y) {
        final String OUTER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.onColour + ")' />";
        final String MIDDLE_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.offColour + ")' />";
        final String INNER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.onColour + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer circle
        finderPatterns.append(OUTER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(PrintQRSVG.FINDER_PATTERN_SIZE * printQrSvg.scaling / 2)) + "\n"
            );
        // draw inner circle
        finderPatterns.append(MIDDLE_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(5 * printQrSvg.scaling / 2)) + "\n"
            );
        // draw dot
        finderPatterns.append(INNER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(3 * printQrSvg.scaling / 2)) + "\n"
            );

        return finderPatterns;
    }

    /**
     * 
     * @throws Exception
     */
    public void write() throws Exception {
        printQrSvg.write(this::renderQRImage);
    }
}
