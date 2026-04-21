package cz.cvut.fel.zan.marketviewer.core.utils

import cz.cvut.fel.zan.marketviewer.BuildConfig

//backend endpoints
const val defaultBackendUrl: String = BuildConfig.DEFAULT_BASE_URL
const val backendSSORoute = "auth/sso/mobile"
const val SSOCallbackEndpoint = "marketviewer://sso/callback?token={token}"