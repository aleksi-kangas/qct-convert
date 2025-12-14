package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.reader.DynamicByteBuffer;

import java.util.ArrayList;
import java.util.List;

public final class HuffmanCodeBook {
  private final List<Integer> bytes = new ArrayList<>(256);

  private int pointer = 0;

  public HuffmanCodeBook(final DynamicByteBuffer dynamicByteBuffer) throws QctDecoderException {
    int colorCount = 0;
    int branchCount = 0;
    while (colorCount <= branchCount) {
      bytes.add(dynamicByteBuffer.nextByte());
      if (isFarBranch(size() - 1)) {
        bytes.add(dynamicByteBuffer.nextByte());
        bytes.add(dynamicByteBuffer.nextByte());
        ++branchCount;
      } else if (isNearBranch(size() - 1)) {
        ++branchCount;
      } else {
        ++colorCount;
      }
    }
    if (!isValid()) {
      throw new QctDecoderException("Invalid Huffman Code Book");
    }
  }

  int size() {
    return bytes.size();
  }

  boolean isColor() {
    return isColor(pointer);
  }

  boolean isColor(final int node) {
    return bytes.get(node) < 128;
  }

  boolean isFarBranch() {
    return isFarBranch(pointer);
  }

  boolean isFarBranch(final int node) {
    return bytes.get(node) == 128;
  }

  boolean isNearBranch() {
    return isNearBranch(pointer);
  }

  boolean isNearBranch(final int node) {
    return bytes.get(node) > 128;
  }

  QctPixel getColor(final Palette palette) {
    return getColor(pointer, palette);
  }

  QctPixel getColor(final int node, final Palette palette) {
    if (!isColor(node)) {
      throw new IllegalStateException("Attempting to get color in a non-color node");
    }
    final int paletteIndex = bytes.get(node);
    return new QctPixel(palette.getColor(paletteIndex), paletteIndex);
  }

  void step(final boolean bit) {
    if (bit) {  // Right -> Jump
      if (isNearBranch()) {
        pointer += nearBranchJumpSize(pointer);
      } else if (isFarBranch()) {
        pointer += farBranchJumpSize(pointer);
      } else {
        throw new IllegalStateException("Attempting to step in a non-branch node");
      }
    } else {
      if (isNearBranch()) {
        ++pointer;
      } else if (isFarBranch()) {
        pointer += 3;
      } else {
        throw new IllegalStateException("Attempting to step in a non-branch node");
      }
    }
  }

  void reset() {
    pointer = 0;
  }

  private int farBranchJumpSize(final int node) {
    return 65537 - (256 * bytes.get(node + 2) + bytes.get(node + 1)) + 2;
  }

  private int nearBranchJumpSize(final int node) {
    return 257 - bytes.get(node);
  }

  private boolean isValid() {
    if (size() == 0) return false;
    if (size() == 1) return true;
    int i = 0;
    while (i < size()) {
      if (isColor(i)) continue;
      if (isFarBranch(i)) {
        if (i + 2 >= size() || i + farBranchJumpSize(i) >= size()) return false;
        i += 2;
      } else if (isNearBranch(i)) {
        if (i + nearBranchJumpSize(i) >= size()) return false;
        ++i;
      } else {
        return false;
      }
    }
    return true;
  }
}
