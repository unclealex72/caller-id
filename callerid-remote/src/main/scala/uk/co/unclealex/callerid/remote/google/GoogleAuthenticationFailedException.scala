package uk.co.unclealex.callerid.remote.google

/**
 * An exception that is thrown when a user cannot be authenticated by Google.
 */
class GoogleAuthenticationFailedException extends Exception {
    
    new(String message) {
        super(message)
    }
    
    new(String message, Throwable cause) {
        super(message, cause)
    }
}