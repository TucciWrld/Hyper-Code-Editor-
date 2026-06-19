package com.example.data

import org.json.JSONObject

object DependencyManager {

    data class PackageInfo(
        val name: String,
        val version: String,
        val description: String,
        val cdnUrl: String,
        val isInstalled: Boolean = false
    )

    val AVAILABLE_PACKAGES = listOf(
        PackageInfo(
            name = "react",
            version = "^18.2.0",
            description = "The library for web and native user interfaces.",
            cdnUrl = "https://unpkg.com/react@18/umd/react.development.js"
        ),
        PackageInfo(
            name = "react-dom",
            version = "^18.2.0",
            description = "React package for working with the DOM.",
            cdnUrl = "https://unpkg.com/react-dom@18/umd/react-dom.development.js"
        ),
        PackageInfo(
            name = "axios",
            version = "^1.6.0",
            description = "Promise based HTTP client for the browser and node.js.",
            cdnUrl = "https://unpkg.com/axios/dist/axios.min.js"
        ),
        PackageInfo(
            name = "tailwindcss",
            version = "^3.3.5",
            description = "A utility-first CSS framework for rapid UI development.",
            cdnUrl = "https://cdn.tailwindcss.com"
        ),
        PackageInfo(
            name = "lodash",
            version = "^4.17.21",
            description = "A modern JavaScript utility library delivering modularity, performance & extras.",
            cdnUrl = "https://cdn.jsdelivr.net/npm/lodash@4.17.21/lodash.min.js"
        ),
        PackageInfo(
            name = "framer-motion",
            version = "^10.16.4",
            description = "An open-source production-ready animation library for React.",
            cdnUrl = "https://unpkg.com/framer-motion@10.16.4/dist/framer-motion.js"
        ),
        PackageInfo(
            name = "react-router-dom",
            version = "^6.20.0",
            description = "Declarative routing for React web applications.",
            cdnUrl = "https://unpkg.com/react-router-dom@6.20.0/dist/umd/react-router-dom.production.min.js"
        ),
        PackageInfo(
            name = "redux-toolkit",
            version = "^1.9.7",
            description = "The official, opinionated, batteries-included toolset for efficient Redux development.",
            cdnUrl = "https://unpkg.com/@reduxjs/toolkit@1.9.7/dist/redux-toolkit.umd.js"
        )
    )

    fun getInstalledDependencies(packageJsonContent: String): List<PackageInfo> {
        return try {
            val json = JSONObject(packageJsonContent)
            val deps = json.optJSONObject("dependencies") ?: return emptyList()
            val list = mutableListOf<PackageInfo>()
            for (key in deps.keys()) {
                val version = deps.getString(key)
                val standardPkg = AVAILABLE_PACKAGES.firstOrNull { it.name == key }
                list.add(
                    PackageInfo(
                        name = key,
                        version = version,
                        description = standardPkg?.description ?: "External library installed via package manager.",
                        cdnUrl = standardPkg?.cdnUrl ?: "https://unpkg.com/$key",
                        isInstalled = true
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addDependency(packageJsonContent: String, packageName: String): String {
        return try {
            val json = if (packageJsonContent.trim().isEmpty()) {
                JSONObject("""{"name": "hyper-project", "dependencies": {}}""")
            } else {
                JSONObject(packageJsonContent)
            }
            val deps = json.optJSONObject("dependencies") ?: JSONObject().also { json.put("dependencies", it) }
            val pkg = AVAILABLE_PACKAGES.firstOrNull { it.name == packageName }
            deps.put(packageName, pkg?.version ?: "latest")
            json.toString(2)
        } catch (e: Exception) {
            packageJsonContent
        }
    }

    fun removeDependency(packageJsonContent: String, packageName: String): String {
        return try {
            val json = JSONObject(packageJsonContent)
            val deps = json.optJSONObject("dependencies") ?: return packageJsonContent
            deps.remove(packageName)
            json.toString(2)
        } catch (e: Exception) {
            packageJsonContent
        }
    }

    fun generateCdnInjections(packageJsonContent: String): String {
        val installed = getInstalledDependencies(packageJsonContent)
        val sb = StringBuilder()
        // Always include Babel standalone to handle React, JSX, files
        sb.append("<script src=\"https://unpkg.com/@babel/standalone/babel.min.js\"></script>\n")
        
        // Ensure react and react-dom are injected first if they are in the package dependencies
        val hasReact = installed.any { it.name == "react" }
        val hasReactDOM = installed.any { it.name == "react-dom" }

        if (hasReact) {
            sb.append("<script src=\"https://unpkg.com/react@18/umd/react.development.js\"></script>\n")
        }
        if (hasReactDOM) {
            sb.append("<script src=\"https://unpkg.com/react-dom@18/umd/react-dom.development.js\"></script>\n")
        }

        for (pkg in installed) {
            if (pkg.name == "react" || pkg.name == "react-dom") continue
            sb.append("<script src=\"${pkg.cdnUrl}\"></script>\n")
        }
        return sb.toString()
    }
}
