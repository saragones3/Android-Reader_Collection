/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package aragones.sergio.readercollection.data.local

class LocalStorageProvider : SharedPreferencesProvider {

    override fun writeBoolean(key: String, value: Boolean, isEncrypted: Boolean) {
        setItemJs(key, value.toString())
    }

    override fun writeInt(key: String, value: Int, isEncrypted: Boolean) {
        setItemJs(key, value.toString())
    }

    override fun writeString(key: String, value: String?, isEncrypted: Boolean) {
        if (value != null) {
            setItemJs(key, value)
        } else {
            removeItemJs(key)
        }
    }

    override fun readBoolean(key: String, defaultValue: Boolean, isEncrypted: Boolean): Boolean =
        getItemJs(key)?.toBoolean() ?: defaultValue

    override fun readInt(key: String, defaultValue: Int, isEncrypted: Boolean): Int =
        getItemJs(key)?.toIntOrNull() ?: defaultValue

    override fun readString(key: String, isEncrypted: Boolean): String? = getItemJs(key)

    override fun removeValues(keys: List<String>, isEncrypted: Boolean) {
        for (key in keys) {
            removeItemJs(key)
        }
    }
}

@JsFun("(key, value) => window.localStorage.setItem(key, value)")
external fun setItemJs(key: String, value: String)

@JsFun("(key) => window.localStorage.getItem(key)")
external fun getItemJs(key: String): String?

@JsFun("(key) => window.localStorage.removeItem(key)")
external fun removeItemJs(key: String)