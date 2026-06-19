/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local.di

import aragones.sergio.readercollection.data.local.AppInfoProvider
import aragones.sergio.readercollection.data.local.LocalStorageProvider
import aragones.sergio.readercollection.data.local.SharedPreferencesProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::AppInfoProvider)
    single<SharedPreferencesProvider> { LocalStorageProvider() }
}