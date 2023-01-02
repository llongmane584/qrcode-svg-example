package qrcode.svg.example.svg;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Write QR Code as an SVG format text file (.svg).
 */
public class PrintQRSVGSquare {
    private final PrintQRSVG printQrSvg;
    public PrintQRSVGSquare() {
        printQrSvg = new PrintQRSVG();
    }

    /**
     * Convert QR Code squares to squares, then generate SVG text of QR Code.
     * 
     * @param code source QR Code
     * @return dot converted SVG format QR Code text
     */
    public StringBuilder renderQRImage(QRCode code) {
        final String CELL = "<rect x='$x' y='$y' width='$r' height='$r' stroke='rgb(" + printQrSvg.getOnColour() + ")' fill='rgb(" + printQrSvg.getOnColour() + ")' stroke-width='0' />";

        int padding = printQrSvg.getQuietZoneSize() * printQrSvg.getScaling();
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

        int fillSize = Math.round(printQrSvg.getScaling() * printQrSvg.getShapeSizeRatio());
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
                        .replace("$x", String.valueOf(x * printQrSvg.getScaling() + padding))
                        .replace("$y", String.valueOf(y * printQrSvg.getScaling() + padding))
                        .replace("$r", String.valueOf(printQrSvg.getScaling())));
                    }
                    // other cells
                    else {
                        cells.append(CELL
                            .replace("$x", String.valueOf(x * printQrSvg.getScaling() + padding))
                            .replace("$y", String.valueOf(y * printQrSvg.getScaling() + padding))
                            .replace("$r", String.valueOf(fillSize)));
                    }
                    cells.append("\n");
                }
            }
        }

        return cells;
    }

    public void write(){
        
    }
}
