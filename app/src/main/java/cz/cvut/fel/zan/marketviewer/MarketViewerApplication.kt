package cz.cvut.fel.zan.marketviewer

import android.app.Application
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.zan.marketviewer.core.di.appModules
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.startKoin

class MarketViewerApplication(
) : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MarketViewerApplication)
            modules(appModules)
        }
    }
}