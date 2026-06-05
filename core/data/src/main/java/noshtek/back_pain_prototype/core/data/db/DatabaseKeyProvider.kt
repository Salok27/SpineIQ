package noshtek.back_pain_prototype.core.data.db

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates and persists the SQLCipher database passphrase using Android Keystore-backed
 * EncryptedSharedPreferences (NFR-05).
 *
 * On first call a 32-byte random key is generated, Base64-encoded, and stored encrypted.
 * Subsequent calls retrieve and decode the same key. The key never leaves the device in plaintext.
 */
@Singleton
class DatabaseKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        @Suppress("DEPRECATION")
        EncryptedSharedPreferences.create(
            "spineiq_db_key_store",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getOrCreatePassphrase(): ByteArray {
        val stored = prefs.getString(KEY_DB_PASSPHRASE, null)
        if (stored != null) return Base64.decode(stored, Base64.DEFAULT)

        val newKey = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit()
            .putString(KEY_DB_PASSPHRASE, Base64.encodeToString(newKey, Base64.DEFAULT))
            .apply()
        return newKey
    }

    private companion object {
        const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
