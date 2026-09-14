/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 29/12/2024
 */

package aragones.sergio.readercollection.data.local.di

import aragones.sergio.readercollection.data.di.dataModule
import aragones.sergio.readercollection.data.local.SharedPreferencesHandler
import aragones.sergio.readercollection.data.local.UserLocalDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val storageModule = module {
    includes(localDataSourceModule)
    includes(platformModule)
    includes(dataModule)
    singleOf(::SharedPreferencesHandler)
    factoryOf(::UserLocalDataSource)
}