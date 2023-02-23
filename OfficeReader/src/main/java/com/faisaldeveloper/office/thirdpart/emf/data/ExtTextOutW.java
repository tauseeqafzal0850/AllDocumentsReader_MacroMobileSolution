// Copyright 2002-2007, FreeHEP.
package com.faisaldeveloper.office.thirdpart.emf.data;

import java.io.IOException;

import com.faisaldeveloper.office.java.awt.Rectangle;
import com.faisaldeveloper.office.thirdpart.emf.EMFConstants;
import com.faisaldeveloper.office.thirdpart.emf.EMFInputStream;
import com.faisaldeveloper.office.thirdpart.emf.EMFTag;

/**
 * ExtTextOutW TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtTextOutW.java 10525 2007-02-12 04:08:30Z duns $
 */
public class ExtTextOutW extends AbstractExtTextOut implements EMFConstants {

    private TextW text;

    public ExtTextOutW() {
        super(84, 1, null, 0, 1, 1);
    }

    public ExtTextOutW(
        Rectangle bounds,
        int mode,
        float xScale,
        float yScale,
        TextW text) {

        super(84, 1, bounds, mode, xScale, yScale);
        this.text = text;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ExtTextOutW(
            emf.readRECTL(),
            emf.readDWORD(),
            emf.readFLOAT(),
            emf.readFLOAT(),
            TextW.read(emf));
    }

    public Text getText() {
        return text;
    }
}
