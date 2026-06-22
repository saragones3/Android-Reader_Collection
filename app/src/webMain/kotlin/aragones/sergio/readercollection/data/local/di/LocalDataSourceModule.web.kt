/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local.di

import aragones.sergio.readercollection.data.datasource.FirestoreBooksDataSource
import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val localDataSourceModule: Module = module {
    singleOf(::FirestoreBooksDataSource) bind BooksLocalDataSource::class
}