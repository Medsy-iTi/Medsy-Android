package com.medsy.data.remote.api


import com.medsy.data.cart.remote.AddCartItemRequestDto
import com.medsy.data.cart.remote.CartDto
import com.medsy.data.cart.remote.MedicineRequestDto
import com.medsy.data.cart.remote.ProductsRequestDto
import com.medsy.data.offers.remote.OffersPageDto
import com.medsy.data.productdetails.remote.ProductDetailsDto
import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.data.profile.remote.dto.UpdateCustomerProfileRequestDto
import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.dtos.products.ProductsDataDto
import com.medsy.data.remote.network.ApiResponse
import com.medsy.data.search.remote.ProductsPageDto
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.data.prescription.remote.AiInterceptor
import com.medsy.data.prescription.remote.dto.AnalyzedMedicineImageDto
import com.medsy.data.prescription.remote.dto.PrescriptionAnalysisDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Query


interface ApiService {
    @Multipart
    @Headers("${AiInterceptor.AI_KEY_FLAG}: true")
    @POST("api/v1/prescriptions/analyze")
    suspend fun analyzePrescription(
        @Part image: MultipartBody.Part,
    ): Response<ApiResponse<PrescriptionAnalysisDto>>

    @Multipart
    @Headers("${AiInterceptor.AI_KEY_FLAG}: true")
    @POST("api/v1/products/analyze-image")
    suspend fun analyzeMedicineImage(
        @Part image: MultipartBody.Part,
    ): Response<ApiResponse<List<AnalyzedMedicineImageDto>>>

    @GET("api/v1/cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("api/v1/cart/items")
    suspend fun addCartItem(
        @Body request: AddCartItemRequestDto,
    ): Response<ApiResponse<CartDto>>

    @PATCH("api/v1/cart/items/{cartItemId}")
    suspend fun setCartItemQuantity(
        @Path("cartItemId") cartItemId: Long,
        @Body quantity: Int,
    ): Response<ApiResponse<CartDto>>

    @DELETE("api/v1/cart/items/{cartItemId}")
    suspend fun removeCartItem(
        @Path("cartItemId") cartItemId: Long,
    ): Response<ApiResponse<CartDto>>

    @DELETE("api/v1/cart")
    suspend fun clearCart(): Response<ApiResponse<Any>>

    @Multipart
    @POST("api/v1/requests")
    suspend fun submitProductsRequest(
        @Part("request") request: ProductsRequestDto,
        @Part prescription: MultipartBody.Part? = null,
    ): Response<ApiResponse<MedicineRequestDto>>

    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<ApiResponse<CategoriesDataDto>>

    @GET("api/v1/products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<ApiResponse<ProductsDataDto>>


    @GET("api/v1/customers/me")
    suspend fun getCurrentCustomer(): Response<ApiResponse<CustomerDto>>

    @PUT("api/v1/customers/me")
    suspend fun updateCurrentCustomer(
        @Body request: UpdateCustomerProfileRequestDto,
    ): Response<ApiResponse<CustomerDto>>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>?,
        @Query("categoryId") categoryId: Int? = null
    ): Response<ApiResponse<ProductsPageDto>>

    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>?,
    ): Response<ApiResponse<ProductsPageDto>>
    @GET("api/v1/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
        @Query("lang") lang: String = "en",
    ): Response<ApiResponse<ProductDetailsDto>>

    @GET("api/v1/offers/requests/{requestId}")
    suspend fun getOffersForRequest(
        @Path("requestId") requestId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<ApiResponse<OffersPageDto>>

    @POST("api/v1/requests/{requestId}/confirm")
    suspend fun confirmRequest(
        @Path("requestId") requestId: Long,
        @retrofit2.http.Body request: com.medsy.data.offers.remote.ConfirmRequestDto
    ): Response<ApiResponse<Any>>

    @GET("api/v1/requests/{id}")
    suspend fun getRequestById(
        @Path("id") id: Long,
    ): Response<ApiResponse<MedicineRequestDto>>
}