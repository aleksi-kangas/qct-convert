# QCT Convert

[![Build](https://github.com/aleksi-kangas/qct-convert/actions/workflows/workflow.yaml/badge.svg)](https://github.com/aleksi-kangas/qct-convert/actions/workflows/workflow.yaml)

> A C++ implementation of the [(unofficial) Quick Chart (.QCT)
> File Format Specification (v1.03)](https://www.etheus.net/Quick_Chart_File_Format) offering decoding of .QCT map files
> and export to various formats

![](map.png)
*A small sample of a `.PNG` file exported from a `.QCT` file, depicting the famous Ben Nevis, the highest point in the
UK.*

## Motivation

This project was inspired by the desire to convert a few of [Ordnance Survey](https://www.ordnancesurvey.co.uk/) UK maps
that I have from the proprietary `.QCT` format into more widely accessible formats for use in image viewers and GIS
software. It provides a multithreaded C++ implementation of
the [(unofficial) Quick Chart (.QCT) File Format Specification (v1.03)](https://www.etheus.net/Quick_Chart_File_Format)
by *Craig Shelley*.

## Features

- **QCT File Decoding**
    - [x] Multithreaded decoding
    - [x] Decoding of [Huffman-Coded](http://en.wikipedia.org/wiki/Huffman_coding) tiles
    - [x] Decoding of [Run-Length-Encoded (RLE)](http://en.wikipedia.org/wiki/Run-length_encoding) tiles
    - [ ] Decoding of *Pixel-Packed* tiles
        - *Note:* Not implemented, as I have yet to I have yet to come across any .qct file with _Pixel-Packed_ tiles.
- **Export Formats**
    - [x] Export map boundaries to [.kml](https://en.wikipedia.org/wiki/Keyhole_Markup_Language)
    - [x] Export map to [.PNG]() using [fpng](https://github.com/richgel999/fpng)
    - [ ] Export to GeoTIFF
- **Other**
    - [ ] Precompiled binaries
    - [ ] QCT3-file decoding
        - *Note:* QCT3 files are not supported, as they may include encrypted data for Digital Rights Management (DRM).
          I may add support for these in the future, for the unencrypted portion that is.

## Usage

The command-line interface (CLI) provides a straightforward way to decode `.QCT` files and export them to supported
formats. A path to the `.QCT` file is required. Optionally, one may export the `.QCT` file to various formats. If no
export options are specified, the tool decodes the file and prints its metadata.

### CLI

- `<path/to/map.qct>`: Path to the input `.QCT` file (required)
- `--export-kml-path <path>`: Export map boundaries to a .KML file
- `--export-png-path <path>`: Export map image to a .PNG file

```cmd
qct-convert.exe <path/to/map.qct> --export-kml-path <path/to/map.kml> --export-png-path <path/to/map.png> 
```

##### Acknowledgements

This project would not be possible without the incredible work of Craig Shelley and Mark Bryant on
the [(unofficial) Quick Chart (.QCT) File Format Specification v1.03](https://www.etheus.net/Quick_Chart_File_Format).
