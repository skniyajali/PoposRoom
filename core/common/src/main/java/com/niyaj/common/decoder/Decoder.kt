package com.niyaj.common.decoder

interface Decoder {
    fun decodeString(encodedString: String): String
    
    fun decodeInt(encodedInt: Int): Int
}
