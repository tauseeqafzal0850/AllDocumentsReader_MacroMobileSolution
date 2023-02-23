// Copyright 2001, FreeHEP.
package com.faisaldeveloper.office.thirdpart.emf;

/**
 * EMF Constants
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFConstants.java 10363 2007-01-20 15:30:50Z duns $
 */
public interface EMFConstants {

    int UNITS_PER_PIXEL = 1;

    int TWIPS = 20;

    int GRADIENT_FILL_RECT_H = 0x00000000;

    int GRADIENT_FILL_RECT_V = 0x00000001;

    int GRADIENT_FILL_TRIANGLE = 0x00000002;

    int SRCCOPY = 0x00CC0020;

    int ICM_OFF = 1;

    int ICM_ON = 2;

    int ICM_QUERY = 3;

    int ICM_DONE_OUTSIDEDC = 4;

    int FW_DONTCARE = 0;

    int FW_THIN = 100;

    int FW_EXTRALIGHT = 200;

    int FW_LIGHT = 300;

    int FW_NORMAL = 400;

    int FW_MEDIUM = 500;

    int FW_SEMIBOLD = 600;

    int FW_BOLD = 700;

    int FW_EXTRABOLD = 800;

    int FW_HEAVY = 900;

    int PAN_ANY = 0;

    int PAN_NO_FIT = 1;

    int ETO_OPAQUE = 0x0002;

    int ETO_CLIPPED = 0x0004;

    int ETO_GLYPH_INDEX = 0x0010;

    int ETO_RTLREADING = 0x0080;

    int ETO_NUMERICSLOCAL = 0x0400;

    int ETO_NUMERICSLATIN = 0x0800;

    int ETO_IGNORELANGUAGE = 0x1000;

    int ETO_PDY = 0x2000;

    int GM_COMPATIBLE = 1;

    int GM_ADVANCED = 2;

    int FLOODFILLBORDER = 0;

    int FLOODFILLSURFACE = 1;

    int BLACKONWHITE = 1;

    int WHITEONBLACK = 2;

    int COLORONCOLOR = 3;

    int HALFTONE = 4;

    int STRETCH_ANDSCANS = BLACKONWHITE;

    int STRETCH_ORSCANS = WHITEONBLACK;

    int STRETCH_DELETESCANS = COLORONCOLOR;

    int STRETCH_HALFTONE = HALFTONE;

    int R2_BLACK = 1;

    int R2_NOTMERGEPEN = 2;

    int R2_MASKNOTPEN = 3;

    int R2_NOTCOPYPEN = 4;

    int R2_MASKPENNOT = 5;

    int R2_NOT = 6;

    int R2_XORPEN = 7;

    int R2_NOTMASKPEN = 8;

    int R2_MASKPEN = 9;

    int R2_NOTXORPEN = 10;

    int R2_NOP = 11;

    int R2_MERGENOTPEN = 12;

    int R2_COPYPEN = 13;

    int R2_MERGEPENNOT = 14;

    int R2_MERGEPEN = 15;

    int R2_WHITE = 16;

    int ALTERNATE = 1;

    int WINDING = 2;

    int TA_BASELINE = 24;

    int TA_BOTTOM = 8;

    int TA_TOP = 0;

    int TA_CENTER = 6;

    int TA_LEFT = 0;

    int TA_RIGHT = 2;

    int TA_NOUPDATECP = 0;

    int TA_RTLREADING = 256;

    int TA_UPDATECP = 1;

    int MM_TEXT = 1;

    int MM_LOMETRIC = 2;

    int MM_HIMETRIC = 3;

    int MM_LOENGLISH = 4;

    int MM_HIENGLISH = 5;

    int MM_TWIPS = 6;

    int MM_ISOTROPIC = 7;

    int MM_ANISOTROPIC = 8;

    int AD_COUNTERCLOCKWISE = 1;

    int AD_CLOCKWISE = 2;

    int RGN_AND = 1;

    int RGN_OR = 2;

    int RGN_XOR = 3;

    int RGN_DIFF = 4;

    int RGN_COPY = 5;

    int RGN_MIN = RGN_AND;

    int RGN_MAX = RGN_COPY;

    int BKG_TRANSPARENT = 1;

    int BKG_OPAQUE = 2;

    int PT_CLOSEFIGURE = 0x01;

    int PT_LINETO = 0x02;

    int PT_BEZIERTO = 0x04;

    int PT_MOVETO = 0x06;

    int MWT_IDENTITY = 1;

    int MWT_LEFTMULTIPLY = 2;

    int MWT_RIGHTMULTIPLY = 3;

    int BI_RGB = 0;

    int BI_RLE8 = 1;

    int BI_RLE4 = 2;

    int BI_BITFIELDS = 3;

    int BI_JPEG = 4;

    int BI_PNG = 5;

    int BS_SOLID = 0;

    int BS_NULL = 1;

    int BS_HATCHED = 2;

    int BS_PATTERN = 3;

    int BS_INDEXED = 4;

    int BS_DIBPATTERN = 5;

    int BS_DIBPATTERNPT = 6;

    int BS_PATTERN8X8 = 7;

    int BS_DIBPATTERN8X8 = 8;

    int BS_MONOPATTERN = 9;

    int BS_HOLLOW = BS_NULL;

    int DIB_RGB_COLORS = 0;

    int DIB_PAL_COLORS = 1;

    int HS_HORIZONTAL = 0; /* ----- */

    int HS_VERTICAL = 1; /* ||||| */

    int HS_FDIAGONAL = 2; /* \\\\\ */

    int HS_BDIAGONAL = 3; /* ///// */

    int HS_CROSS = 4; /* +++++ */

    int HS_DIAGCROSS = 5; /* xxxxx */

    int PS_GEOMETRIC = 0x00010000;

    int PS_COSMETIC = 0x00000000;

    int PS_SOLID = 0x00000000;

    int PS_DASH = 0x00000001;

    int PS_DOT = 0x00000002;

    int PS_DASHDOT = 0x00000003;

    int PS_DASHDOTDOT = 0x00000004;

    int PS_NULL = 0x00000005;

    int PS_INSIDEFRAME = 0x00000006;

    int PS_USERSTYLE = 0x00000007;

    int PS_ENDCAP_ROUND = 0x00000000;

    int PS_ENDCAP_SQUARE = 0x00000100;

    int PS_ENDCAP_FLAT = 0x00000200;

    int PS_JOIN_ROUND = 0x00000000;

    int PS_JOIN_BEVEL = 0x00001000;

    int PS_JOIN_MITER = 0x00002000;

    int AC_SRC_OVER = 0x00;

    int AC_SRC_ALPHA = 0x01;

    int GDICOMMENT_BEGINGROUP       = 0x00000002;

    int GDICOMMENT_ENDGROUP         = 0x00000003;

    int GDICOMMENT_UNICODE_STRING   = 0x00000040;

    int GDICOMMENT_UNICODE_END      = 0x00000080;

    int GDICOMMENT_MULTIFORMATS     = 0x40000004;

    int GDICOMMENT_IDENTIFIER       = 0x43494447;

    int GDICOMMENT_WINDOWS_METAFILE = 0x80000001;
}
