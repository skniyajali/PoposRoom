package com.niyaj.common.decoder.di

import com.niyaj.common.decoder.Decoder
import com.niyaj.common.decoder.UriDecoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DecoderModule {
    @Binds
    abstract fun bindStringDecoder(uriDecoder: UriDecoder): Decoder
}
