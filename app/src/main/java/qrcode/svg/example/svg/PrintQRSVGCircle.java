package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGCircle{
    private final PrintQRSVG printQrSvg;
    public PrintQRSVGCircle() {
        printQrSvg = new PrintQRSVG();
    }

    /**
     * Convert QR Code squares to squares, then generate SVG text of QR Code.
     * 
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    public StringBuilder renderQRImage(QRCode code) {
        final String CELL = "<circle cx='$x' cy='$y' r='$r' stroke='rgb(" + printQrSvg.getOnColour() + ")' fill='rgb(" + printQrSvg.getOnColour() + ")' stroke-width='0' />";

        int padding = printQrSvg.getQuietZoneSize() * printQrSvg.getScaling(); // quietZoneSize * scaling;
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
        int finderRightTopXFrom = printQrSvg.getQrCodeSize() - PrintQRSVG.FINDER_PATTERN_SIZE;
        int finderRightTopYFrom = 0;
        int finderRightTopXTo = printQrSvg.getQrCodeSize() - 1;
        int finderRightTopYTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        // Left-Bottom
        int finderLeftBottomXFrom = 0;
        int finderLeftBottomYFrom = printQrSvg.getQrCodeSize() - PrintQRSVG.FINDER_PATTERN_SIZE;
        int finderLeftBottomXTo = PrintQRSVG.FINDER_PATTERN_SIZE - 1;
        int finderLeftBottomYTo = printQrSvg.getQrCodeSize() - 1;

        int fillSize = Math.round(printQrSvg.getScaling() * printQrSvg.getShapeSizeRatio() / 2);
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
                            .replace("$x", String.valueOf(x * printQrSvg.getScaling() + padding))
                            .replace("$y", String.valueOf(y * printQrSvg.getScaling() + padding))
                            .replace("$r", String.valueOf(fillSize)));
                        cells.append("\n");
                    }
                }
            }
        }

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append(drawFinderPattern(padding + (finderLeftTopXFrom + 3) * printQrSvg.getScaling(), padding + (finderLeftTopYFrom + 3) * printQrSvg.getScaling()));
        qrSvg.append(drawFinderPattern(padding + (finderRightTopXFrom + 3) * printQrSvg.getScaling(), padding + (finderRightTopYFrom + 3) * printQrSvg.getScaling()));
        qrSvg.append(drawFinderPattern(padding + (finderLeftBottomXFrom + 3) * printQrSvg.getScaling(), padding + (finderLeftBottomYFrom + 3) * printQrSvg.getScaling()));
        return qrSvg.append(cells);
    }

    private StringBuilder drawFinderPattern(int x, int y) {
        final String OUTER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.getOnColour() + ")' />";
        final String INNER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.getOffColour() + ")' />";
        final String DOT = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + printQrSvg.getOnColour() + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer circle
        finderPatterns.append(OUTER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(PrintQRSVG.FINDER_PATTERN_SIZE * printQrSvg.getScaling() / 2)) + "\n"
            );
        // draw inner circle
        finderPatterns.append(INNER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(5 * printQrSvg.getScaling() / 2)) + "\n"
            );
        // draw dot
        finderPatterns.append(DOT
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(3 * printQrSvg.getScaling() / 2)) + "\n"
            );

        return finderPatterns;
    }

    public void write(){
        
    }
}
