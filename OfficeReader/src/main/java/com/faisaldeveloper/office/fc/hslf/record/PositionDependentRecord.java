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

package com.faisaldeveloper.office.fc.hslf.record;
import java.util.Hashtable;

/**
 * Records which either care about where they are on disk, or have other
 *  records who care about where they are, will implement this interface.
 * Normally, they'll subclass PositionDependentRecordAtom or
 *  PositionDependentRecordContainer, which will do the work of providing
 *  the setting and updating interfaces for them.
 * This is a special (and dangerous) kind of Record. When created, they
 *  need to be pinged with their current location. When written out, they
 *  need to be given their new location, and offered the list of records
 *  which have changed their location.
 *
 * @author Nick Burch
 */

public interface PositionDependentRecord
{
	/** Fetch our location on the disk, as of the last write out */
    int getLastOnDiskOffset();

	/**
	 * Update the Record's idea of where on disk it lives, after a write out.
	 * Use with care...
	 */
    void setLastOnDiskOffset(int offset);

	/**
	 * Offer the record the list of records that have changed their
	 *  location as part of the writeout.
	 */
    void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup);
	
	/**
     * 
     */
    void dispose();
}
