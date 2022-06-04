package com.dtflys.test.http

import com.dtflys.forest.annotation.Address
import com.dtflys.forest.annotation.Body
import com.dtflys.forest.annotation.Get
import com.dtflys.forest.annotation.Post
import com.dtflys.forest.annotation.Query
import com.dtflys.forest.backend.HttpBackend
import com.dtflys.forest.config.ForestConfiguration
import com.dtflys.forest.mock.MockServerRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class TestKotlinClient(backend: HttpBackend?) : BaseClientTest(backend, configuration) {
    companion object {
        const val EXPECTED = "{\"status\":\"ok\"}"
        @JvmStatic
        private var configuration: ForestConfiguration? = null

        @JvmStatic
        @BeforeClass
        fun prepareClient() {
            configuration = ForestConfiguration.createConfiguration()
        }
    }

    @Rule @JvmField
    val server = MockWebServer()

    var client : Client?

    init {
        configuration?.setVariableValue("port", server.port)
        client = configuration?.client(Client::class.java)
    }


    @Address(host = "127.0.0.1", port = "{port}")
    interface Client {

        @Get("/")
        fun getText() : String

        @Get("/")
        fun getWithQuery(@Query("name") name: String) : String

        @Post("/")
        fun postText(@Body("text") text: String) : String
    }

    @Test
    fun testKotlinGet() {
        server.enqueue(MockResponse().setBody(EXPECTED))
        val result = client?.getText()
        assertThat(result).isNotNull.isEqualTo(EXPECTED)
    }

    @Test
    fun testKotlinGetWithQuery() {
        server.enqueue(MockResponse().setBody(EXPECTED))
        val result = client?.getWithQuery("ok")
        assertThat(result).isNotNull.isEqualTo(EXPECTED)
        MockServerRequest.mockRequest(server)
            .assertPathEquals("/")
            .assertQueryEquals("name", "ok")
    }


    @Test
    fun testKotlinPost() {
        server.enqueue(MockResponse().setBody(EXPECTED))
        val result = client?.postText("ok")
        assertThat(result).isNotNull.isEqualTo(EXPECTED)
        MockServerRequest.mockRequest(server)
            .assertPathEquals("/")
            .assertBodyEquals("text=ok")
    }

}
