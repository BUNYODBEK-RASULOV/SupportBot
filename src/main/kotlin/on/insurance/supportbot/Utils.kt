package on.insurance.supportbot

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class JwtUtil {

    private val secretKey = "someKeyWord"


    fun encode(userName: String): String {
        val jwtBuilder: JwtBuilder = Jwts.builder()
        jwtBuilder.setIssuedAt(Date()) // 18:58:00
        jwtBuilder.setExpiration(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 19:58:00
        jwtBuilder.setIssuer("Mazgi production")
        jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey)
        jwtBuilder.claim("username", userName)
        return jwtBuilder.compact()
    }

    fun decode(token: String?): String? {
        val claims: Claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
        return claims.get("name").toString()
    }

}

class MD5Util {
    fun getMd5(input: String): String? {
        return try {
            // Static getInstance method is called with hashing MD5
            val md = MessageDigest.getInstance("MD5")
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            val messageDigest = md.digest(input.toByteArray())
            // Convert byte array into signum representation
            val no = BigInteger(1, messageDigest)
            // Convert message digest into hex value
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } // For specifying wrong message digest algorithms
        catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}
