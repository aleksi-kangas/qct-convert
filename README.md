# QCT Convert

[![Build](https://github.com/aleksi-kangas/qct-convert/actions/workflows/workflow.yaml/badge.svg)](https://github.com/aleksi-kangas/qct-convert/actions/workflows/workflow.yaml)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/aleksi-kangas/qct-convert)

> A C++ implementation of the [(unofficial) Quick Chart (.QCT)
> File Format Specification (v1.03)](https://www.etheus.net/Quick_Chart_File_Format) offering decoding of `.qct` map
> files
> and export to various formats

![](map.png)
*A small sample of a `.png` file exported from a `.qct` file, depicting the famous Ben Nevis, the highest point in the
UK. All rights reserved by Ordnance Survey.*

## Motivation

This project was inspired by the desire to convert a few of [Ordnance Survey](https://www.ordnancesurvey.co.uk/) UK maps
that I have from the proprietary `.qct` format into more widely accessible formats for use in image viewers and GIS
software. It provides a multithreaded C++ implementation of
the [(unofficial) Quick Chart (.QCT) File Format Specification (v1.03)](https://www.etheus.net/Quick_Chart_File_Format)
by *Craig Shelley*.

## Features

- **QCT File Decoding**
    - [x] Multithreaded decoding
    - [x] Decoding of [Huffman-Coded](http://en.wikipedia.org/wiki/Huffman_coding) tiles
    - [x] Decoding of [Run-Length-Encoded (RLE)](http://en.wikipedia.org/wiki/Run-length_encoding) tiles
    - [ ] Decoding of *Pixel-Packed* tiles
        - *Note:* Not implemented, as I have yet to I have yet to come across any `.qct` file with _Pixel-Packed_ tiles.
          Feel free to open an issue with a sample `.qct` file for implementation and testing purposes.
- **Export Formats**
    - [x] Export map boundaries to [`.kml`](https://en.wikipedia.org/wiki/Keyhole_Markup_Language)
    - [x] Export map to `.png`using [fpng](https://github.com/richgel999/fpng)
    - [x] Export to GeoTIFF using [GDAL](https://www.gdal.org/)
- **Other**
    - [x] Precompiled binaries for Windows, Linux, and macOS
    - [ ] QCT3-file decoding
        - *Note:* QCT3 files are not supported, as they may include encrypted data for Digital Rights Management (DRM).
          I may add support for these in the future, for the unencrypted portion that is.

## Usage

The command-line interface (CLI) provides a straightforward way to decode `.qct` files and export them to supported
formats. A path to the `.qct` file is required. Optionally, one may export the `.qct` file to various formats. If no
export options are specified, the tool decodes the file and prints its metadata.

### CLI

- `<path/to/map.qct>`: Path to the input `.qct` file (required)
- `--force`: To attempt decoding anyways if the metadata is invalid or shows incompatible file

#### Export Formats

##### KML

- `--export-kml-path <path>`: Export map boundaries to a `.kml` file

##### GeoTIFF

- `--export-geotiff-path <path>`: Export georeferenced map to a `.tiff` (GeoTIFF) file
- `--geotiff-georef-method`: Specify the georeferencing method for GeoTIFF export:
    - `auto`: Automatically determine the most suitable georeferencing method. This is the default method.
    - `gcp`: Use GCPs (Ground Control Points) for georeferencing. This uses all the georeferencing coefficients (incl.
      higher order) from the `.qct` file, yielding a more accurate result, when the higher order coefficients are
      non-zero. Note, not all GIS software support this method and that
      additional [gdalwarp](https://gdal.org/en/stable/programs/gdalwarp.html) may be required for GIS software
      compatibility.
    - `linear`: [GDAL GeoTransform](https://gdal.org/en/stable/tutorials/geotransforms_tut.html) using constant and
      first order polynomial coefficients. If the higher order georeferencing coefficients in the `.qct` file are zero,
      this method is sufficient and recommended, as a wide range of GIS software support this method.

##### PNG

- `--export-png-path <path>`: Export map image to a `.png` file

On Windows:

```cmd
qct-convert.exe <path\to\map.qct> --export-kml-path <path\to\map.kml> --export-geotiff-path <path\to\map.tiff> --export-png-path <path\to\map.png> 
```

On Linux/macOS:

```bash
qct-convert <path/to/map.qct> --export-kml-path <path/to/map.kml> --export-geotiff-path <path/to/map.tiff> --export-png-path <path/to/map.png>
```

##### Acknowledgements

This project would not be possible without the incredible work of Craig Shelley and Mark Bryant on
the [(unofficial) Quick Chart (.QCT) File Format Specification v1.03](https://www.etheus.net/Quick_Chart_File_Format).
