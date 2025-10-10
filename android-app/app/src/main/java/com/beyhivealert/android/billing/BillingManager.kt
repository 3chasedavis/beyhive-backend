package com.beyhivealert.android.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BillingManager(private val context: Context) : ViewModel() {
    
    private val _isPurchased = MutableStateFlow(false)
    val isPurchased: StateFlow<Boolean> = _isPurchased.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private var billingClient: BillingClient? = null
    private val prefs: SharedPreferences = context.getSharedPreferences("billing_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val NOTIFICATIONS_PRODUCT_ID = "com.chasedavis.beyhivealert.notificationssss"
        private const val NOTIFICATIONS_PURCHASE_KEY = "notifications_purchased"
        
        // Debug mode - set to false for real billing
        private const val DEBUG_MODE = false
    }
    
    init {
        loadPurchaseFromLocal()
        if (DEBUG_MODE) {
            setupDebugMode()
        } else {
            setupBillingClient()
        }
    }
    
    private fun setupDebugMode() {
        println("BillingManager: Running in DEBUG mode - simulating billing")
        _isLoading.value = false
        _errorMessage.value = null
        // In debug mode, we'll simulate the billing flow
    }
    
    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                println("BillingManager: Purchase update - Response: ${billingResult.responseCode}, Message: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    println("BillingManager: Processing ${purchases.size} purchases")
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else {
                    handleBillingError(billingResult)
                }
            }
            .enablePendingPurchases()
            .build()
        
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                println("BillingManager: Billing setup finished - Response: ${billingResult.responseCode}, Message: ${billingResult.debugMessage}")
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        println("BillingManager: Billing client ready, checking existing purchases")
                        // Billing client is ready
                        checkExistingPurchases()
                    }
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                        println("BillingManager: Billing unavailable")
                        _errorMessage.value = "Billing unavailable. Please update Google Play Services."
                        _isLoading.value = false
                    }
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                        println("BillingManager: Billing service unavailable")
                        _errorMessage.value = "Billing service unavailable. Please check your connection."
                        _isLoading.value = false
                    }
                    else -> {
                        println("BillingManager: Billing setup failed - ${billingResult.debugMessage}")
                        // Handle other billing setup failures
                        _errorMessage.value = "Billing setup failed: ${billingResult.debugMessage}"
                        _isLoading.value = false
                    }
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Retry connection
                _errorMessage.value = "Billing service disconnected. Retrying..."
                setupBillingClient()
            }
        })
    }
    
    private fun checkExistingPurchases() {
        println("BillingManager: Checking existing purchases for product: $NOTIFICATIONS_PRODUCT_ID")
        billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()) { billingResult, purchases ->
            println("BillingManager: Query purchases result - Response: ${billingResult.responseCode}, Purchases: ${purchases?.size ?: 0}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    println("BillingManager: Found purchase - State: ${purchase.purchaseState}, Products: ${purchase.products}")
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.products.contains(NOTIFICATIONS_PRODUCT_ID)) {
                            println("BillingManager: Found matching purchase for notifications")
                            _isPurchased.value = true
                            savePurchaseLocally()
                        }
                    }
                }
            } else {
                println("BillingManager: Failed to query purchases - ${billingResult.debugMessage}")
            }
        }
    }
    
    fun loadPurchaseFromLocal() {
        _isPurchased.value = prefs.getBoolean(NOTIFICATIONS_PURCHASE_KEY, false)
    }
    
    private fun savePurchaseLocally() {
        prefs.edit().putBoolean(NOTIFICATIONS_PURCHASE_KEY, true).apply()
    }
    
    fun purchaseNotifications(activity: Activity) {
        viewModelScope.launch {
            println("BillingManager: Starting purchase flow for product: $NOTIFICATIONS_PRODUCT_ID")
            _isLoading.value = true
            _errorMessage.value = null
            
            if (DEBUG_MODE) {
                // Simulate purchase in debug mode
                simulatePurchase()
                return@launch
            }
            
            // Check if billing client is ready
            if (billingClient?.isReady != true) {
                println("BillingManager: Billing client not ready, attempting to reconnect")
                _errorMessage.value = "Billing service not ready. Attempting to reconnect..."
                setupBillingClient()
                _isLoading.value = false
                return@launch
            }
            
            try {
                println("BillingManager: Getting product details")
                val productDetails = getProductDetails()
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                
                println("BillingManager: Launching billing flow")
                val billingResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
                
                if (billingResult?.responseCode != BillingClient.BillingResponseCode.OK) {
                    println("BillingManager: Failed to launch billing flow - ${billingResult?.responseCode}")
                    billingResult?.let { handleBillingError(it) }
                } else {
                    println("BillingManager: Billing flow launched successfully")
                }
                
            } catch (e: Exception) {
                println("BillingManager: Exception during purchase - ${e.message}")
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun simulatePurchase() {
        println("BillingManager: Simulating purchase in DEBUG mode")
        // Simulate a delay
        kotlinx.coroutines.delay(2000)
        _isPurchased.value = true
        savePurchaseLocally()
        _isLoading.value = false
        _errorMessage.value = null
        println("BillingManager: DEBUG purchase completed successfully")
    }
    
    fun retryPurchase(activity: Activity) {
        purchaseNotifications(activity)
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    private suspend fun getProductDetails(): ProductDetails = suspendCancellableCoroutine { continuation ->
        println("BillingManager: Querying product details for: $NOTIFICATIONS_PRODUCT_ID")
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(NOTIFICATIONS_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            println("BillingManager: Product details query result - Response: ${billingResult.responseCode}, Products: ${productDetailsList?.size ?: 0}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull()
                if (productDetails != null) {
                    println("BillingManager: Product details found - ${productDetails.name}, Price: ${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}")
                    continuation.resume(productDetails)
                } else {
                    println("BillingManager: Product not found in store")
                    continuation.resumeWithException(Exception("Product not found"))
                }
            } else {
                println("BillingManager: Failed to query product details - ${billingResult.debugMessage}")
                continuation.resumeWithException(Exception("Failed to query product details: ${billingResult.debugMessage}"))
            }
        }
    }
    
    private fun handleBillingError(billingResult: BillingResult) {
        _isLoading.value = false
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                println("BillingManager: Purchase cancelled by user")
                _errorMessage.value = "Purchase cancelled"
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                println("BillingManager: Item already owned")
                _errorMessage.value = "You already own this item"
                // Check existing purchases to verify
                checkExistingPurchases()
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                println("BillingManager: Item unavailable")
                _errorMessage.value = "This item is not available for purchase"
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                println("BillingManager: Billing unavailable")
                _errorMessage.value = "Billing unavailable. Please update Google Play Services."
            }
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                println("BillingManager: Service unavailable")
                _errorMessage.value = "Billing service unavailable. Please check your connection and try again."
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                println("BillingManager: Developer error")
                _errorMessage.value = "Configuration error. Please contact support."
            }
            BillingClient.BillingResponseCode.ERROR -> {
                println("BillingManager: General error")
                _errorMessage.value = "An error occurred. Please try again."
            }
            else -> {
                println("BillingManager: Unknown error - ${billingResult.debugMessage}")
                _errorMessage.value = "Purchase failed: ${billingResult.debugMessage}"
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        println("BillingManager: Handling purchase - State: ${purchase.purchaseState}, Products: ${purchase.products}, Acknowledged: ${purchase.isAcknowledged}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                println("BillingManager: Acknowledging purchase")
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    println("BillingManager: Acknowledge purchase result - ${billingResult.responseCode}")
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        println("BillingManager: Purchase acknowledged successfully")
                        _isPurchased.value = true
                        savePurchaseLocally()
                        _isLoading.value = false
                        _errorMessage.value = null
                    } else {
                        println("BillingManager: Failed to acknowledge purchase - ${billingResult.debugMessage}")
                        _errorMessage.value = "Failed to complete purchase. Please try again."
                    }
                }
            } else {
                println("BillingManager: Purchase already acknowledged")
                _isPurchased.value = true
                savePurchaseLocally()
                _isLoading.value = false
                _errorMessage.value = null
            }
        }
    }
    
    
    override fun onCleared() {
        super.onCleared()
        billingClient?.endConnection()
    }
}