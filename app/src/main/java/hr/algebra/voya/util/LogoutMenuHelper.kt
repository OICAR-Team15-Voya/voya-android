package hr.algebra.voya.util

object LogoutMenuHelper {

    private const val LOGOUT_MENU_RESOURCE_NAME = "menu_logout"
    private const val RESOURCE_TYPE_ID = "id"

    fun resolveLogoutMenuId(
        resourceIdResolver: (String, String, String) -> Int,
        packageName: String
    ): Int {
        return resourceIdResolver(LOGOUT_MENU_RESOURCE_NAME, RESOURCE_TYPE_ID, packageName)
    }

    fun isLogoutSelection(itemId: Int, logoutMenuId: Int): Boolean {
        return logoutMenuId != 0 && itemId == logoutMenuId
    }
}

