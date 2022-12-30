package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGCircle2 extends PrintQRSVG {
    /**
     * Convert QR Code squares to squares, then generate SVG text of QR Code.
     * 
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    @Override
    public StringBuilder renderQRImage(QRCode code) {
        final String CELL = "<circle cx='$x' cy='$y' r='$r' stroke='rgb(" + onColour + ")' fill='rgb(" + onColour + ")' stroke-width='0' />";

        int padding = quietZoneSize * scaling;
        int canvasSize = (QR_SIZE_BASE + (qrCodeVersion - 1) * 4) * scaling + padding * 2;

        ByteMatrix qrByteMatrix = code.getMatrix();
        if (qrByteMatrix == null) {
            throw new IllegalStateException();
        }

        // finder pattern position
        // Left-Top
        int finderLeftTopXFrom = 0;
        int finderLeftTopYFrom = 0;
        int finderLeftTopXTo = FINDER_PATTERN_SIZE - 1;
        int finderLeftTopYTo = FINDER_PATTERN_SIZE - 1;
        // Right-Top
        int finderRightTopXFrom = qrCodeSize - FINDER_PATTERN_SIZE;
        int finderRightTopYFrom = 0;
        int finderRightTopXTo = qrCodeSize - 1;
        int finderRightTopYTo = FINDER_PATTERN_SIZE - 1;
        // Left-Bottom
        int finderLeftBottomXFrom = 0;
        int finderLeftBottomYFrom = qrCodeSize - FINDER_PATTERN_SIZE;
        int finderLeftBottomXTo = FINDER_PATTERN_SIZE - 1;
        int finderLeftBottomYTo = qrCodeSize - 1;

        int fillSize = Math.round(scaling * shapeSizeRatio / 2);
        StringBuilder cells = new StringBuilder();
        int qrMatrixSize = qrByteMatrix.getWidth();
        for(int y=0; y<qrMatrixSize; ++y) {
            for(int x=0; x<qrMatrixSize; ++x) {
                // fill a cell when the cell colour is on
                if(qrByteMatrix.get(x, y) == 1) {
                    // finder pattern cells
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
                            .replace("$x", String.valueOf(x * scaling + padding))
                            .replace("$y", String.valueOf(y * scaling + padding))
                            .replace("$r", String.valueOf(fillSize)));
                    }
                    cells.append("\n");
                }
            }
        }

        StringBuilder qrSvg = new StringBuilder();
        qrSvg.append("<rect id='background' x='0' y='0' width='" + String.valueOf(canvasSize) + "' height='" + String.valueOf(canvasSize) + "' stroke='rgb(" + offColour + ")' fill='rgb(" + offColour + ")' stroke-width='2' />");
        qrSvg.append("\n");
        qrSvg.append(drawFinderPattern(padding + (finderLeftTopXFrom + 3) * scaling, padding + (finderLeftTopYFrom + 3) * scaling));
        qrSvg.append(drawFinderPattern(padding + (finderRightTopXFrom + 3) * scaling, padding + (finderRightTopYFrom + 3) * scaling));
        qrSvg.append(drawFinderPattern(padding + (finderLeftBottomXFrom + 3) * scaling, padding + (finderLeftBottomYFrom + 3) * scaling));
        return qrSvg.append(cells);
    }

    private StringBuilder drawFinderPattern(int x, int y) {
        final String OUTER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";
        final String INNER_CIRCLE = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + offColour + ")' />";
        final String DOT = "<circle cx='$x' cy='$y' r='$r' fill='rgb(" + onColour + ")' />";

        StringBuilder finderPatterns =  new StringBuilder();
        // draw outer circle
        finderPatterns.append(OUTER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(FINDER_PATTERN_SIZE * scaling / 2)) + "\n"
            );
        // draw inner circle
        finderPatterns.append(INNER_CIRCLE
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(5 * scaling / 2)) + "\n"
            );
        // draw dot
        finderPatterns.append(DOT
            .replace("$x", String.valueOf(x))
            .replace("$y", String.valueOf(y))
            .replace("$r", String.valueOf(3 * scaling / 2)) + "\n"
            );

        return finderPatterns;
    }
}
