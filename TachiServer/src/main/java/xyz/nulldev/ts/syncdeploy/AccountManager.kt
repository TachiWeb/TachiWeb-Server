package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import com.kizitonwose.time.hours
import com.kizitonwose.time.milliseconds
import com.kizitonwose.time.minutes
import com.kizitonwose.time.schedule
import xyz.nulldev.ts.config.ConfigManager
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.withLock

class AccountManager: KodeinGlobalAware {
    private val accounts = ConcurrentHashMap<String, Account>()
    private val syncConfig = instance<ConfigManager>().module<SyncConfigModule>()

    val VALID_USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyz".let {
        it + it.toUpperCase() + "_-@" + "0123456789"
    }.toCharArray()
    val MAX_USERNAME_LENGTH = 100

    fun confAccount(account: String, password: String) {
        lockAcc(account) {
            //Write config
            it.folder.mkdirs()
            it.configFolder.mkdirs()
            val configFile = File(it.configFolder, "server.config")
            configFile.writeText("""
|ts.server.rootDir = ${it.folder.absolutePath}/tachiserver-data
    """.trimMargin())

            //Copy sandbox template config
            if(syncConfig.sandboxedConfig.isFile)
                syncConfig.sandboxedConfig.copyTo(File(it.configFolder, "sandbox_template.config"))

            //Conf password
            confAccountPw(account, password)
        }
    }

    fun confAccountPw(account: String, password: String) {
        lockAcc(account) {
            //Write password
            it.pwFile.writeText(if (password.isNotEmpty())
                PasswordHasher.getSaltedHash(password)
            else "")
        }
    }

    fun authAccount(account: String, password: String?): Boolean {
        return lockAcc(account) {
            val hash = it.pwFile.readText().trim()

            if(password == null || password.isEmpty())
                hash.isEmpty()
            else
                hash.isNotEmpty() && PasswordHasher.check(password, hash)
        }
    }

    fun authToken(account: String, token: String?): Boolean {
        return lockAcc(account) {
            if(token != null && token.isNotEmpty())
                it.token.toString() == token.trim()
            else {
                val hash = it.pwFile.readText().trim()

                return hash.isEmpty()
            }
        }
    }

    @Synchronized
    fun getAccount(account: String)
            = synchronized(accounts) {
        accounts.getOrPut(account, {
            Account(account)
        })
    }

    inline fun <T> lockAcc(account: String, block: (Account) -> T): T
            = getAccount(account).let {
        val res = it.lock.withLock {
            block(it)
        }
        it.lastUsedTime = System.currentTimeMillis()
        res
    }

    private val ACCOUNT_REMOVAL_TIMEOUT = 1.hours

    init {
        val timer = Timer()
        timer.schedule(1.minutes, 1.minutes) {
            println("Reaping accounts...")
            synchronized(accounts) {
                val toRemove = mutableListOf<Account>()
                accounts.forEach { _: String, u: Account ->
                    if(u.lock.tryLock()) {
                        val diff = System.currentTimeMillis() - u.lastUsedTime
                        if (diff.milliseconds > ACCOUNT_REMOVAL_TIMEOUT)
                            toRemove.add(u) //Keep account locked
                        else u.lock.unlock()
                    }
                }
                toRemove.forEach {
                    accounts.remove(it.name)
                    it.lock.unlock()
                }
            }
        }
    }
}