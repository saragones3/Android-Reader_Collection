//
//  AppUpdateChecker.swift
//  ReaderCollection
//
//  Created by Sergio Aragonés on 24/1/26.
//

import Foundation
import UIKit

class AppUpdateChecker {
    
    public static func checkForUpdate(isThereMandatoryUpdate: Bool, completionHandler: @escaping @Sendable (Bool) -> Void) {
        
        guard let info = Bundle.main.infoDictionary,
              let currentVersion = info["CFBundleShortVersionString"] as? String,
              let bundleId = Bundle.main.bundleIdentifier,
              let url = URL(string: "https://itunes.apple.com/lookup?bundleId=\(bundleId)") else {
            completionHandler(false)
            return
        }
        URLSession.shared.dataTask(with: url) { data, _, _ in
            guard let data = data,
                  let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let results = (json["results"] as? [[String: Any]])?.first,
                  let latestVersion = results["version"] as? String,
                  let appStoreURL = results["trackViewUrl"] as? String else {
                completionHandler(false)
                return
            }
            
            if latestVersion.compare(currentVersion, options: .numeric) == .orderedDescending {
                showUpdatePopUp { accepted in
                    if accepted {
                        openAppStore(appStoreURL: appStoreURL)
                    } else if isThereMandatoryUpdate {
                        exit(0)
                    } else {
                        
                    }
                }
            } else {
                completionHandler(false)
            }
        }.resume()
    }
    
    private static func showUpdatePopUp(completionHandler: @escaping @Sendable (Bool) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Update Available",
                message: "A new version of the app is available. Would you like to update now?",
                preferredStyle: .alert
            )
            alert.addAction(UIAlertAction(title: "Update", style: .default) { _ in
                completionHandler(true)
            })
            alert.addAction(UIAlertAction(title: "Later", style: .cancel) { _ in
                completionHandler(false)
            })
            if let scene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
               let rootVC = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController {
                rootVC.present(alert, animated: true)
            }
        }
    }
    
    private static func openAppStore(appStoreURL: String) {
        if let url = URL(string: appStoreURL) {
            UIApplication.shared.open(url)
        }
    }
}
