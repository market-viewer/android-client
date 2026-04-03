package cz.cvut.fel.zan.marketviewer.core.utils

//backend endpoints
const val backendBaseUrl = "https://api.market-viewer.jotalac.dev/"
const val frontendBaseUrl = "https://market-viewer.jotalac.dev/"
const val backendSSOEndpoint = backendBaseUrl + "oauth2/authorization/github"
const val SSOCallbackEndpoint = backendSSOEndpoint + "oauth2/github/callback?token={token}"