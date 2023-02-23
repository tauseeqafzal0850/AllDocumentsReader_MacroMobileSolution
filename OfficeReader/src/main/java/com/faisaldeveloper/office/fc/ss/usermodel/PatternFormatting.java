/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */

package com.faisaldeveloper.office.fc.ss.usermodel;

/**
 * @author Yegor Kozlov
 */
public interface PatternFormatting {
    /**  No background */
    short     NO_FILL             = 0  ;
    /**  Solidly filled */
    short     SOLID_FOREGROUND    = 1  ;
    /**  Small fine dots */
    short     FINE_DOTS           = 2  ;
    /**  Wide dots */
    short     ALT_BARS            = 3  ;
    /**  Sparse dots */
    short     SPARSE_DOTS         = 4  ;
    /**  Thick horizontal bands */
    short     THICK_HORZ_BANDS    = 5  ;
    /**  Thick vertical bands */
    short     THICK_VERT_BANDS    = 6  ;
    /**  Thick backward facing diagonals */
    short     THICK_BACKWARD_DIAG = 7  ;
    /**  Thick forward facing diagonals */
    short     THICK_FORWARD_DIAG  = 8  ;
    /**  Large spots */
    short     BIG_SPOTS           = 9  ;
    /**  Brick-like layout */
    short     BRICKS              = 10 ;
    /**  Thin horizontal bands */
    short     THIN_HORZ_BANDS     = 11 ;
    /**  Thin vertical bands */
    short     THIN_VERT_BANDS     = 12 ;
    /**  Thin backward diagonal */
    short     THIN_BACKWARD_DIAG  = 13 ;
    /**  Thin forward diagonal */
    short     THIN_FORWARD_DIAG   = 14 ;
    /**  Squares */
    short     SQUARES             = 15 ;
    /**  Diamonds */
    short     DIAMONDS            = 16 ;
    /**  Less Dots */
    short     LESS_DOTS           = 17 ;
    /**  Least Dots */
    short     LEAST_DOTS          = 18 ;

    short getFillBackgroundColor();

    short getFillForegroundColor();

    short getFillPattern();

    void setFillBackgroundColor(short bg);

    void setFillForegroundColor(short fg);

    void setFillPattern(short fp);
}
