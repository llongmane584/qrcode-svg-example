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
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    @Override
    public StringBuilder renderQRImage(QRCode code) {
        final String CELL = "<rect x='$x' y='$y' width='$r' height='$r' stroke='rgb(" + onColour + ")' fill='rgb(" + onColour + ")' stroke-width='0' />";

        int padding = quietZoneSize * scaling;
        int canvasSize = (qrCodeSize) * scaling + padding * 2;

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

        int fillSize = Math.round(scaling * shapeSizeRatio);
        StringBuilder cells = new StringBuilder();
        int qrMatrixSize = qrByteMatrix.getWidth();
        for(int y=0; y<qrMatrixSize; ++y) {
            for(int x=0; x<qrMatrixSize; ++x) {
                // fill a cell when the cell colour is on
                if(qrByteMatrix.get(x, y) == 1) {
                    // finder pattern cells (for preventing read error)
                    if(
                        ((y >= finderLeftTopYFrom && y <= finderLeftTopYTo)
                        && (x >= finderLeftTopXFrom && x <= finderLeftTopXTo))
                        ||
                        ((y >= finderRightTopYFrom && y <= finderRightTopYTo)
                        && (x >= finderRightTopXFrom && x <= finderRightTopXTo))
                        ||
                        ((y >= finderLeftBottomYFrom && y <= finderLeftBottomYTo)
                            && (x >= finderLeftBottomXFrom && x <= finderLeftBottomXTo))                      
                    ) {
                        cells.append(CELL
                        .replace("$x", String.valueOf(x * scaling + padding))
                        .replace("$y", String.valueOf(y * scaling + padding))
                        .replace("$r", String.valueOf(scaling)));
                    }
                    // other cells
                    else {
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
        return qrSvg.append(cells);
    }
}
