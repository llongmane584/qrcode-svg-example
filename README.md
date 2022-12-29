# ZXing QR Code generation example.
## Description
Generating QR Code PNG and SVG image by using ZXing.

## Classes
### App.java
The main entry point of this example project.

### PrintQR
Generate a PNG format QR Code image file.

### PrintQRSVG
Generate an SVG format quasi QR Code image file.
This changes shapes (from square to circle) of finder patterns and cells of QR Code.
The original source code about the QR circle conversion was published by Curtis Yallop on 2020-01-28.
See also the article on Stack Overflow:
https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing

## Usage
Run App.java.

## Requirements
ZXing, Java >= 17.
About the QR Code, please read [QR code.com articles](https://www.qrcode.com/en/index.html).
