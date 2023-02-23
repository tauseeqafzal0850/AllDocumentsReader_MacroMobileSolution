package com.example.alldocumentsreaderimagescanner.converter.interfaces

import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity

/**
 * The [Enhancer] is a functional interface for all enhancements.
 */
interface Enhancer {
    /**
     * To apply an enhancement.
     */
    fun enhance()

    /**
     * @return The [EnhancementOptionsEntity] for this [Enhancer].
     */
    val enhancementOptionsEntity: EnhancementOptionsEntity?
}