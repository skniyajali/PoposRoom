package com.niyaj.common.decoder

import android.net.Uri
import javax.inject.Inject

class UriDecoder @Inject constructor() : Decoder {
    override fun decodeString(encodedString: String): String = Uri.decode(encodedString)

    override fun decodeInt(encodedInt: Int): Int = Uri.decode(encodedInt.toString()).toInt()
}
