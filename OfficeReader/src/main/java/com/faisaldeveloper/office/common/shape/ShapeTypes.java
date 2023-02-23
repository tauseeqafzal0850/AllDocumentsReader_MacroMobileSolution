/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package com.faisaldeveloper.office.common.shape;

public interface ShapeTypes {
    int NotPrimitive = 0;
    int Rectangle = 1;
    int RoundRectangle = 2;
    int Ellipse = 3;
    int Diamond = 4;
    int Triangle = 5;
    int RtTriangle = 6;
    int Parallelogram = 7;
    int Trapezoid = 8;
    int Hexagon = 9;
    int Octagon = 10;
    int Plus = 11;
    int Star = 12;
    int RightArrow = 13;
    int ThickArrow = 14;
    int HomePlate = 15;                                 // 五边形
    int Cube = 16;
    int Balloon = 17;
    int Seal = 18;
    int Arc = 19;
    int Line = 20;
    int Plaque = 21;
    int Can = 22;
    int Donut = 23;
    int TextSimple = 24;
    int TextOctagon = 25;
    int TextHexagon = 26;
    int TextCurve = 27;
    int TextWave = 28;
    int TextRing = 29;
    int TextOnCurve = 30;
    int TextOnRing = 31;
    int StraightConnector1 = 32;
    int BentConnector2 = 33;
    int BentConnector3 = 34;
    int BentConnector4 = 35;
    int BentConnector5 = 36;
    int CurvedConnector2 = 37;
    int CurvedConnector3 = 38;
    int CurvedConnector4 = 39;
    int CurvedConnector5 = 40;
    int Callout2 = 41;
    int Callout3 = 42;
    int Callout4 = 43;
    int AccentCallout2 = 44;
    int AccentCallout3 = 45;
    int AccentCallout4 = 46;
    int BorderCallout2 = 47;
    int BorderCallout3 = 48;
    int BorderCallout4 = 49;
    int AccentBorderCallout2 = 50;
    int AccentBorderCallout3 = 51;
    int AccentBorderCallout4 = 52;
    int Ribbon = 53;                                    // 前凸带形
    int Ribbon2 = 54;                                   // 上凸带形
    int Chevron = 55;                                   // 燕尾形
    int Pentagon = 56;
    int NoSmoking = 57;
    int Star8 = 58;
    int Star16 = 59;
    int Star32 = 60;
    int WedgeRectCallout = 61;
    int WedgeRoundRectCallout = 62;
    int WedgeEllipseCallout = 63;
    int Wave = 64;
    int FoldedCorner = 65;
    int LeftArrow = 66;
    int DownArrow = 67;
    int UpArrow = 68;
    int LeftRightArrow = 69;
    int UpDownArrow = 70;
    int IrregularSeal1 = 71;                            // 爆炸形1
    int IrregularSeal2 = 72;                            // 爆炸形2
    int LightningBolt = 73;
    int Heart = 74;
    int PictureFrame = 75;
    int QuadArrow = 76;
    int LeftArrowCallout = 77;
    int RightArrowCallout = 78;
    int UpArrowCallout = 79;
    int DownArrowCallout = 80;
    int LeftRightArrowCallout = 81;
    int UpDownArrowCallout = 82;
    int QuadArrowCallout = 83;
    int Bevel = 84;
    int LeftBracket = 85;
    int RightBracket = 86;
    int LeftBrace = 87;
    int RightBrace = 88;
    int LeftUpArrow = 89;                               // 直角双向箭头
    int BentUpArrow = 90;                               // 直角上箭头
    int BentArrow = 91;                                 // 圆角右箭头
    int Star24 = 92;
    int StripedRightArrow = 93;                         // 虚尾箭头
    int NotchedRightArrow = 94;                         // 燕尾形箭头
    int BlockArc = 95;
    int SmileyFace = 96;
    int VerticalScroll = 97;
    int HorizontalScroll = 98;
    int CircularArrow = 99;                             // 环形箭头
    int NotchedCircularArrow = 100;
    int UturnArrow = 101;                               // 手杖形箭头
    int CurvedRightArrow = 102;                         // 左弧形箭头
    int CurvedLeftArrow = 103;                          // 右弧形箭头
    int CurvedUpArrow = 104;                            // 下弧形箭头
    int CurvedDownArrow = 105;                          // 上弧形箭头
    int CloudCallout = 106;
    int EllipseRibbon = 107;                            // 前凸弯带形
    int EllipseRibbon2 = 108;                           // 上凸弯带形
    int FlowChartProcess = 109;
    int FlowChartDecision = 110;
    int FlowChartInputOutput = 111;
    int FlowChartPredefinedProcess = 112;
    int FlowChartInternalStorage = 113;
    int FlowChartDocument = 114;
    int FlowChartMultidocument = 115;
    int FlowChartTerminator = 116;
    int FlowChartPreparation = 117;
    int FlowChartManualInput = 118;
    int FlowChartManualOperation = 119;
    int FlowChartConnector = 120;
    int FlowChartPunchedCard = 121;
    int FlowChartPunchedTape = 122;
    int FlowChartSummingJunction = 123;
    int FlowChartOr = 124;
    int FlowChartCollate = 125;
    int FlowChartSort = 126;
    int FlowChartExtract = 127;
    int FlowChartMerge = 128;
    int FlowChartOfflineStorage = 129;
    int FlowChartOnlineStorage = 130;
    int FlowChartMagneticTape = 131;
    int FlowChartMagneticDisk = 132;
    int FlowChartMagneticDrum = 133;
    int FlowChartDisplay = 134;
    int FlowChartDelay = 135;
    int TextPlainText = 136;
    int TextStop = 137;
    int TextTriangle = 138;
    int TextTriangleInverted = 139;
    int TextChevron = 140;
    int TextChevronInverted = 141;
    int TextRingInside = 142;
    int TextRingOutside = 143;
    int TextArchUpCurve = 144;
    int TextArchDownCurve = 145;
    int TextCircleCurve = 146;
    int TextButtonCurve = 147;
    int TextArchUpPour = 148;
    int TextArchDownPour = 149;
    int TextCirclePour = 150;
    int TextButtonPour = 151;
    int TextCurveUp = 152;
    int TextCurveDown = 153;
    int TextCascadeUp = 154;
    int TextCascadeDown = 155;
    int TextWave1 = 156;
    int TextWave2 = 157;
    int TextWave3 = 158;
    int TextWave4 = 159;
    int TextInflate = 160;
    int TextDeflate = 161;
    int TextInflateBottom = 162;
    int TextDeflateBottom = 163;
    int TextInflateTop = 164;
    int TextDeflateTop = 165;
    int TextDeflateInflate = 166;
    int TextDeflateInflateDeflate = 167;
    int TextFadeRight = 168;
    int TextFadeLeft = 169;
    int TextFadeUp = 170;
    int TextFadeDown = 171;
    int TextSlantUp = 172;
    int TextSlantDown = 173;
    int TextCanUp = 174;
    int TextCanDown = 175;
    int FlowChartAlternateProcess = 176;
    int FlowChartOffpageConnector = 177;
    int Callout1 = 178;
    int AccentCallout1 = 179;
    int BorderCallout1 = 180;
    int AccentBorderCallout1 = 181;
    int LeftRightUpArrow = 182;                         // 丁字箭头
    int Sun = 183;
    int Moon = 184;
    int BracketPair = 185;
    int BracePair = 186;
    int Star4 = 187;                                    // 十字星
    int DoubleWave = 188;                               // 双波形
    int ActionButtonBlank = 189;
    int ActionButtonHome = 190;
    int ActionButtonHelp = 191;
    int ActionButtonInformation = 192;
    int ActionButtonForwardNext = 193;
    int ActionButtonBackPrevious = 194;
    int ActionButtonEnd = 195;
    int ActionButtonBeginning = 196;
    int ActionButtonReturn = 197;
    int ActionButtonDocument = 198;
    int ActionButtonSound = 199;
    int ActionButtonMovie = 200;
    int HostControl = 201;
    int TextBox = 202;
    
    int IsocelesTriangle = 203;
    int RightTriangle = 204;
    int Arrow = 205;
    int Callout90 = 206;
    int AccentCallout90 = 207;
    int BorderCallout90 = 208;
    int AccentBorderCallout90 = 209;
    
    // add new
    int Round1Rect = 210;
    int Round2SameRect = 211;
    int Round2DiagRect = 212;
    int Snip1Rect = 213;
    int Snip2SameRect = 214;
    int Snip2DiagRect = 215;
    int SnipRoundRect = 216;
    int Heptagon = 217;
    int Decagon = 218;
    int Dodecagon = 219;
    int Pie = 220;
    int Chord = 221;
    int Teardrop = 222;
    int Frame = 223;
    int HalfFrame = 224;
    int Corner = 225;
    int DiagStripe = 226;
    int MathPlus = 227;
    int MathMinus = 228;
    int MathMultiply = 229;
    int MathDivide = 230;
    int MathEqual = 231;
    int MathNotEqual = 232;
    //Arbitrary polygon
    int ArbitraryPolygon = 233;
    int Cloud = 234;
    int Star5 = 235;
    int Star6 = 236;
    int Star7 = 237;
    int Star10 = 238;
    int Star12 = 239;
    
    /**
     * for smart art shape
     */
    int Funnel = 240;
    int Gear6 = 241;
    int Gear9 = 242;
    int LeftCircularArrow = 243;
    int LeftRightRibbon = 244;
    int PieWedge = 245;
    int SwooshArrow = 246;
    
    int WP_Line = 247;
    int Curve = 248;
    //direct line polygon
    int DirectPolygon = 249;
}
