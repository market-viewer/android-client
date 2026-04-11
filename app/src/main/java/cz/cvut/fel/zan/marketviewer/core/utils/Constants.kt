package cz.cvut.fel.zan.marketviewer.core.utils

//backend endpoints
//const val backendBaseUrl = "https://api.market-viewer.jotalac.dev/"
//const val frontendBaseUrl = "https://market-viewer.jotalac.dev/"

//request to localhost on host machine
const val backendBaseUrl = "http://10.0.2.2:8080/"
const val backendSSOEndpoint = backendBaseUrl + "oauth2/authorization/github"
const val SSOCallbackEndpoint = backendBaseUrl + "oauth2/github/callback?token={token}"