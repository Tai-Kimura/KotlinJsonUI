#!/usr/bin/env python3
"""Does a rebooted emulator still draw a blur layer? (run_conformance.sh 2c)

Reads one capture of common/effectStyle__light — `adb exec-out screencap`
(raw RGBA with a 12- or 16-byte header) or a PNG — and the display density,
and answers whether the 200dp `target` (a Light scrim over a striped backdrop)
shows the stripes through it.

Measured 2026-11-13 (run 43379878694, codegen job): after an a11y re-roll
reboot that the emulator answered with "DisplaySurfaceGlContextHelper context
was preempted", the whole target drew as a flat #DDDDDD — the blur layer not
drawn at all — and that single picture was the run's only codegen-parity
deviation. A healthy boot draws the stripes, 40% white over them.

Rows 80..180dp below the top of the screen, columns 50..190dp: inside the
target whatever the status bar's height (the content is systemBarsPadding'd,
and a status bar of up to 60dp keeps every row inside the 200dp target), and
clear of the 40dp boxes at its top-left. A healthy row crosses several stripes
(white, blue, green and red under the scrim); a flat one is one colour.

Exit 0 drawn, 1 flat, 2 not measured (unreadable capture) — the caller names
the last one rather than taking it as either.
"""
from __future__ import annotations

import struct
import sys
import zlib


def _png_rgba(data: bytes):
    """Decode a non-interlaced 8-bit RGB/RGBA PNG (what screencap -p writes)."""
    pos, chunks = 8, []
    width = height = color = None
    while pos < len(data):
        length, kind = struct.unpack(">I4s", data[pos:pos + 8])
        body = data[pos + 8:pos + 8 + length]
        if kind == b"IHDR":
            width, height, depth, color = struct.unpack(">IIBB", body[:10])
            if depth != 8 or color not in (2, 6):
                raise ValueError("unsupported PNG")
        elif kind == b"IDAT":
            chunks.append(body)
        pos += 12 + length
    bpp = 4 if color == 6 else 3
    raw = zlib.decompress(b"".join(chunks))
    stride = width * bpp
    out, prev = bytearray(), bytearray(stride)
    for y in range(height):
        f = raw[y * (stride + 1)]
        line = bytearray(raw[y * (stride + 1) + 1:(y + 1) * (stride + 1)])
        for i in range(stride):
            a = line[i - bpp] if i >= bpp else 0
            b = prev[i]
            c = prev[i - bpp] if i >= bpp else 0
            if f == 1:
                line[i] = (line[i] + a) & 255
            elif f == 2:
                line[i] = (line[i] + b) & 255
            elif f == 3:
                line[i] = (line[i] + (a + b) // 2) & 255
            elif f == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                line[i] = (line[i] + (a if pa <= pb and pa <= pc else b if pb <= pc else c)) & 255
        prev = line
        if bpp == 3:
            out += b"".join(bytes(line[i:i + 3]) + b"\xff" for i in range(0, stride, 3))
        else:
            out += line
    return width, height, bytes(out)


def read_capture(data: bytes):
    if data[:8] == b"\x89PNG\r\n\x1a\n":
        return _png_rgba(data)
    width, height = struct.unpack("<II", data[:8])
    for header in (16, 12):
        if len(data) - header == width * height * 4:
            return width, height, data[header:]
    raise ValueError("not a raw RGBA screencap")


def drawn(width: int, height: int, pixels: bytes, density: int) -> bool:
    scale = density / 160.0
    for y_dp in range(80, 181, 20):
        y = int(y_dp * scale)
        if y >= height:
            break
        colors = set()
        for x_dp in range(50, 191, 10):
            x = int(x_dp * scale)
            i = (y * width + x) * 4
            colors.add(pixels[i:i + 3])
        if len(colors) >= 4:
            return True
    return False


def main(argv) -> int:
    path, density = argv[1], int(argv[2])
    try:
        with open(path, "rb") as f:
            width, height, pixels = read_capture(f.read())
    except (OSError, ValueError, struct.error, zlib.error) as e:
        print(f"blur probe: capture unreadable ({e})", file=sys.stderr)
        return 2
    return 0 if drawn(width, height, pixels, density) else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))
