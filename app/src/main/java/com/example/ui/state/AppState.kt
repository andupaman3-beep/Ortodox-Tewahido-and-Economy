package com.example.ui.state

enum class AppScreen {
    LANDING_HOME,
    JOBS,
    JOB_DETAIL,
    MARKETPLACE,
    PRODUCT_DETAIL,
    B2B,
    B2B_DETAIL,
    SERVICES,
    SERVICE_DETAIL,
    MESSAGES,
    CHAT,
    NOTIFICATIONS,
    PROFILE,
    ADMIN,
    AI_ASSISTANT,
    GLOBAL_SEARCH,
    AUTH,
    CV_BUILDER,
    DOCUMENTS,
    SELLER_DASHBOARD,
    FAVORITES
}

data class FilterState(
    val category: String = "All",
    val location: String = "All",
    val verifiedOnly: Boolean = false,
    val searchQuery: String = "",
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)
