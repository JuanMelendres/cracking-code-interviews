// Generates a minimal, valid, solid-color PNG with real, correct pixel
// dimensions -- no external deps, just Node's built-in zlib -- so
// next/image's static-import dimension inference has real data to read.
// Usage: node scripts/make-png.mjs <outPath> <width> <height> <r> <g> <b>
import { writeFileSync } from "node:fs";
import { deflateSync } from "node:zlib";

const [, , outPath, widthStr, heightStr, rStr, gStr, bStr] = process.argv;
const width = Number(widthStr);
const height = Number(heightStr);
const [r, g, b] = [Number(rStr), Number(gStr), Number(bStr)];

function crc32(buf) {
  let c;
  const table = crc32.table || (crc32.table = (() => {
    const t = [];
    for (let n = 0; n < 256; n++) {
      c = n;
      for (let k = 0; k < 8; k++) c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
      t[n] = c;
    }
    return t;
  })());
  let crc = 0xffffffff;
  for (let i = 0; i < buf.length; i++) crc = table[(crc ^ buf[i]) & 0xff] ^ (crc >>> 8);
  return (crc ^ 0xffffffff) >>> 0;
}

function chunk(type, data) {
  const typeBuf = Buffer.from(type, "ascii");
  const lenBuf = Buffer.alloc(4);
  lenBuf.writeUInt32BE(data.length, 0);
  const crcBuf = Buffer.alloc(4);
  crcBuf.writeUInt32BE(crc32(Buffer.concat([typeBuf, data])), 0);
  return Buffer.concat([lenBuf, typeBuf, data, crcBuf]);
}

const signature = Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]);

const ihdr = Buffer.alloc(13);
ihdr.writeUInt32BE(width, 0);
ihdr.writeUInt32BE(height, 4);
ihdr[8] = 8; // bit depth
ihdr[9] = 2; // color type: RGB
ihdr[10] = 0;
ihdr[11] = 0;
ihdr[12] = 0;

const rawRow = Buffer.alloc(1 + width * 3);
for (let x = 0; x < width; x++) {
  rawRow[1 + x * 3] = r;
  rawRow[1 + x * 3 + 1] = g;
  rawRow[1 + x * 3 + 2] = b;
}
const raw = Buffer.concat(Array(height).fill(rawRow));
const idatData = deflateSync(raw);

const png = Buffer.concat([
  signature,
  chunk("IHDR", ihdr),
  chunk("IDAT", idatData),
  chunk("IEND", Buffer.alloc(0)),
]);

writeFileSync(outPath, png);
console.log(`Wrote ${outPath}: ${width}x${height}, rgb(${r},${g},${b})`);
