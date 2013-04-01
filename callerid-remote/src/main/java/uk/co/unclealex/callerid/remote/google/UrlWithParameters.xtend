package uk.co.unclealex.callerid.remote.google

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.google.common.collect.Lists
import java.net.URL
import java.util.List
import java.util.Map
import org.eclipse.xtext.xbase.lib.Pair

/**
 * A class that represents a URL with its query parameters.
 */
@Data class UrlWithParameters {
    /**
     * The URL without any parameters.
     */
    var String url

    /**
     * The map of parameters of the url.
     */
    var Map<String, String> parameters = newLinkedHashMap()

    /**
     * Simple constructor.
     */
    protected new(String url, Pair<? extends Object, ? extends Object>... extraParameters) {
        this._url = url
        extraParameters.forEach[_parameters.put(key.toString, value.toString)]
    }

    /**
     * Parse a URL, discovering its parameters.
     */
    def static parse(URL url) {
        val List<String> urlParts = Lists::newArrayList(Splitter::on('?').split(url.toString))
        switch (urlParts.size) {
            case 1:
                new UrlWithParameters(urlParts.get(0))
            case 2: {
                new UrlWithParameters(urlParts.get(0)).withParameters(
                    Splitter::on('&').omitEmptyStrings.withKeyValueSeparator("=").split(urlParts.get(1)))
            }
            default: {
                throw new IllegalArgumentException('''Cannot parse URL «url»''')
            }
        }
    }

    /**
     * Add or update a parameters.
     * @param extraParameters The parameters to add or update.
     * @return this.
     */
    def UrlWithParameters withParameters(Pair<? extends Object, ? extends Object>... extraParameters) {
        extraParameters.forEach [ name, value |
            parameters.put(name.toString, value.toString)
        ]
        this
    }

    /**
     * Add or update a parameters.
     * @param extraParameters The parameters to add or update.
     * @return this.
     */
    def UrlWithParameters withParameters(Map<? extends Object, ? extends Object> extraParameters) {
        extraParameters.forEach [ name, value |
            parameters.put(name.toString, value.toString)
        ]
        this
    }

    /**
     * Convert to a plain URL.
     */
    def URL toURL() {
        new URL(
            if (parameters.empty) {
                url
            } else {
                '''«url»?«Joiner::on('&').withKeyValueSeparator("=").join(parameters)»'''
            })
    }
}
