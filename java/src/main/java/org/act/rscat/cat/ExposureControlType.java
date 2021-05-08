package org.act.rscat.cat;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines possible values for exposure type.
 * <p>
 * ExposureType indicates whether exposure control is at the passage or the item
 * level.
 */
public enum ExposureControlType {
    /**
     * NONE: no exposure control; PASSAGE: passage level exposure control; ITEM:
     * item level exposure control.
     */
    NONE(1), PASSAGE(2), ITEM(3);

    /**
     * A collection object to hold exposure control types.
     */
    private static final Map<Integer, ExposureControlType> TYPEMAP = new HashMap<>();

    /**
     * The exposure control type code.
     */
    private final int code;

    static {
        for (ExposureControlType type : ExposureControlType.values()) {
            TYPEMAP.put(type.getCode(), type);
        }
    }

    /**
     * Constructs a exposure type.
     *
     * @param code the exposure type code
     */
    ExposureControlType(int code) {
        this.code = code;
    }

    /**
     * Looks up an exposure type by code.
     *
     * @param code the lookup code
     * @return the exposure type
     */
    public static ExposureControlType lookup(int code) {
        return TYPEMAP.get(code);
    }

    /**
     * Returns the exposure type code.
     *
     * @return the exposure type code
     */
    public int getCode() {
        return code;
    }
}
