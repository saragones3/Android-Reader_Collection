/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 24/1/2026
 */

package aragones.sergio.readercollection.utils

import aragones.sergio.readercollection.domain.UserRepository
import io.ktor.client.request.invoke
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import kotlinx.serialization.Serializable
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringEncodingForData

class AppUpdateChecker2(
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {

    //region Private properties
    private val _installStatus = MutableStateFlow(InstallStatus.UNKNOWN)
    //endregion

    //region Public properties
    val installStatus: StateFlow<InstallStatus> = _installStatus
    //endregion

    fun checkForUpdate() {

        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: ""
        val url = NSURL(string = "https://itunes.apple.com/lookup?bundleId=$bundleId")
        NSURLSession.sharedSession.dataTaskWithURL(url) { data, _, _ ->

            data?.let {
                val json = Json { ignoreUnknownKeys = true }
                val string = NSString.stringEncodingForData(it)
                json.decodeFromString<Map<String, Any>>(string)
            }


            if (data != null) {
                Json { ignoreUnknownKeys = true }
            }
            val json = Json { ignoreUnknownKeys = true }
            data?.let {
                NSString()
                val responseString =
                    NSString(data = it, encoding = NSUTF8StringEncoding)?.toString() ?: ""
                val response = try {
                    json.decodeFromString<ItunesSearchResponse>(responseString)
                } catch (e: Exception) {
                    null
                }
                response?.results?.firstOrNull()?.let { appInfo ->
                    if (isNewerVersion(appInfo.version, currentVersion)) {
                        CoroutineScope(ioDispatcher).launch {
                            val isThereMandatoryUpdate = userRepository.isThereMandatoryUpdate()
                            if (isThereMandatoryUpdate) {
                                _installStatus.value = InstallStatus.MANDATORY_UPDATE
                            } else {
                                _installStatus.value = InstallStatus.OPTIONAL_UPDATE
                            }
                        }
                    } else {
                        _installStatus.value = InstallStatus.INSTALLED
                    }
                }
            }


            guard let data = data,
            let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let results = (json["results"] as? [[String: Any]])?.first,
            let latestVersion = results["version"] as? String,
            let appStoreURL = results["trackViewUrl"] as? String else { return }
            if latestVersion.compare(currentVersion, options: .numeric) == .orderedDescending {
                showUpdateSuggestionPopUp(appStoreURL: appStoreURL)
            }
        }.resume()



        val isUpdateAvailable = true

        if (isUpdateAvailable) {
            CoroutineScope(ioDispatcher).launch {
                val isThereMandatoryUpdate = userRepository.isThereMandatoryUpdate()
                if (isThereMandatoryUpdate) {
                    _installStatus.value = InstallStatus.MANDATORY_UPDATE
                } else {
                    _installStatus.value = InstallStatus.OPTIONAL_UPDATE
                }
            }
        } else {
            _installStatus.value = InstallStatus.INSTALLED
        }
    }
}

enum class InstallStatus {
    UNKNOWN,
    INSTALLED,
    MANDATORY_UPDATE,
    OPTIONAL_UPDATE
}

@Serializable
private data class ItunesSearchResponse(val results: List<AppInfo>)

@Serializable
private data class AppInfo(
    val version: String,
    val trackViewUrl: String
)