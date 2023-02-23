package com.faisaldeveloper.office.thirdpart.emf.data;

import com.faisaldeveloper.office.thirdpart.emf.EMFRenderer;

/**
 * A GDIObject uses a {@link EMFRenderer}
 * to render itself to a Graphics2D object.
 *
 * @author Steffen Greiffenberg
 * @version $Id$
 */
public interface GDIObject {

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    void render(EMFRenderer renderer);
}
