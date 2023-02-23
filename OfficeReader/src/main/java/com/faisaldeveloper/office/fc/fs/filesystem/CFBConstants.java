
package com.faisaldeveloper.office.fc.fs.filesystem;


/**
 * <p>A repository for constants shared by POI classes.</p>
 */
public interface CFBConstants
{
    /** Most files use 512 bytes as their big block size */
    int SMALLER_BIG_BLOCK_SIZE = 0x0200;
    BlockSize SMALLER_BIG_BLOCK_SIZE_DETAILS = new BlockSize(SMALLER_BIG_BLOCK_SIZE, (short)9);
    /** Some use 4096 bytes */
    int LARGER_BIG_BLOCK_SIZE = 0x1000;
    BlockSize LARGER_BIG_BLOCK_SIZE_DETAILS = new BlockSize(LARGER_BIG_BLOCK_SIZE, (short)12);

    /** How big a block in the small block stream is. Fixed size */
    int SMALL_BLOCK_SIZE = 0x0040;

    /** How big a single property is */
    int PROPERTY_SIZE = 0x0080;
    /** 
     * The minimum size of a document before it's stored using 
     *  Big Blocks (normal streams). Smaller documents go in the 
     *  Mini Stream (SBAT / Small Blocks)
     */
    int BIG_BLOCK_MINIMUM_DOCUMENT_SIZE = 0x1000;

    /** The highest sector number you're allowed, 0xFFFFFFFA */
    int LARGEST_REGULAR_SECTOR_NUMBER = -5;

    /** Indicates the sector holds a DIFAT block (0xFFFFFFFC) */
    int DIFAT_SECTOR_BLOCK = -4;
    /** Indicates the sector holds a FAT block (0xFFFFFFFD) */
    int FAT_SECTOR_BLOCK = -3;
    /** Indicates the sector is the end of a chain (0xFFFFFFFE) */
    int END_OF_CHAIN = -2;
    /** Indicates the sector is not used (0xFFFFFFFF) */
    int UNUSED_BLOCK = -1;
} 
