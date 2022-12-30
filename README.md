# ZXing QR Code generation example.
## Description
Generating QR Code PNG and SVG image by using ZXing.

## Classes
### App
The main entry point of this example project.

### PrintQR
Generate a PNG format QR Code image file.

### PrintQRSVG
Generate an SVG format quasi QR Code image file.  
This changes shapes (from square to circle) of finder patterns and cells of QR Code. The output size is fixed to 600.  
The original source code about the QR circle conversion was published by Curtis Yallop on 2020-01-28.  
See also the post on Stack Overflow:  
[Generate QR codes with custom dot shapes using zxing](https://stackoverflow.com/questions/35419511/generate-qr-codes-with-custom-dot-shapes-using-zxing)

## Usage
Configure application.properties and run App.

## Requirements
ZXing, Java >= 17.
About the QR Code, please read [QR code.com articles](https://www.qrcode.com/en/index.html) as well.

## Example images
### version:4, error correction level:H, x3 scaling, square shape cell
![4-x3-square](https://user-images.githubusercontent.com/46396892/210091447-2ee8c7b7-36fe-418b-ba32-d225d5e7cdb7.svg)
### version:4, error correction level:H, x3 scaling, circle shape cell
![4-x3-circle](https://user-images.githubusercontent.com/46396892/210091406-4a697ca6-2315-4cf1-826e-8adab7001e29.svg)

