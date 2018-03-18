package xyz.nulldev.ts.syncdeploy

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** @author: https://stackoverflow.com/a/11038230/5054192 **/
object PasswordHasher {
    // The higher the number of iterations the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private val iterations = 20 * 1000
    private val saltLen = 32
    private val desiredKeyLen = 256

    /** Computes a salted PBKDF2 hash of given plaintext password
     * suitable for storing in a database.
     * Empty passwords are not supported.  */
    fun getSaltedHash(password: String): String {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen)
        // store the salt with the password
        return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt)
    }

    /** Checks whether given plaintext password corresponds
     * to a stored salted hash of the password.  */
    fun check(password: String, stored: String): Boolean {
        val saltAndPass = stored.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (saltAndPass.size != 2) {
            throw IllegalStateException(
                    "The stored password have the form 'salt\$hash'")
        }
        val hashOfInput = hash(password, Base64.getDecoder().decode(saltAndPass[0]))
        return hashOfInput == saltAndPass[1]
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private fun hash(password: String?, salt: ByteArray): String {
        if (password == null || password.isEmpty())
            throw IllegalArgumentException("Empty passwords are not supported.")
        val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val key = f.generateSecret(PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        )
        return Base64.getEncoder().encodeToString(key.encoded)
    }
}