/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.remote.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule = module {
    single<String>(named("api_key")) { "AIzaSyB34yKO3Snl1CVOSoR8jurnODVtAm3_ZRI" }
    single<HttpClientEngine> { Js.create() }
}