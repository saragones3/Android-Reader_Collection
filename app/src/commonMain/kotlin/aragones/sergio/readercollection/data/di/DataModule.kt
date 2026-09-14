/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 10/9/2026
 */

package aragones.sergio.readercollection.data.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}