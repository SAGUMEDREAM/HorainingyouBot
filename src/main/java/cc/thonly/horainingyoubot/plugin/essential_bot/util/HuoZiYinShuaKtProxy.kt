package cc.thonly.horainingyoubot.plugin.essential_bot.util

import huozi.concatAudio
import huozi.parsePinyin
import huozi.tempDir

class HuoZiYinShuaKtProxy {
    companion object {
        @JvmStatic
        fun huoZiYinShua(s: String): ByteArray? {
            val voicePartList = parsePinyin(s)
            val audioBytes = concatAudio(voicePartList)
            return audioBytes
        }
        @JvmStatic
        fun deleteTemp() {
            tempDir.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
        }
    }
}