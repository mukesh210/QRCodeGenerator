package util

import javax.crypto.{KeyGenerator, Cipher}
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64

trait Encryption {
  def encrypt(dataBytes: Array[Byte], secret: String): Array[Byte]
  def decrypt(codeBytes: Array[Byte], secret: String): Array[Byte]
}

class JavaCryptoEncryption(algorithm: String) extends Encryption {
  def decodeBase64(string: String) = Base64.decodeBase64(string)
  private def cipher(mode: Int, b64secret: String): Cipher = {
    val encipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding")
    encipher.init(mode, new SecretKeySpec(decodeBase64(b64secret), algorithm))
    encipher
  }

  def encrypt(bytes: Array[Byte], b64secret: String): Array[Byte] = {
    val encoder = cipher(Cipher.ENCRYPT_MODE, b64secret)
    encoder.doFinal(bytes)
  }

  def decrypt(bytes: Array[Byte], b64secret: String): Array[Byte] = {
    val decoder = cipher(Cipher.DECRYPT_MODE, b64secret)
    decoder.doFinal(bytes)
  }

  def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)

  def generateKey(algorithm: String, size: Int): Array[Byte] = {
    val generator = KeyGenerator.getInstance(algorithm)
    generator.init(size)
    generator.generateKey().getEncoded
  }
}

object DES extends JavaCryptoEncryption("DES")
object AES extends JavaCryptoEncryption("AES")