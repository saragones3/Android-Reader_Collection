/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local.di

import aragones.sergio.readercollection.data.local.BooksLocalDataSource
import aragones.sergio.readercollection.data.local.DatabaseBooksLocalDataSource
import com.aragones.sergio.di.databaseModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val localDataSourceModule = module {
    includes(databaseModule)
    factoryOf(::DatabaseBooksLocalDataSource) bind BooksLocalDataSource::class
}